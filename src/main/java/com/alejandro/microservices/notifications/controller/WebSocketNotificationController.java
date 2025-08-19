package com.alejandro.microservices.notifications.controller;

import com.alejandro.microservices.notifications.model.Notification;
import com.alejandro.microservices.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketNotificationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    // Cliente envía notificación a /app/send
    @MessageMapping("/send")
    public void sendNotification(@Payload NotificationRequest request) {
        Notification saved = notificationService.sendNotification(request.getUsername(), request.getMessage());

        // La enviamos en tiempo real al canal del usuario
        messagingTemplate.convertAndSend("/topic/notifications/" + request.getUsername(), saved);
    }

    // Método para enviar notificaciones desde otros servicios
    public void sendRealTimeNotification(String username, Notification notification) {
        messagingTemplate.convertAndSend("/topic/notifications/" + username, notification);
    }

    // Clase interna para el request de WebSocket
    public static class NotificationRequest {
        private String username;
        private String message;

        public NotificationRequest() {}

        public NotificationRequest(String username, String message) {
            this.username = username;
            this.message = message;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
