package com.alejandro.microservices.notifications.service;

import com.alejandro.microservices.notifications.model.Notification;
import com.alejandro.microservices.notifications.model.User;
import com.alejandro.microservices.notifications.repository.NotificationRepository;
import com.alejandro.microservices.notifications.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    private final RedisPublisher redisPublisher;

    // 📩 Enviar una notificación con Redis Pub/Sub distribuido
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

        // 🚀 Publicamos en Redis para sincronizar con todas las instancias
        redisPublisher.publish(saved);

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

    // ✅ Marcar todas las notificaciones como leídas
    @Transactional
    public void markAllAsRead(String username) {
        notificationRepository.markAllAsRead(username);
    }

    // ✅ Marcar una notificación específica como leída
    @Transactional
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        notificationRepository.markAsReadById(notificationId);
        notification.setRead(true);

        return notification;
    }

    // 📊 Contar notificaciones no leídas
    public long countUnreadNotifications(String username) {
        return notificationRepository.countUnreadByUsername(username);
    }

    // Métodos existentes para compatibilidad con User
    public List<Notification> getNotifications(String username) {
        User recipient = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return notificationRepository.findByRecipient(recipient);
    }
}
