package com.alejandro.microservices.notifications.controller;

import com.alejandro.microservices.notifications.metrics.NotificationMetrics;
import com.alejandro.microservices.notifications.repository.NotificationRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Component
@Endpoint(id = "notifications-metrics")
@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
@Tag(name = "Métricas", description = "Endpoints de métricas personalizadas para notificaciones")
public class MetricsController {

    private final MeterRegistry meterRegistry;
    private final NotificationRepository notificationRepository;

    @ReadOperation
    @GetMapping("/notifications")
    @Operation(
        summary = "Obtener métricas completas de notificaciones",
        description = "Devuelve un resumen completo de todas las métricas del sistema de notificaciones"
    )
    public ResponseEntity<Map<String, Object>> getNotificationMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // Obtener métricas de contadores
        metrics.put("total_sent", getCounterValue("notifications.sent"));
        metrics.put("total_read", getCounterValue("notifications.read"));
        metrics.put("total_unread_created", getCounterValue("notifications.unread.created"));
        metrics.put("mark_all_read_operations", getCounterValue("notifications.mark_all_read"));

        // Obtener métricas de gauges
        metrics.put("active_notifications", getGaugeValue("notifications.active.total"));
        metrics.put("unread_notifications", getGaugeValue("notifications.unread.total"));

        // Métricas de base de datos en tiempo real
        long totalInDB = notificationRepository.count();
        long unreadInDB = notificationRepository.countUnreadByUsername("");

        metrics.put("database_total", totalInDB);
        metrics.put("database_unread", unreadInDB);
        metrics.put("database_read", totalInDB - unreadInDB);

        // Calcular porcentajes
        if (totalInDB > 0) {
            metrics.put("read_percentage", Math.round(((double)(totalInDB - unreadInDB) / totalInDB) * 100.0));
            metrics.put("unread_percentage", Math.round(((double)unreadInDB / totalInDB) * 100.0));
        } else {
            metrics.put("read_percentage", 0);
            metrics.put("unread_percentage", 0);
        }

        // Metadata
        metrics.put("timestamp", System.currentTimeMillis());
        metrics.put("status", "active");

        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/notifications/summary")
    @Operation(
        summary = "Resumen ejecutivo de métricas",
        description = "Devuelve un resumen ejecutivo de las métricas más importantes"
    )
    public ResponseEntity<Map<String, Object>> getMetricsSummary() {
        Map<String, Object> summary = new HashMap<>();

        long totalSent = (long) getCounterValue("notifications.sent");
        long totalRead = (long) getCounterValue("notifications.read");
        long activeNotifications = (long) getGaugeValue("notifications.active.total");
        long unreadNotifications = (long) getGaugeValue("notifications.unread.total");

        summary.put("performance", Map.of(
            "total_notifications_sent", totalSent,
            "total_notifications_read", totalRead,
            "read_rate_percentage", totalSent > 0 ? Math.round(((double)totalRead / totalSent) * 100.0) : 0
        ));

        summary.put("current_state", Map.of(
            "active_notifications", activeNotifications,
            "unread_notifications", unreadNotifications,
            "read_notifications", activeNotifications - unreadNotifications
        ));

        summary.put("health_indicators", Map.of(
            "system_active", activeNotifications >= 0,
            "notifications_flowing", totalSent > 0,
            "users_engaging", totalRead > 0
        ));

        return ResponseEntity.ok(summary);
    }

    private double getCounterValue(String meterName) {
        Counter counter = meterRegistry.find(meterName).counter();
        return counter != null ? counter.count() : 0.0;
    }

    private double getGaugeValue(String meterName) {
        Gauge gauge = meterRegistry.find(meterName).gauge();
        return gauge != null ? gauge.value() : 0.0;
    }
}
