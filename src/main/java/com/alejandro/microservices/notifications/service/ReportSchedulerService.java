package com.alejandro.microservices.notifications.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.task.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class ReportSchedulerService {

    private final ReportGenerationService reportGenerationService;
    private final EmailService emailService;

    @Value("${reports.email.admin}")
    private String adminEmail;

    @Value("${reports.schedule.weekly.enabled:true}")
    private boolean weeklyReportsEnabled;

    @Value("${reports.schedule.monthly.enabled:true}")
    private boolean monthlyReportsEnabled;

    @Value("${reports.email.enabled:true}")
    private boolean emailEnabled;

    /**
     * Envía reporte semanal cada lunes a las 8:00 AM
     */
    @Scheduled(cron = "0 0 8 * * MON")
    public void sendWeeklyReport() {
        if (!weeklyReportsEnabled || !emailEnabled) {
            log.info("Reporte semanal deshabilitado o email deshabilitado. No se enviará el reporte.");
            return;
        }

        log.info("Iniciando generación de reporte semanal automático...");

        try {
            // Validar configuración de email
            if (!emailService.isEmailConfigurationValid()) {
                log.error("Configuración de email inválida. No se puede enviar el reporte semanal.");
                return;
            }

            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(7);

            // Generar reportes PDF y Excel
            log.info("Generando reportes semanal PDF y Excel...");
            byte[] pdfReport = reportGenerationService.generateWeeklyPdfReport();
            byte[] excelReport = reportGenerationService.generateWeeklyExcelReport();

            // Calcular estadísticas básicas para el email
            long totalNotifications = getTotalNotificationsForPeriod(startDate, endDate);
            long successfulNotifications = getSuccessfulNotificationsForPeriod(startDate, endDate);
            double successRate = totalNotifications > 0 ? (successfulNotifications * 100.0 / totalNotifications) : 0.0;

            // Generar contenido del email
            String emailContent = emailService.generateReportEmailContent(
                "Reporte Semanal de Notificaciones",
                startDate,
                endDate,
                totalNotifications,
                successfulNotifications,
                successRate
            );

            // Nombres de archivos con fecha
            String dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String pdfFilename = String.format("reporte_semanal_%s.pdf", dateString);
            String excelFilename = String.format("reporte_semanal_%s.xlsx", dateString);

            // Enviar email con ambos adjuntos
            emailService.sendEmailWithMultipleAttachments(
                adminEmail,
                "📊 Reporte Semanal de Notificaciones - " + dateString,
                emailContent,
                new EmailService.EmailAttachment(pdfFilename, pdfReport, "application/pdf"),
                new EmailService.EmailAttachment(excelFilename, excelReport, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            );

            log.info("Reporte semanal enviado exitosamente a: {} (PDF: {} bytes, Excel: {} bytes)",
                adminEmail, pdfReport.length, excelReport.length);

        } catch (Exception e) {
            log.error("Error generando/enviando reporte semanal: {}", e.getMessage(), e);

            // Enviar notificación de error al administrador
            try {
                emailService.sendErrorNotification(adminEmail,
                    "Error al generar reporte semanal automático", e);
            } catch (Exception emailError) {
                log.error("Error enviando notificación de error: {}", emailError.getMessage());
            }
        }
    }

    /**
     * Envía reporte mensual el primer día de cada mes a las 9:00 AM
     */
    @Scheduled(cron = "0 0 9 1 * *")
    public void sendMonthlyReport() {
        if (!monthlyReportsEnabled || !emailEnabled) {
            log.info("Reporte mensual deshabilitado o email deshabilitado. No se enviará el reporte.");
            return;
        }

        log.info("Iniciando generación de reporte mensual automático...");

        try {
            // Validar configuración de email
            if (!emailService.isEmailConfigurationValid()) {
                log.error("Configuración de email inválida. No se puede enviar el reporte mensual.");
                return;
            }

            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusMonths(1);

            // Generar reportes PDF y Excel
            log.info("Generando reportes mensual PDF y Excel...");
            byte[] pdfReport = reportGenerationService.generateMonthlyPdfReport();
            byte[] excelReport = reportGenerationService.generateMonthlyExcelReport();

            // Calcular estadísticas básicas para el email
            long totalNotifications = getTotalNotificationsForPeriod(startDate, endDate);
            long successfulNotifications = getSuccessfulNotificationsForPeriod(startDate, endDate);
            double successRate = totalNotifications > 0 ? (successfulNotifications * 100.0 / totalNotifications) : 0.0;

            // Generar contenido del email
            String emailContent = emailService.generateReportEmailContent(
                "Reporte Mensual de Notificaciones",
                startDate,
                endDate,
                totalNotifications,
                successfulNotifications,
                successRate
            );

            // Nombres de archivos con fecha
            String dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String pdfFilename = String.format("reporte_mensual_%s.pdf", dateString);
            String excelFilename = String.format("reporte_mensual_%s.xlsx", dateString);

            // Enviar email con ambos adjuntos
            emailService.sendEmailWithMultipleAttachments(
                adminEmail,
                "📊 Reporte Mensual de Notificaciones - " + dateString,
                emailContent,
                new EmailService.EmailAttachment(pdfFilename, pdfReport, "application/pdf"),
                new EmailService.EmailAttachment(excelFilename, excelReport, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            );

            log.info("Reporte mensual enviado exitosamente a: {} (PDF: {} bytes, Excel: {} bytes)",
                adminEmail, pdfReport.length, excelReport.length);

        } catch (Exception e) {
            log.error("Error generando/enviando reporte mensual: {}", e.getMessage(), e);

            // Enviar notificación de error al administrador
            try {
                emailService.sendErrorNotification(adminEmail,
                    "Error al generar reporte mensual automático", e);
            } catch (Exception emailError) {
                log.error("Error enviando notificación de error: {}", emailError.getMessage());
            }
        }
    }

    /**
     * Reporte de salud del sistema cada día a las 6:00 AM
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void sendDailyHealthCheck() {
        if (!emailEnabled) {
            return;
        }

        log.info("Ejecutando chequeo de salud diario del sistema...");

        try {
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(1);

            long totalNotifications = getTotalNotificationsForPeriod(startDate, endDate);
            long successfulNotifications = getSuccessfulNotificationsForPeriod(startDate, endDate);
            double successRate = totalNotifications > 0 ? (successfulNotifications * 100.0 / totalNotifications) : 0.0;

            // Solo enviar si hay problemas (tasa de éxito < 95%) o si es lunes (resumen semanal)
            boolean isMonday = LocalDateTime.now().getDayOfWeek().getValue() == 1;
            boolean hasProblems = successRate < 95.0 || totalNotifications == 0;

            if (hasProblems || isMonday) {
                String subject = hasProblems ?
                    "🚨 Alerta: Problemas en Sistema de Notificaciones" :
                    "✅ Resumen de Salud del Sistema";

                String statusIcon = successRate >= 95.0 ? "✅" : successRate >= 90.0 ? "⚠️" : "🚨";

                String content = String.format("""
                    <html>
                    <body style="font-family: Arial, sans-serif;">
                        <div style="background-color: %s; color: white; padding: 15px;">
                            <h2>%s Estado del Sistema de Notificaciones</h2>
                        </div>
                        
                        <div style="padding: 20px;">
                            <h3>Resumen de las últimas 24 horas:</h3>
                            <ul>
                                <li><strong>Total de notificaciones:</strong> %d</li>
                                <li><strong>Exitosas:</strong> %d</li>
                                <li><strong>Tasa de éxito:</strong> %.2f%%</li>
                            </ul>
                            
                            <p><em>Generado automáticamente el %s</em></p>
                        </div>
                    </body>
                    </html>
                    """,
                    hasProblems ? "#dc3545" : "#28a745",
                    statusIcon,
                    totalNotifications,
                    successfulNotifications,
                    successRate,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                );

                emailService.sendEmailWithAttachment(adminEmail, subject, content, null, null);
                log.info("Reporte de salud diario enviado: Tasa de éxito: {}%", successRate);
            } else {
                log.info("Sistema funcionando correctamente. Tasa de éxito: {}%. No se envía reporte.", successRate);
            }

        } catch (Exception e) {
            log.error("Error en chequeo de salud diario: {}", e.getMessage(), e);
        }
    }

    /**
     * Envío manual de reporte personalizado
     */
    public void sendCustomReport(String email, LocalDateTime startDate, LocalDateTime endDate,
                               boolean includePdf, boolean includeExcel) throws Exception {

        if (!emailService.isEmailConfigurationValid()) {
            throw new IllegalStateException("Configuración de email inválida");
        }

        log.info("Generando reporte personalizado para {} desde {} hasta {}", email, startDate, endDate);

        byte[] pdfReport = null;
        byte[] excelReport = null;

        if (includePdf) {
            pdfReport = reportGenerationService.generateCustomPdfReport(startDate, endDate);
        }

        if (includeExcel) {
            excelReport = reportGenerationService.generateCustomExcelReport(startDate, endDate);
        }

        long totalNotifications = getTotalNotificationsForPeriod(startDate, endDate);
        long successfulNotifications = getSuccessfulNotificationsForPeriod(startDate, endDate);
        double successRate = totalNotifications > 0 ? (successfulNotifications * 100.0 / totalNotifications) : 0.0;

        String emailContent = emailService.generateReportEmailContent(
            "Reporte Personalizado de Notificaciones",
            startDate,
            endDate,
            totalNotifications,
            successfulNotifications,
            successRate
        );

        if (includePdf && includeExcel) {
            String dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            emailService.sendEmailWithMultipleAttachments(
                email,
                "📊 Reporte Personalizado de Notificaciones - " + dateString,
                emailContent,
                new EmailService.EmailAttachment("reporte_personalizado.pdf", pdfReport, "application/pdf"),
                new EmailService.EmailAttachment("reporte_personalizado.xlsx", excelReport, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            );
        } else if (includePdf) {
            emailService.sendEmailWithAttachment(email,
                "📊 Reporte Personalizado de Notificaciones (PDF)",
                emailContent, pdfReport, "reporte_personalizado.pdf");
        } else if (includeExcel) {
            emailService.sendEmailWithAttachment(email,
                "📊 Reporte Personalizado de Notificaciones (Excel)",
                emailContent, excelReport, "reporte_personalizado.xlsx");
        } else {
            emailService.sendEmailWithAttachment(email,
                "📊 Reporte Personalizado de Notificaciones",
                emailContent, null, null);
        }

        log.info("Reporte personalizado enviado exitosamente a: {}", email);
    }

    // Métodos auxiliares para obtener estadísticas
    private long getTotalNotificationsForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return reportGenerationService.getNotificationLogRepository()
                .countByDateRange(startDate, endDate);
        } catch (Exception e) {
            log.warn("Error obteniendo total de notificaciones: {}", e.getMessage());
            return 0;
        }
    }

    private long getSuccessfulNotificationsForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return reportGenerationService.getNotificationLogRepository()
                .findBySentAtBetween(startDate, endDate)
                .stream()
                .mapToLong(n -> n.isSuccess() ? 1 : 0)
                .sum();
        } catch (Exception e) {
            log.warn("Error obteniendo notificaciones exitosas: {}", e.getMessage());
            return 0;
        }
    }
}
