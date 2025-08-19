package com.alejandro.microservices.notifications.service;

import com.alejandro.microservices.notifications.model.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(name = "spring.data.redis.host")
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public void onMessage(String message, String channel) {
        try {
            log.info("Recibido mensaje Redis en canal {}: {}", channel, message);
            Notification notification = objectMapper.readValue(message, Notification.class);

            // Enviamos al canal WebSocket correspondiente
            String destination = "/topic/notifications/" + notification.getRecipient().getUsername();
            messagingTemplate.convertAndSend(destination, notification);

            log.info("Notificación enviada via WebSocket a: {}", destination);
        } catch (Exception e) {
            log.error("Error procesando mensaje Redis: {}", e.getMessage(), e);
        }
    }
}
