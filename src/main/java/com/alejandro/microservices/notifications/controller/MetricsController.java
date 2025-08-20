package com.alejandro.microservices.notifications.controller;

import com.alejandro.microservices.notifications.metrics.NotificationMetrics;
import com.alejandro.microservices.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final NotificationMetrics notificationMetrics;
    private final NotificationService notificationService;

    /**
     * 📊 Endpoint para obtener estadísticas en tiempo real
     * Útil para dashboards personalizados o integración con otros sistemas
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        // Forzar actualización de métricas globales
        notificationService.updateGlobalMetrics();

        Map<String, Object> stats = new HashMap<>();
        stats.put("timestamp", System.currentTimeMillis());
        stats.put("metrics", notificationMetrics.getCurrentStats());

        return ResponseEntity.ok(stats);
    }

    /**
     * 📈 Endpoint para obtener estadísticas por usuario
     */
    @GetMapping("/stats/{username}")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String username) {
        Map<String, Object> userStats = new HashMap<>();

        long unreadCount = notificationService.countUnreadNotifications(username);
        long totalCount = notificationService.getAllNotifications(username).size();

        userStats.put("username", username);
        userStats.put("totalNotifications", totalCount);
        userStats.put("unreadNotifications", unreadCount);
        userStats.put("readNotifications", totalCount - unreadCount);
        userStats.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(userStats);
    }

    /**
     * 🔄 Endpoint para forzar sincronización de métricas
     * Útil para debugging o mantenimiento
     */
    @PostMapping("/sync")
    public ResponseEntity<Map<String, String>> syncMetrics() {
        notificationService.updateGlobalMetrics();

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Métricas sincronizadas correctamente");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));

        return ResponseEntity.ok(response);
    }

    /**
     * 🏥 Health check avanzado con métricas
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        Map<String, Object> stats = notificationMetrics.getCurrentStats();

        // Determinar estado de salud basado en métricas
        boolean isHealthy = true;
        String status = "UP";

        // Verificaciones de salud básicas
        Long activeConnections = (Long) stats.get("activeWebsocketConnections");
        Long unreadNotifications = (Long) stats.get("totalUnreadNotifications");

        if (activeConnections == null || activeConnections < 0) {
            isHealthy = false;
            status = "DOWN";
        }

        health.put("status", status);
        health.put("isHealthy", isHealthy);
        health.put("metrics", stats);
        health.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(health);
    }
}
