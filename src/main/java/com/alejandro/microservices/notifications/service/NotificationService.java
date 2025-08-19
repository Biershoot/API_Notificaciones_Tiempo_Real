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
        User recipient = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Notification notification = Notification.builder()
                .username(username)
                .message(message)
                .recipient(recipient)
                .timestamp(LocalDateTime.now())
                .read(false)
                .build();

        Notification saved = notificationRepository.save(notification);

        // 🔥 Contabilizar en métricas
        metrics.incrementSentNotifications();

        // 🚀 Si Redis está disponible, publicar. Si no, usar WebSocket directo
        if (redisPublisher != null) {
            redisPublisher.publish(saved);
        } else {
            // Fallback: envío directo por WebSocket
            messagingTemplate.convertAndSend("/topic/notifications/" + username, saved);
        }

        return saved;
    }

    // 📋 Listar todas las notificaciones de un usuario (ordenadas por timestamp)
    public List<Notification> getAllNotifications(String username) {
        return notificationRepository.findByUsernameOrderByTimestampDesc(username);
    }

    // 📬 Listar solo notificaciones no leídas (ordenadas por timestamp)
    public List<Notification> getUnreadNotifications(String username) {
        return notificationRepository.findByUsernameAndReadFalseOrderByTimestampDesc(username);
    }

    // ✅ Marcar una notificación específica como leída
    @Transactional
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        if (!notification.isRead()) {
            notificationRepository.markAsReadById(notificationId);
            notification.setRead(true);

            // 🔥 Contabilizar en métricas solo si cambió de estado
            metrics.incrementReadNotifications();
        }

        return notification;
    }

    // ✅ Marcar todas las notificaciones como leídas
    @Transactional
    public void markAllAsRead(String username) {
        // Contar cuántas se van a marcar para métricas
        long unreadCount = notificationRepository.countUnreadByUsername(username);

        notificationRepository.markAllAsRead(username);

        // 🔥 Contabilizar en métricas
        if (unreadCount > 0) {
            metrics.incrementMarkAllReadOperations(unreadCount);
        }
    }

    // 📊 Contar notificaciones no leídas (con actualización de métricas)
    public long countUnreadNotifications(String username) {
        long count = notificationRepository.countUnreadByUsername(username);

        // Sincronizar métricas con estado real de BD
        long totalUnread = notificationRepository.countUnreadByUsername("");
        metrics.updateUnreadNotificationsCount(totalUnread);

        return count;
    }

    // Métodos existentes para compatibilidad con User
    public List<Notification> getNotifications(String username) {
        User recipient = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return notificationRepository.findByRecipient(recipient);
    }
}
