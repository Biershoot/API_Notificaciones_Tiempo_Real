package com.alejandro.microservices.notifications.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Servicio para enviar notificaciones SMS usando Twilio.
 */
@Service
@Slf4j
public class SmsService {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.phoneNumber}")
    private String fromNumber;

    /**
     * Inicializa la configuración de Twilio.
     */
    @PostConstruct
    public void init() {
        if (!"TU_ACCOUNT_SID".equals(accountSid) && !"TU_AUTH_TOKEN".equals(authToken)) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio inicializado correctamente con número: {}", fromNumber);
        } else {
            log.warn("Twilio no configurado - usando credenciales por defecto");
        }
    }

    /**
     * Envía un SMS usando Twilio.
     *
     * @param to      Número de teléfono destino
     * @param message Mensaje a enviar
     * @return true si se envió correctamente, false en caso contrario
     */
    public boolean sendSms(String to, String message) {
        try {
            if ("TU_ACCOUNT_SID".equals(accountSid) || "TU_AUTH_TOKEN".equals(authToken)) {
                log.warn("Twilio no configurado - SMS simulado para: {} - Mensaje: {}", to, message);
                return true; // Simulado para desarrollo
            }

            Message twilioMessage = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(fromNumber),
                    message
            ).create();

            log.info("SMS enviado exitosamente a {} - SID: {}", to, twilioMessage.getSid());
            return true;

        } catch (Exception e) {
            log.error("Error enviando SMS a {}: {}", to, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Envía un SMS de notificación con formato estándar.
     *
     * @param to      Número de teléfono destino
     * @param title   Título de la notificación
     * @param message Mensaje de la notificación
     * @return true si se envió correctamente
     */
    public boolean sendNotificationSms(String to, String title, String message) {
        String formattedMessage = String.format("[%s] %s", title, message);
        return sendSms(to, formattedMessage);
    }

    /**
     * Verifica si el servicio de SMS está configurado correctamente.
     *
     * @return true si está configurado
     */
    public boolean isConfigured() {
        return !"TU_ACCOUNT_SID".equals(accountSid) && !"TU_AUTH_TOKEN".equals(authToken);
    }
}
