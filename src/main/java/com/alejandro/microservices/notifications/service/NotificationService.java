package com.alejandro.microservices.notifications.service;

import com.alejandro.microservices.notifications.model.Notification;
import com.alejandro.microservices.notifications.model.User;
import com.alejandro.microservices.notifications.repository.NotificationRepository;
import com.alejandro.microservices.notifications.repository.UserRepository;
import com.alejandro.microservices.notifications.metrics.NotificationMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationMetrics metrics;

    // Redis Publisher es opcional
    @Autowired(required = false)
    private RedisPublisher redisPublisher;

    // 📩 Enviar una notificación con Redis Pub/Sub distribuido (opcional)
    public Notification sendNotification(String username, String message) {
        // Iniciar timer para medir latencia
        var sendTimer = metrics.startNotificationSendTimer();

        try {
            User recipient = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Notification notification = Notification.builder()
                    .username(username)
                    .message(message)
                    .recipient(recipient)
                    .timestamp(LocalDateTime.now())
                    .read(false)
                    .build();

            // Medir tiempo de operación en base de datos
            var dbTimer = metrics.startDatabaseQueryTimer();
            Notification saved = notificationRepository.save(notification);
            metrics.recordDatabaseQueryTime(dbTimer);

            // 🔥 Contabilizar en métricas avanzadas
            metrics.incrementNotificationsSent(username);
            metrics.incrementUnreadNotifications();

            // Registrar tamaño del mensaje
            metrics.recordNotificationMessageSize(message.getBytes().length);

            // 🚀 Si Redis está disponible, publicar. Si no, usar WebSocket directo
            if (redisPublisher != null) {
                var redisTimer = metrics.startRedisOperationTimer();
                redisPublisher.publish(saved);
                metrics.recordRedisOperationTime(redisTimer);
                metrics.incrementRedisPublishEvents();
            } else {
                // Fallback: envío directo por WebSocket
                var wsTimer = metrics.startWebsocketBroadcastTimer();
                messagingTemplate.convertAndSend("/topic/notifications/" + username, saved);
                metrics.recordWebsocketBroadcastTime(wsTimer);
            }

            return saved;

        } catch (Exception e) {
            metrics.incrementFailedNotifications();
            throw e;
        } finally {
            metrics.recordNotificationSendTime(sendTimer);
        }
    }

    // 📋 Listar todas las notificaciones de un usuario (ordenadas por timestamp)
    public List<Notification> getAllNotifications(String username) {
        var dbTimer = metrics.startDatabaseQueryTimer();
        try {
            return notificationRepository.findByUsernameOrderByTimestampDesc(username);
        } finally {
            metrics.recordDatabaseQueryTime(dbTimer);
        }
    }

    // 📬 Listar solo notificaciones no leídas (ordenadas por timestamp)
    public List<Notification> getUnreadNotifications(String username) {
        var dbTimer = metrics.startDatabaseQueryTimer();
        try {
            return notificationRepository.findByUsernameAndReadFalseOrderByTimestampDesc(username);
        } finally {
            metrics.recordDatabaseQueryTime(dbTimer);
        }
    }

    // ✅ Marcar una notificación específica como leída
    @Transactional
    public Notification markAsRead(Long notificationId) {
        var dbTimer = metrics.startDatabaseQueryTimer();

        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

            if (!notification.isRead()) {
                notificationRepository.markAsReadById(notificationId);
                notification.setRead(true);

                // 🔥 Contabilizar en métricas solo si cambió de estado
                metrics.incrementNotificationsRead();
            }

            return notification;
        } finally {
            metrics.recordDatabaseQueryTime(dbTimer);
        }
    }

    // ✅ Marcar todas las notificaciones como leídas
    @Transactional
    public void markAllAsRead(String username) {
        var dbTimer = metrics.startDatabaseQueryTimer();

        try {
            // Contar cuántas se van a marcar para métricas
            long unreadCount = notificationRepository.countUnreadByUsername(username);

            if (unreadCount > 0) {
                notificationRepository.markAllAsRead(username);
                // 🔥 Contabilizar en métricas
                metrics.markAllAsRead(username, (int) unreadCount);
            }
        } finally {
            metrics.recordDatabaseQueryTime(dbTimer);
        }
    }

    // 📊 Contar notificaciones no leídas (con actualización de métricas)
    public long countUnreadNotifications(String username) {
        var dbTimer = metrics.startDatabaseQueryTimer();

        try {
            long count = notificationRepository.countUnreadByUsername(username);

            // Actualizar métricas globales
            updateGlobalMetrics();

            return count;
        } finally {
            metrics.recordDatabaseQueryTime(dbTimer);
        }
    }

    // 📊 Método para actualizar métricas globales periódicamente
    public void updateGlobalMetrics() {
        var dbTimer = metrics.startDatabaseQueryTimer();

        try {
            long totalNotifications = notificationRepository.count();
            long totalUnread = notificationRepository.countByReadFalse();
            long totalUsers = userRepository.count();

            metrics.setTotalActiveNotifications(totalNotifications);
            metrics.setTotalUnreadNotifications(totalUnread);
            metrics.setTotalUsers(totalUsers);
        } finally {
            metrics.recordDatabaseQueryTime(dbTimer);
        }
    }

    // Métodos existentes para compatibilidad con User
    public List<Notification> getNotifications(String username) {
        User recipient = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return notificationRepository.findByRecipient(recipient);
    }
}
