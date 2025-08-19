package com.alejandro.microservices.notifications.service;

import com.alejandro.microservices.notifications.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(Notification notification) {
        try {
            String message = objectMapper.writeValueAsString(notification);
            redisTemplate.convertAndSend("notifications", message);
        } catch (Exception e) {
            throw new RuntimeException("Error publicando notificación en Redis", e);
        }
    }
}
