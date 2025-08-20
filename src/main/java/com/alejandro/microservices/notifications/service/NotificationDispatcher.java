package com.alejandro.microservices.notifications.service;

import com.alejandro.microservices.notifications.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Servicio para despachar notificaciones a través de diferentes canales.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final SmsService smsService;
    private final SendGridEmailService emailService;
    private final RedisPublisher redisPublisher;

    /**
     * Despacha una notificación a través del canal especificado.
     *
     * @param notification Notificación a enviar
     * @param channel      Canal de envío (APP, EMAIL, SMS, ALL)
     * @param email        Email del destinatario (opcional, requerido para canal EMAIL)
     * @param phone        Teléfono del destinatario (opcional, requerido para canal SMS)
     * @return true si se envió correctamente
     */
    public boolean dispatchNotification(Notification notification, String channel, String email, String phone) {
        try {
            log.info("Despachando notificación ID: {} por canal: {}", notification.getId(), channel);

            switch (channel.toUpperCase()) {
                case "APP" -> {
                    // Enviar por WebSocket/Redis
                    redisPublisher.publish(notification);
                    log.info("Notificación enviada por APP (WebSocket/Redis)");
                    return true;
                }
                case "EMAIL" -> {
                    // Validar que el email esté presente
                    if (email == null || email.trim().isEmpty()) {
                        log.error("Email requerido para canal EMAIL");
                        return false;
                    }
                    
                    // Enviar por email
                    boolean emailSent = emailService.sendNotificationEmail(
                            email,
                            notification.getTitle() != null ? notification.getTitle() : "Notificación",
                            notification.getMessage()
                    );
                    log.info("Notificación enviada por EMAIL a {}: {}", email, emailSent);
                    return emailSent;
                }
                case "SMS" -> {
                    // Validar que el teléfono esté presente
                    if (phone == null || phone.trim().isEmpty()) {
                        log.error("Teléfono requerido para canal SMS");
                        return false;
                    }
                    
                    // Enviar por SMS
                    boolean smsSent = smsService.sendNotificationSms(
                            phone,
                            notification.getTitle() != null ? notification.getTitle() : "Notificación",
                            notification.getMessage()
                    );
                    log.info("Notificación enviada por SMS a {}: {}", phone, smsSent);
                    return smsSent;
                }
                case "ALL" -> {
                    // Enviar por todos los canales
                    return dispatchToAllChannels(notification, email, phone);
                }
                default -> {
                    log.error("Canal no soportado: {}", channel);
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("Error despachando notificación por canal {}: {}", channel, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Despacha una notificación a través del canal especificado (método legacy).
     *
     * @param notification Notificación a enviar
     * @param channel      Canal de envío (APP, EMAIL, SMS, ALL)
     * @return true si se envió correctamente
     */
    public boolean dispatchNotification(Notification notification, String channel) {
        // Usar valores por defecto para compatibilidad
        String defaultEmail = notification.getUsername() + "@example.com";
        String defaultPhone = "+1234567890";
        return dispatchNotification(notification, channel, defaultEmail, defaultPhone);
    }

    /**
     * Despacha una notificación a todos los canales disponibles.
     *
     * @param notification Notificación a enviar
     * @param email        Email del destinatario
     * @param phone        Teléfono del destinatario
     * @return true si se envió por al menos un canal
     */
    public boolean dispatchToAllChannels(Notification notification, String email, String phone) {
        log.info("Despachando notificación ID: {} a todos los canales", notification.getId());

        // Enviar de forma asíncrona a todos los canales
        CompletableFuture<Boolean> appFuture = CompletableFuture.supplyAsync((Supplier<Boolean>) () -> {
            try {
                redisPublisher.publish(notification);
                return true;
            } catch (Exception e) {
                log.error("Error enviando por APP: {}", e.getMessage());
                return false;
            }
        });

        CompletableFuture<Boolean> emailFuture = CompletableFuture.supplyAsync((Supplier<Boolean>) () -> {
            try {
                if (email == null || email.trim().isEmpty()) {
                    log.warn("Email no proporcionado, saltando envío por EMAIL");
                    return false;
                }
                return emailService.sendNotificationEmail(
                        email,
                        notification.getTitle() != null ? notification.getTitle() : "Notificación",
                        notification.getMessage()
                );
            } catch (Exception e) {
                log.error("Error enviando por EMAIL: {}", e.getMessage());
                return false;
            }
        });

        CompletableFuture<Boolean> smsFuture = CompletableFuture.supplyAsync((Supplier<Boolean>) () -> {
            try {
                if (phone == null || phone.trim().isEmpty()) {
                    log.warn("Teléfono no proporcionado, saltando envío por SMS");
                    return false;
                }
                return smsService.sendNotificationSms(
                        phone,
                        notification.getTitle() != null ? notification.getTitle() : "Notificación",
                        notification.getMessage()
                );
            } catch (Exception e) {
                log.error("Error enviando por SMS: {}", e.getMessage());
                return false;
            }
        });

        // Esperar a que todos los canales completen
        CompletableFuture.allOf(appFuture, emailFuture, smsFuture).join();

        boolean appSent = appFuture.getNow(false);
        boolean emailSent = emailFuture.getNow(false);
        boolean smsSent = smsFuture.getNow(false);

        log.info("Resultados del despacho múltiple - APP: {}, EMAIL: {}, SMS: {}", 
                appSent, emailSent, smsSent);

        return appSent || emailSent || smsSent;
    }

    /**
     * Despacha una notificación a todos los canales disponibles (método legacy).
     *
     * @param notification Notificación a enviar
     * @return true si se envió por al menos un canal
     */
    public boolean dispatchToAllChannels(Notification notification) {
        String defaultEmail = notification.getUsername() + "@example.com";
        String defaultPhone = "+1234567890";
        return dispatchToAllChannels(notification, defaultEmail, defaultPhone);
    }

    /**
     * Despacha una notificación a múltiples canales específicos.
     *
     * @param notification Notificación a enviar
     * @param channels     Lista de canales a usar
     * @param email        Email del destinatario
     * @param phone        Teléfono del destinatario
     * @return Mapa con el resultado de cada canal
     */
    public Map<String, Boolean> dispatchToChannels(Notification notification, List<String> channels, String email, String phone) {
        log.info("Despachando notificación ID: {} a canales: {}", notification.getId(), channels);

        return channels.stream()
                .collect(java.util.stream.Collectors.toMap(
                        channel -> channel,
                        channel -> dispatchNotification(notification, channel, email, phone)
                ));
    }

    /**
     * Despacha una notificación a múltiples canales específicos (método legacy).
     *
     * @param notification Notificación a enviar
     * @param channels     Lista de canales a usar
     * @return Mapa con el resultado de cada canal
     */
    public Map<String, Boolean> dispatchToChannels(Notification notification, List<String> channels) {
        String defaultEmail = notification.getUsername() + "@example.com";
        String defaultPhone = "+1234567890";
        return dispatchToChannels(notification, channels, defaultEmail, defaultPhone);
    }

    /**
     * Verifica el estado de configuración de todos los canales.
     *
     * @return Mapa con el estado de cada canal
     */
    public Map<String, Boolean> getChannelsStatus() {
        return Map.of(
                "SMS", smsService.isConfigured(),
                "EMAIL", emailService.isConfigured(),
                "APP", true // Siempre disponible
        );
    }
}
