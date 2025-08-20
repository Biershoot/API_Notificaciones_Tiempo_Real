package com.alejandro.microservices.notifications.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Tag;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Componente para la gestión de métricas de la aplicación de notificaciones.
 * Proporciona contadores, temporizadores y resúmenes para monitorear y analizar
 * el comportamiento de la aplicación en producción.
 */
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
    private final Counter cleanupCounter;
    private final Counter queryCounter;
    private final Counter bulkReadCounter;

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

    /**
     * Constructor que inicializa todas las métricas y las registra en el MeterRegistry.
     *
     * @param meterRegistry El registro de métricas de Spring
     */
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

        this.cleanupCounter = Counter.builder("notifications_cleanup_total")
                .description("Total notifications cleaned up")
                .tag("service", "notification-api")
                .register(meterRegistry);

        this.queryCounter = Counter.builder("notifications_query_total")
                .description("Total notification queries executed")
                .tag("service", "notification-api")
                .register(meterRegistry);

        this.bulkReadCounter = Counter.builder("notifications_bulk_read_total")
                .description("Total notifications marked as read in bulk operations")
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

        // Distribution summary para tamaños de mensaje
        this.notificationMessageSize = DistributionSummary.builder("notification_message_size_bytes")
                .description("Size distribution of notification messages")
                .tag("service", "notification-api")
                .baseUnit("bytes")
                .register(meterRegistry);

        // Registrar gauges
        meterRegistry.gauge("notifications_active_total", List.of(Tag.of("service", "notification-api")), totalActiveNotifications);
        meterRegistry.gauge("notifications_unread_total", List.of(Tag.of("service", "notification-api")), totalUnreadNotifications);
        meterRegistry.gauge("websocket_connections_active", List.of(Tag.of("service", "notification-api")), activeWebsocketConnections);
        meterRegistry.gauge("users_total", List.of(Tag.of("service", "notification-api")), totalUsers);
    }

    // ===== MÉTODOS PARA INCREMENTAR CONTADORES =====

    /**
     * Incrementa el contador de notificaciones enviadas, segmentado por tipo y prioridad.
     */
    public void incrementSentCounter(String type, String priority) {
        sentNotifications.increment();

        // Contadores específicos por tipo y prioridad
        meterRegistry.counter("notifications_sent_by_type", "type", type).increment();
        meterRegistry.counter("notifications_sent_by_priority", "priority", priority).increment();
    }

    /**
     * Incrementa el contador de notificaciones enviadas para un usuario específico.
     */
    public void incrementNotificationsSent(String username) {
        notificationsByUser.computeIfAbsent(username, k -> new AtomicLong(0)).incrementAndGet();
    }

    /**
     * Incrementa el contador de notificaciones leídas, segmentado por tipo.
     */
    public void incrementReadCounter(String type) {
        readNotifications.increment();
        meterRegistry.counter("notifications_read_by_type", "type", type).increment();
    }

    /**
     * Incrementa el contador de notificaciones no leídas.
     */
    public void incrementUnreadNotifications() {
        unreadNotifications.increment();
        totalUnreadNotifications.incrementAndGet();
    }

    /**
     * Incrementa el contador de notificaciones fallidas.
     */
    public void incrementFailedNotifications() {
        failedNotifications.increment();
    }

    /**
     * Incrementa el contador de conexiones WebSocket.
     */
    public void incrementWebSocketConnections() {
        websocketConnections.increment();
        activeWebsocketConnections.incrementAndGet();
    }

    /**
     * Decrementa el contador de conexiones WebSocket activas.
     */
    public void decrementActiveWebSocketConnections() {
        activeWebsocketConnections.decrementAndGet();
    }

    /**
     * Incrementa el contador de notificaciones eliminadas.
     */
    public void incrementCleanupCounter(int count) {
        cleanupCounter.increment(count);
        totalActiveNotifications.addAndGet(-count);
    }

    /**
     * Incrementa el contador de consultas de notificaciones.
     */
    public void incrementQueryCounter() {
        queryCounter.increment();
    }

    /**
     * Incrementa el contador de notificaciones marcadas como leídas en masa.
     */
    public void incrementBulkReadCounter(int count) {
        bulkReadCounter.increment(count);
        totalUnreadNotifications.addAndGet(-count);
    }

    // ===== MÉTODOS PARA MEDIR TIEMPOS =====

    /**
     * Inicia un temporizador para medir el tiempo de envío de notificaciones.
     * @return Un objeto Sample que debe detenerse al finalizar la operación
     */
    public Timer.Sample startNotificationSendTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * Registra el tiempo transcurrido para el envío de una notificación.
     */
    public void recordNotificationSendTime(Timer.Sample sample) {
        sample.stop(notificationSendTime);
    }

    /**
     * Inicia un temporizador para medir el tiempo de consultas a la base de datos.
     */
    public Timer.Sample startDatabaseQueryTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * Registra el tiempo transcurrido para una consulta a la base de datos.
     */
    public void recordDatabaseQueryTime(Timer.Sample sample) {
        sample.stop(databaseQueryTime);
    }

    /**
     * Registra el tamaño de un mensaje de notificación.
     */
    public void recordNotificationMessageSize(long bytes) {
        notificationMessageSize.record(bytes);
    }

    // ===== MÉTODOS PARA ESTADÍSTICAS =====

    /**
     * Actualiza el número total de usuarios.
     */
    public void setTotalUsers(long count) {
        totalUsers.set(count);
    }

    /**
     * Obtiene estadísticas de notificaciones por usuario.
     */
    public Map<String, Long> getNotificationsByUser() {
        Map<String, Long> result = new ConcurrentHashMap<>();
        notificationsByUser.forEach((user, count) -> result.put(user, count.get()));
        return result;
    }

    /**
     * Obtiene estadísticas actuales del sistema.
     */
    public Map<String, Object> getCurrentStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        
        stats.put("totalUsers", totalUsers.get());
        stats.put("totalActiveNotifications", totalActiveNotifications.get());
        stats.put("totalUnreadNotifications", totalUnreadNotifications.get());
        stats.put("activeWebsocketConnections", activeWebsocketConnections.get());
        
        stats.put("sentNotifications", sentNotifications.count());
        stats.put("readNotifications", readNotifications.count());
        stats.put("unreadNotifications", unreadNotifications.count());
        stats.put("failedNotifications", failedNotifications.count());
        stats.put("websocketConnections", websocketConnections.count());
        stats.put("redisPublishEvents", redisPublishEvents.count());
        stats.put("cleanupCounter", cleanupCounter.count());
        stats.put("queryCounter", queryCounter.count());
        stats.put("bulkReadCounter", bulkReadCounter.count());
        
        return stats;
    }
}
