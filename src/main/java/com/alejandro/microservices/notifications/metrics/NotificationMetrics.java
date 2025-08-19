package com.alejandro.microservices.notifications.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class NotificationMetrics {

    private final MeterRegistry meterRegistry;

    // Contadores para diferentes tipos de eventos
    private final Counter sentNotifications;
    private final Counter readNotifications;
    private final Counter unreadNotifications;
    private final Counter markAllReadOperations;

    // Contadores atómicos para gauges (registrados de forma más simple)
    private final AtomicLong totalActiveNotifications = new AtomicLong(0);
    private final AtomicLong totalUnreadNotifications = new AtomicLong(0);

    public NotificationMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Inicializar contadores
        this.sentNotifications = Counter.builder("notifications.sent")
                .description("Número total de notificaciones enviadas")
                .tag("type", "notification")
                .register(meterRegistry);

        this.readNotifications = Counter.builder("notifications.read")
                .description("Número total de notificaciones marcadas como leídas")
                .tag("type", "notification")
                .register(meterRegistry);

        this.unreadNotifications = Counter.builder("notifications.unread.created")
                .description("Número total de notificaciones no leídas creadas")
                .tag("type", "notification")
                .register(meterRegistry);

        this.markAllReadOperations = Counter.builder("notifications.mark_all_read")
                .description("Número de operaciones de marcar todas como leídas")
                .tag("type", "operation")
                .register(meterRegistry);

        // Registrar gauges usando una sintaxis más simple
        meterRegistry.gauge("notifications.active.total", totalActiveNotifications);
        meterRegistry.gauge("notifications.unread.total", totalUnreadNotifications);
    }

    // 🔥 Contabilizar notificación enviada
    public void incrementSentNotifications() {
        sentNotifications.increment();
        totalActiveNotifications.incrementAndGet();
        totalUnreadNotifications.incrementAndGet();
    }

    // 🔥 Contabilizar notificación leída
    public void incrementReadNotifications() {
        readNotifications.increment();
        totalUnreadNotifications.decrementAndGet();
    }

    // 🔥 Contabilizar operación de marcar todas como leídas
    public void incrementMarkAllReadOperations(long notificationsMarked) {
        markAllReadOperations.increment();
        totalUnreadNotifications.addAndGet(-notificationsMarked);
    }

    // 📊 Actualizar contadores de gauges (para sincronización con BD)
    public void updateActiveNotificationsCount(long count) {
        totalActiveNotifications.set(count);
    }

    public void updateUnreadNotificationsCount(long count) {
        totalUnreadNotifications.set(count);
    }

    // Getters para acceso directo a los valores
    public long getActiveNotificationsCount() {
        return totalActiveNotifications.get();
    }

    public long getUnreadNotificationsCount() {
        return totalUnreadNotifications.get();
    }
}
