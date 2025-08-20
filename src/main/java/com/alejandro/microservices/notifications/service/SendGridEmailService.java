package com.alejandro.microservices.notifications.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Servicio para enviar notificaciones por email usando SendGrid.
 */
@Service
@Slf4j
public class SendGridEmailService {

    @Value("${sendgrid.apiKey}")
    private String apiKey;

    @Value("${sendgrid.fromEmail}")
    private String fromEmail;

    /**
     * Envía un email usando SendGrid.
     *
     * @param to      Dirección de email destino
     * @param subject Asunto del email
     * @param body    Contenido del email
     * @return true si se envió correctamente, false en caso contrario
     */
    public boolean sendEmail(String to, String subject, String body) {
        try {
            if ("TU_API_KEY_SENDGRID".equals(apiKey)) {
                log.warn("SendGrid no configurado - Email simulado para: {} - Asunto: {}", to, subject);
                return true; // Simulado para desarrollo
            }

            Email from = new Email(fromEmail);
            Email recipient = new Email(to);
            Content content = new Content("text/plain", body);
            Mail mail = new Mail(from, subject, recipient, content);

            SendGrid sg = new SendGrid(apiKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            var response = sg.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Email enviado exitosamente a {} - Status: {}", to, response.getStatusCode());
                return true;
            } else {
                log.error("Error enviando email a {} - Status: {} - Body: {}", 
                         to, response.getStatusCode(), response.getBody());
                return false;
            }

        } catch (IOException e) {
            log.error("Error enviando email a {}: {}", to, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Envía un email de notificación con formato estándar.
     *
     * @param to      Dirección de email destino
     * @param title   Título de la notificación
     * @param message Mensaje de la notificación
     * @return true si se envió correctamente
     */
    public boolean sendNotificationEmail(String to, String title, String message) {
        String subject = String.format("Notificación: %s", title);
        String body = String.format("""
                Hola,
                
                Has recibido una nueva notificación:
                
                Título: %s
                Mensaje: %s
                
                Saludos,
                Sistema de Notificaciones
                """, title, message);

        return sendEmail(to, subject, body);
    }

    /**
     * Envía un email HTML usando SendGrid.
     *
     * @param to      Dirección de email destino
     * @param subject Asunto del email
     * @param htmlBody Contenido HTML del email
     * @return true si se envió correctamente
     */
    public boolean sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            if ("TU_API_KEY_SENDGRID".equals(apiKey)) {
                log.warn("SendGrid no configurado - Email HTML simulado para: {} - Asunto: {}", to, subject);
                return true; // Simulado para desarrollo
            }

            Email from = new Email(fromEmail);
            Email recipient = new Email(to);
            Content content = new Content("text/html", htmlBody);
            Mail mail = new Mail(from, subject, recipient, content);

            SendGrid sg = new SendGrid(apiKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            var response = sg.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Email HTML enviado exitosamente a {} - Status: {}", to, response.getStatusCode());
                return true;
            } else {
                log.error("Error enviando email HTML a {} - Status: {} - Body: {}", 
                         to, response.getStatusCode(), response.getBody());
                return false;
            }

        } catch (IOException e) {
            log.error("Error enviando email HTML a {}: {}", to, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Verifica si el servicio de email está configurado correctamente.
     *
     * @return true si está configurado
     */
    public boolean isConfigured() {
        return !"TU_API_KEY_SENDGRID".equals(apiKey);
    }
}
