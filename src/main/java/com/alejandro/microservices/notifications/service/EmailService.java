package com.alejandro.microservices.notifications.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${reports.email.from}")
    private String fromEmail;

    @Value("${reports.email.enabled:true}")
    private boolean emailEnabled;

    /**
     * Envía un email con un adjunto
     */
    public void sendEmailWithAttachment(String to, String subject, String text,
                                      byte[] attachment, String filename) throws MessagingException {
        if (!emailEnabled) {
            log.info("Envío de email deshabilitado. Email no enviado a: {}", to);
            return;
        }

        if (!StringUtils.hasText(to)) {
            log.warn("Dirección de email de destino vacía. No se puede enviar el email.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // HTML habilitado

            if (attachment != null && attachment.length > 0) {
                helper.addAttachment(filename, new ByteArrayResource(attachment));
                log.info("Adjunto agregado: {} ({} bytes)", filename, attachment.length);
            }

            mailSender.send(message);
            log.info("Email enviado exitosamente a: {} - Asunto: {}", to, subject);

        } catch (MessagingException e) {
            log.error("Error enviando email a {}: {}", to, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Envía múltiples adjuntos en un solo email
     */
    public void sendEmailWithMultipleAttachments(String to, String subject, String text,
                                               EmailAttachment... attachments) throws MessagingException {
        if (!emailEnabled) {
            log.info("Envío de email deshabilitado. Email no enviado a: {}", to);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            for (EmailAttachment attachment : attachments) {
                if (attachment.getData() != null && attachment.getData().length > 0) {
                    helper.addAttachment(attachment.getFilename(), new ByteArrayResource(attachment.getData()));
                }
            }

            mailSender.send(message);
            log.info("Email con {} adjuntos enviado exitosamente a: {}", attachments.length, to);

        } catch (MessagingException e) {
            log.error("Error enviando email con múltiples adjuntos a {}: {}", to, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Genera el contenido HTML para el email de reporte
     */
    public String generateReportEmailContent(String reportType, LocalDateTime startDate, LocalDateTime endDate,
                                           long totalNotifications, long successfulNotifications, double successRate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return String.format("""
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .header { background-color: #2E86AB; color: white; padding: 15px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .stats { background-color: white; padding: 15px; margin: 10px 0; border-left: 4px solid #2E86AB; }
                    .footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }
                    .success { color: #28a745; font-weight: bold; }
                    .total { color: #007bff; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h2>📊 %s</h2>
                </div>
                
                <div class="content">
                    <h3>Resumen Ejecutivo</h3>
                    
                    <div class="stats">
                        <p><strong>📅 Período:</strong> %s - %s</p>
                        <p><strong>📧 Total de notificaciones:</strong> <span class="total">%d</span></p>
                        <p><strong>✅ Notificaciones exitosas:</strong> <span class="success">%d</span></p>
                        <p><strong>📈 Tasa de éxito:</strong> <span class="success">%.2f%%</span></p>
                    </div>
                    
                    <p>En el archivo adjunto encontrará el reporte detallado con:</p>
                    <ul>
                        <li>📋 Análisis completo por canal y tipo de notificación</li>
                        <li>📊 Estadísticas de rendimiento y tiempos de procesamiento</li>
                        <li>🔍 Detalle de todas las notificaciones del período</li>
                        <li>📈 Gráficos y métricas de comportamiento</li>
                    </ul>
                    
                    <p><em>Este reporte ha sido generado automáticamente por el sistema de notificaciones.</em></p>
                </div>
                
                <div class="footer">
                    <p>Sistema de Notificaciones - Generado el %s</p>
                    <p>Para cualquier consulta, contacte al administrador del sistema.</p>
                </div>
            </body>
            </html>
            """,
            reportType,
            startDate.format(formatter),
            endDate.format(formatter),
            totalNotifications,
            successfulNotifications,
            successRate,
            LocalDateTime.now().format(formatter)
        );
    }

    /**
     * Envía un email de notificación de error en el sistema
     */
    public void sendErrorNotification(String adminEmail, String errorMessage, Exception exception) {
        if (!emailEnabled) {
            return;
        }

        try {
            String subject = "🚨 Error en Sistema de Notificaciones - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            String content = String.format("""
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <div style="background-color: #dc3545; color: white; padding: 15px;">
                        <h2>🚨 Error en Sistema de Notificaciones</h2>
                    </div>
                    
                    <div style="padding: 20px;">
                        <p><strong>Fecha y hora:</strong> %s</p>
                        <p><strong>Mensaje de error:</strong> %s</p>
                        
                        <div style="background-color: #f8f9fa; padding: 15px; margin: 10px 0; border-left: 4px solid #dc3545;">
                            <pre>%s</pre>
                        </div>
                        
                        <p><em>Este es un mensaje automático del sistema de monitoreo.</em></p>
                    </div>
                </body>
                </html>
                """,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                errorMessage,
                exception != null ? exception.toString() : "No hay detalles adicionales"
            );

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("Notificación de error enviada a: {}", adminEmail);

        } catch (Exception e) {
            log.error("Error enviando notificación de error por email: {}", e.getMessage(), e);
        }
    }

    /**
     * Clase auxiliar para manejar adjuntos
     */
    public static class EmailAttachment {
        private final String filename;
        private final byte[] data;
        private final String contentType;

        public EmailAttachment(String filename, byte[] data, String contentType) {
            this.filename = filename;
            this.data = data;
            this.contentType = contentType;
        }

        public EmailAttachment(String filename, byte[] data) {
            this(filename, data, "application/octet-stream");
        }

        public String getFilename() {
            return filename;
        }

        public byte[] getData() {
            return data;
        }

        public String getContentType() {
            return contentType;
        }
    }

    /**
     * Valida la configuración de email
     */
    public boolean isEmailConfigurationValid() {
        try {
            if (!emailEnabled) {
                log.info("Configuración de email deshabilitada");
                return false;
            }

            if (!StringUtils.hasText(fromEmail) || fromEmail.equals("tu_correo@gmail.com")) {
                log.warn("Configuración de email inválida: email 'from' no configurado correctamente");
                return false;
            }

            // Intentar crear un mensaje de prueba sin enviarlo
            MimeMessage testMessage = mailSender.createMimeMessage();
            log.info("Configuración de email válida");
            return true;

        } catch (Exception e) {
            log.error("Error validando configuración de email: {}", e.getMessage());
            return false;
        }
    }
}
