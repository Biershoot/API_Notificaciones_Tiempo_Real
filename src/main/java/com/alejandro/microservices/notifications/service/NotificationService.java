package com.alejandro.microservices.notifications.service;

import com.alejandro.microservices.notifications.model.Notification;
import com.alejandro.microservices.notifications.model.User;
import com.alejandro.microservices.notifications.repository.NotificationRepository;
import com.alejandro.microservices.notifications.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
                .message(message)
                .recipient(recipient)
                .timestamp(LocalDateTime.now())
                .read(false)
                .build();

        Notification saved = notificationRepository.save(notification);

        // 🚀 Publicamos en Redis para sincronizar con todas las instancias
        // Redis se encarga de distribuir a todos los subscribers (instancias de la app)
        redisPublisher.publish(saved);

        return saved;
    }

    // 📋 Listar todas las notificaciones de un usuario
    public List<Notification> getNotifications(String username) {
        User recipient = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return notificationRepository.findByRecipient(recipient);
    }

    // 📬 Listar solo notificaciones no leídas
    public List<Notification> getUnreadNotifications(String username) {
        User recipient = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return notificationRepository.findByRecipientAndReadFalse(recipient);
    }

    // ✅ Marcar notificación como leída
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
