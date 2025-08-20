package com.alejandro.microservices.notifications.service;

import com.alejandro.microservices.notifications.model.NotificationLog;
import com.alejandro.microservices.notifications.repository.NotificationLogRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportGenerationService {

    private final NotificationLogRepository notificationLogRepository;

    // ========== GENERACIÓN DE REPORTES PDF ==========

    public byte[] generateWeeklyPdfReport() throws Exception {
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(7);
        List<NotificationLog> notifications = notificationLogRepository
                .findBySentAtGreaterThanEqual(startOfWeek);

        return generatePdfReport(notifications, "Reporte Semanal de Notificaciones", startOfWeek, LocalDateTime.now());
    }

    public byte[] generateMonthlyPdfReport() throws Exception {
        LocalDateTime startOfMonth = LocalDateTime.now().minusMonths(1);
        List<NotificationLog> notifications = notificationLogRepository
                .findBySentAtGreaterThanEqual(startOfMonth);

        return generatePdfReport(notifications, "Reporte Mensual de Notificaciones", startOfMonth, LocalDateTime.now());
    }

    public byte[] generateCustomPdfReport(LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        List<NotificationLog> notifications = notificationLogRepository
                .findBySentAtBetween(startDate, endDate);

        String title = String.format("Reporte de Notificaciones (%s - %s)",
            startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return generatePdfReport(notifications, title, startDate, endDate);
    }

    private byte[] generatePdfReport(List<NotificationLog> notifications, String title,
                                   LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Título del reporte
        document.add(new Paragraph(title)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(18)
                .setBold());

        // Información general
        document.add(new Paragraph(String.format("Período: %s - %s",
            startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))))
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph(String.format("Fecha de generación: %s",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))))
                .setTextAlignment(TextAlignment.CENTER));

        // Estadísticas generales
        long totalNotifications = notifications.size();
        long successfulNotifications = notifications.stream().mapToLong(n -> n.isSuccess() ? 1 : 0).sum();
        long failedNotifications = totalNotifications - successfulNotifications;
        double successRate = totalNotifications > 0 ? (successfulNotifications * 100.0 / totalNotifications) : 0.0;

        document.add(new Paragraph("\n--- RESUMEN EJECUTIVO ---").setBold());
        document.add(new Paragraph(String.format("Total de notificaciones: %d", totalNotifications)));
        document.add(new Paragraph(String.format("Notificaciones exitosas: %d", successfulNotifications)));
        document.add(new Paragraph(String.format("Notificaciones fallidas: %d", failedNotifications)));
        document.add(new Paragraph(String.format("Tasa de éxito: %.2f%%", successRate)));

        // Estadísticas por canal
        document.add(new Paragraph("\n--- ESTADÍSTICAS POR CANAL ---").setBold());
        notifications.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    NotificationLog::getChannel,
                    java.util.stream.Collectors.counting()))
                .forEach((channel, count) ->
                    document.add(new Paragraph(String.format("Canal %s: %d notificaciones", channel, count))));

        // Estadísticas por tipo
        document.add(new Paragraph("\n--- ESTADÍSTICAS POR TIPO ---").setBold());
        notifications.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    NotificationLog::getType,
                    java.util.stream.Collectors.counting()))
                .forEach((type, count) ->
                    document.add(new Paragraph(String.format("Tipo %s: %d notificaciones", type, count))));

        // Tabla detallada de notificaciones (últimas 50)
        if (!notifications.isEmpty()) {
            document.add(new Paragraph("\n--- DETALLE DE NOTIFICACIONES (ÚLTIMAS 50) ---").setBold());

            Table table = new Table(UnitValue.createPercentArray(new float[]{10, 15, 10, 10, 10, 15, 30}));
            table.setWidth(UnitValue.createPercentValue(100));

            // Headers
            table.addHeaderCell("ID");
            table.addHeaderCell("Usuario");
            table.addHeaderCell("Canal");
            table.addHeaderCell("Tipo");
            table.addHeaderCell("Prioridad");
            table.addHeaderCell("Fecha/Hora");
            table.addHeaderCell("Estado");

            // Datos (limitamos a las últimas 50)
            notifications.stream()
                    .sorted((a, b) -> b.getSentAt().compareTo(a.getSentAt()))
                    .limit(50)
                    .forEach(notification -> {
                        table.addCell(notification.getId().toString());
                        table.addCell(notification.getUsername());
                        table.addCell(notification.getChannel());
                        table.addCell(notification.getType());
                        table.addCell(notification.getPriority());
                        table.addCell(notification.getSentAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                        table.addCell(notification.isSuccess() ? "Exitoso" : "Fallido");
                    });

            document.add(table);
        }

        document.close();
        log.info("Reporte PDF generado: {} notificaciones, {} bytes", notifications.size(), out.size());
        return out.toByteArray();
    }

    // ========== GENERACIÓN DE REPORTES EXCEL ==========

    public byte[] generateWeeklyExcelReport() throws Exception {
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(7);
        List<NotificationLog> notifications = notificationLogRepository
                .findBySentAtGreaterThanEqual(startOfWeek);

        return generateExcelReport(notifications, "Reporte Semanal", startOfWeek, LocalDateTime.now());
    }

    public byte[] generateMonthlyExcelReport() throws Exception {
        LocalDateTime startOfMonth = LocalDateTime.now().minusMonths(1);
        List<NotificationLog> notifications = notificationLogRepository
                .findBySentAtGreaterThanEqual(startOfMonth);

        return generateExcelReport(notifications, "Reporte Mensual", startOfMonth, LocalDateTime.now());
    }

    public byte[] generateCustomExcelReport(LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        List<NotificationLog> notifications = notificationLogRepository
                .findBySentAtBetween(startDate, endDate);

        return generateExcelReport(notifications, "Reporte Personalizado", startDate, endDate);
    }

    private byte[] generateExcelReport(List<NotificationLog> notifications, String sheetName,
                                     LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        Workbook workbook = new XSSFWorkbook();

        // Hoja de resumen
        Sheet summarySheet = workbook.createSheet("Resumen");
        createSummarySheet(summarySheet, notifications, startDate, endDate, workbook);

        // Hoja de datos detallados
        Sheet detailSheet = workbook.createSheet("Detalle");
        createDetailSheet(detailSheet, notifications, workbook);

        // Hoja de estadísticas
        Sheet statsSheet = workbook.createSheet("Estadísticas");
        createStatsSheet(statsSheet, notifications, workbook);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        log.info("Reporte Excel generado: {} notificaciones, {} bytes", notifications.size(), out.size());
        return out.toByteArray();
    }

    private void createSummarySheet(Sheet sheet, List<NotificationLog> notifications,
                                  LocalDateTime startDate, LocalDateTime endDate, Workbook workbook) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        int rowNum = 0;

        // Título
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("REPORTE DE NOTIFICACIONES - RESUMEN EJECUTIVO");
        titleCell.setCellStyle(headerStyle);

        rowNum++; // Fila vacía

        // Información del período
        createLabelValueRow(sheet, rowNum++, "Período:",
            String.format("%s - %s",
                startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
            headerStyle, dataStyle);

        createLabelValueRow(sheet, rowNum++, "Fecha de generación:",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            headerStyle, dataStyle);

        rowNum++; // Fila vacía

        // Estadísticas generales
        long totalNotifications = notifications.size();
        long successfulNotifications = notifications.stream().mapToLong(n -> n.isSuccess() ? 1 : 0).sum();
        long failedNotifications = totalNotifications - successfulNotifications;
        double successRate = totalNotifications > 0 ? (successfulNotifications * 100.0 / totalNotifications) : 0.0;

        createLabelValueRow(sheet, rowNum++, "Total de notificaciones:", String.valueOf(totalNotifications), headerStyle, dataStyle);
        createLabelValueRow(sheet, rowNum++, "Notificaciones exitosas:", String.valueOf(successfulNotifications), headerStyle, dataStyle);
        createLabelValueRow(sheet, rowNum++, "Notificaciones fallidas:", String.valueOf(failedNotifications), headerStyle, dataStyle);
        createLabelValueRow(sheet, rowNum++, "Tasa de éxito:", String.format("%.2f%%", successRate), headerStyle, dataStyle);

        // Ajustar ancho de columnas
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createDetailSheet(Sheet sheet, List<NotificationLog> notifications, Workbook workbook) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        int rowNum = 0;

        // Headers
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"ID", "Usuario", "Canal", "Tipo", "Prioridad", "Fecha/Hora", "Estado", "Tiempo (ms)", "Mensaje Error"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        for (NotificationLog notification : notifications) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(notification.getId());
            row.createCell(1).setCellValue(notification.getUsername());
            row.createCell(2).setCellValue(notification.getChannel());
            row.createCell(3).setCellValue(notification.getType());
            row.createCell(4).setCellValue(notification.getPriority());
            row.createCell(5).setCellValue(notification.getSentAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            row.createCell(6).setCellValue(notification.isSuccess() ? "Exitoso" : "Fallido");
            row.createCell(7).setCellValue(notification.getProcessingTimeMs() != null ? notification.getProcessingTimeMs() : 0);
            row.createCell(8).setCellValue(notification.getErrorMessage() != null ? notification.getErrorMessage() : "");

            // Aplicar estilo a todas las celdas
            for (int i = 0; i < headers.length; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }

        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createStatsSheet(Sheet sheet, List<NotificationLog> notifications, Workbook workbook) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        int rowNum = 0;

        // Estadísticas por canal
        Row channelTitleRow = sheet.createRow(rowNum++);
        Cell channelTitleCell = channelTitleRow.createCell(0);
        channelTitleCell.setCellValue("ESTADÍSTICAS POR CANAL");
        channelTitleCell.setCellStyle(headerStyle);

        Row channelHeaderRow = sheet.createRow(rowNum++);
        channelHeaderRow.createCell(0).setCellValue("Canal");
        channelHeaderRow.createCell(1).setCellValue("Cantidad");
        channelHeaderRow.createCell(0).setCellStyle(headerStyle);
        channelHeaderRow.createCell(1).setCellStyle(headerStyle);

        notifications.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    NotificationLog::getChannel,
                    java.util.stream.Collectors.counting()))
                .forEach((channel, count) -> {
                    Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                    row.createCell(0).setCellValue(channel);
                    row.createCell(1).setCellValue(count);
                    row.getCell(0).setCellStyle(dataStyle);
                    row.getCell(1).setCellStyle(dataStyle);
                });

        rowNum = sheet.getLastRowNum() + 2;

        // Estadísticas por tipo
        Row typeTitleRow = sheet.createRow(rowNum++);
        Cell typeTitleCell = typeTitleRow.createCell(0);
        typeTitleCell.setCellValue("ESTADÍSTICAS POR TIPO");
        typeTitleCell.setCellStyle(headerStyle);

        Row typeHeaderRow = sheet.createRow(rowNum++);
        typeHeaderRow.createCell(0).setCellValue("Tipo");
        typeHeaderRow.createCell(1).setCellValue("Cantidad");
        typeHeaderRow.createCell(0).setCellStyle(headerStyle);
        typeHeaderRow.createCell(1).setCellStyle(headerStyle);

        notifications.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    NotificationLog::getType,
                    java.util.stream.Collectors.counting()))
                .forEach((type, count) -> {
                    Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                    row.createCell(0).setCellValue(type);
                    row.createCell(1).setCellValue(count);
                    row.getCell(0).setCellStyle(dataStyle);
                    row.getCell(1).setCellStyle(dataStyle);
                });

        // Ajustar ancho de columnas
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createLabelValueRow(Sheet sheet, int rowNum, String label, String value,
                                   CellStyle labelStyle, CellStyle valueStyle) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);

        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(valueStyle);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        return style;
    }

    // Getter para el scheduler service
    public NotificationLogRepository getNotificationLogRepository() {
        return this.notificationLogRepository;
    }
}
