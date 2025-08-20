package com.alejandro.microservices.notifications.service;

import com.alejandro.microservices.notifications.model.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio que se suscribe a mensajes de Redis y los reenvía a través de WebSockets.
 * Este servicio solo se activa cuando está configurado un host de Redis.
 */
@Service
@ConditionalOnProperty(name = "spring.data.redis.host")
@Slf4j
public class RedisSubscriber {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param messagingTemplate Template para enviar mensajes a través de WebSockets
     * @param objectMapper Mapper para deserializar JSON
     */
    @Autowired
    public RedisSubscriber(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Procesa los mensajes recibidos de Redis y los envía a través de WebSockets.
     *
     * @param message El mensaje recibido en formato JSON
     * @param channel El canal de Redis desde el que se recibió el mensaje
     */
    public void onMessage(String message, String channel) {
        try {
            log.info("Recibido mensaje Redis en canal {}: {}", channel, message);
            Notification notification = objectMapper.readValue(message, Notification.class);

            // Obtener el username y asegurarse de que no sea nulo
            String username = notification.getUsername();
            if (username == null || username.isEmpty()) {
                log.warn("Notificación sin username válido: {}", notification);
                return;
            }

            // Enviamos al canal WebSocket correspondiente
            String destination = "/topic/notifications/" + username;
            messagingTemplate.convertAndSend(destination, notification);

            log.info("Notificación enviada via WebSocket a: {}", destination);
        } catch (Exception e) {
            log.error("Error procesando mensaje Redis: {}", e.getMessage(), e);
        }
    }
}
