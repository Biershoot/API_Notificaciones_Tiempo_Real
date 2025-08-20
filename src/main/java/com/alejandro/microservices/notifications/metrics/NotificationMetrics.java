package com.alejandro.microservices.notifications.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.DistributionSummary;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class NotificationMetrics {

    private final MeterRegistry meterRegistry;

    // Contadores avanzados
    private final Counter sentNotifications;
    private final Counter readNotifications;
    private final Counter unreadNotifications;
    private final Counter markAllReadOperations;
    private final Counter failedNotifications;
    private final Counter websocketConnections;
    private final Counter redisPublishEvents;

    // Timers para medir latencia
    private final Timer notificationSendTime;
    private final Timer databaseQueryTime;
    private final Timer redisOperationTime;
    private final Timer websocketBroadcastTime;

    // Distribution Summary para tamaños de mensaje
    private final DistributionSummary notificationMessageSize;

    // Gauges para estado en tiempo real
    private final AtomicLong totalActiveNotifications = new AtomicLong(0);
    private final AtomicLong totalUnreadNotifications = new AtomicLong(0);
    private final AtomicLong activeWebsocketConnections = new AtomicLong(0);
    private final AtomicLong totalUsers = new AtomicLong(0);

    // Map para tracking por usuario
    private final Map<String, AtomicLong> notificationsByUser = new ConcurrentHashMap<>();

    public NotificationMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Contadores con tags más detallados
        this.sentNotifications = Counter.builder("notifications_sent_total")
                .description("Total number of notifications sent")
                .tag("service", "notification-api")
                .register(meterRegistry);

        this.readNotifications = Counter.builder("notifications_read_total")
                .description("Total number of notifications marked as read")
                .tag("service", "notification-api")
                .register(meterRegistry);

        this.unreadNotifications = Counter.builder("notifications_unread_created_total")
                .description("Total number of unread notifications created")
                .tag("service", "notification-api")
                .register(meterRegistry);

        this.markAllReadOperations = Counter.builder("notifications_mark_all_read_total")
                .description("Total mark-all-as-read operations")
                .tag("service", "notification-api")
                .register(meterRegistry);

        this.failedNotifications = Counter.builder("notifications_failed_total")
                .description("Total number of failed notification operations")
                .tag("service", "notification-api")
                .register(meterRegistry);

        this.websocketConnections = Counter.builder("websocket_connections_total")
                .description("Total WebSocket connections established")
                .tag("service", "notification-api")
                .register(meterRegistry);

        this.redisPublishEvents = Counter.builder("redis_publish_events_total")
                .description("Total Redis pub/sub events published")
                .tag("service", "notification-api")
                .register(meterRegistry);

        // Timers para latencia
        this.notificationSendTime = Timer.builder("notification_send_duration_seconds")
                .description("Time taken to send notifications")
                .tag("service", "notification-api")
                .register(meterRegistry);

        this.databaseQueryTime = Timer.builder("database_query_duration_seconds")
                .description("Time taken for database queries")
                .tag("service", "notification-api")
                .register(meterRegistry);

        this.redisOperationTime = Timer.builder("redis_operation_duration_seconds")
                .description("Time taken for Redis operations")
                .tag("service", "notification-api")
                .register(meterRegistry);

        this.websocketBroadcastTime = Timer.builder("websocket_broadcast_duration_seconds")
                .description("Time taken to broadcast via WebSocket")
                .tag("service", "notification-api")
                .register(meterRegistry);

        // Distribution Summary para tamaños
        this.notificationMessageSize = DistributionSummary.builder("notification_message_size_bytes")
                .description("Size of notification messages in bytes")
                .tag("service", "notification-api")
                .register(meterRegistry);

        // Gauges para estado en tiempo real
        Gauge.builder("notifications_active_total")
                .description("Current number of active notifications")
                .tag("service", "notification-api")
                .register(meterRegistry, this, metrics -> metrics.totalActiveNotifications.get());

        Gauge.builder("notifications_unread_current")
                .description("Current number of unread notifications")
                .tag("service", "notification-api")
                .register(meterRegistry, this, metrics -> metrics.totalUnreadNotifications.get());

        Gauge.builder("websocket_connections_active")
                .description("Current number of active WebSocket connections")
                .tag("service", "notification-api")
                .register(meterRegistry, this, metrics -> metrics.activeWebsocketConnections.get());

        Gauge.builder("users_total")
                .description("Total number of users in system")
                .tag("service", "notification-api")
                .register(meterRegistry, this, metrics -> metrics.totalUsers.get());
    }

    // Métodos para incrementar contadores
    public void incrementNotificationsSent() {
        sentNotifications.increment();
        totalActiveNotifications.incrementAndGet();
    }

    public void incrementNotificationsSent(String username) {
        incrementNotificationsSent();
        notificationsByUser.computeIfAbsent(username, k ->
            meterRegistry.gauge("notifications_by_user", "username", username, new AtomicLong(0))
        ).incrementAndGet();
    }

    public void incrementNotificationsRead() {
        readNotifications.increment();
        totalUnreadNotifications.decrementAndGet();
    }

    public void incrementUnreadNotifications() {
        unreadNotifications.increment();
        totalUnreadNotifications.incrementAndGet();
    }

    public void incrementMarkAllReadOperations() {
        markAllReadOperations.increment();
    }

    public void incrementFailedNotifications() {
        failedNotifications.increment();
    }

    public void incrementWebsocketConnections() {
        websocketConnections.increment();
        activeWebsocketConnections.incrementAndGet();
    }

    public void decrementWebsocketConnections() {
        activeWebsocketConnections.decrementAndGet();
    }

    public void incrementRedisPublishEvents() {
        redisPublishEvents.increment();
    }

    // Métodos para timers
    public Timer.Sample startNotificationSendTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordNotificationSendTime(Timer.Sample sample) {
        sample.stop(notificationSendTime);
    }

    public Timer.Sample startDatabaseQueryTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordDatabaseQueryTime(Timer.Sample sample) {
        sample.stop(databaseQueryTime);
    }

    public Timer.Sample startRedisOperationTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordRedisOperationTime(Timer.Sample sample) {
        sample.stop(redisOperationTime);
    }

    public Timer.Sample startWebsocketBroadcastTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordWebsocketBroadcastTime(Timer.Sample sample) {
        sample.stop(websocketBroadcastTime);
    }

    // Métodos para distribution summary
    public void recordNotificationMessageSize(int sizeInBytes) {
        notificationMessageSize.record(sizeInBytes);
    }

    // Métodos para actualizar gauges
    public void setTotalActiveNotifications(long count) {
        totalActiveNotifications.set(count);
    }

    public void setTotalUnreadNotifications(long count) {
        totalUnreadNotifications.set(count);
    }

    public void setTotalUsers(long count) {
        totalUsers.set(count);
    }

    public void markAllAsRead(String username, int count) {
        incrementMarkAllReadOperations();
        totalUnreadNotifications.addAndGet(-count);
    }

    // Método para obtener estadísticas actuales
    public Map<String, Object> getCurrentStats() {
        return Map.of(
            "totalActiveNotifications", totalActiveNotifications.get(),
            "totalUnreadNotifications", totalUnreadNotifications.get(),
            "activeWebsocketConnections", activeWebsocketConnections.get(),
            "totalUsers", totalUsers.get()
        );
    }
}
