package com.alejandro.microservices.notifications.service;

import com.alejandro.microservices.notifications.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servicio para publicar notificaciones en Redis.
 * Este servicio solo se activa cuando está configurado un host de Redis.
 */
@Service
@ConditionalOnProperty(name = "spring.data.redis.host")
@RequiredArgsConstructor
@Slf4j
public class RedisPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CHANNEL = "notifications";

    /**
     * Publica una notificación en el canal de Redis.
     *
     * @param notification La notificación a publicar
     * @throws RuntimeException si ocurre algún error durante la publicación
     */
    public void publish(Notification notification) {
        try {
            log.debug("Publicando notificación en Redis: ID={}, Usuario={}, Tipo={}",
                     notification.getId(), notification.getUsername(), notification.getType());

            String message = objectMapper.writeValueAsString(notification);
            redisTemplate.convertAndSend(CHANNEL, message);

            log.info("Notificación publicada exitosamente en Redis: ID={}", notification.getId());
        } catch (Exception e) {
            log.error("Error publicando notificación en Redis: {}", e.getMessage(), e);
            throw new RuntimeException("Error publicando notificación en Redis: " + e.getMessage(), e);
        }
    }
}
