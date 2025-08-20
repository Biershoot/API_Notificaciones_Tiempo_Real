package com.alejandro.microservices.notifications.controller;

import com.alejandro.microservices.notifications.service.ReportGenerationService;
import com.alejandro.microservices.notifications.service.ReportSchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports/automated")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reportes Automáticos", description = "Endpoints para generar y enviar reportes en PDF/Excel con envío automático por correo")
public class AutomatedReportController {

    private final ReportGenerationService reportGenerationService;
    private final ReportSchedulerService reportSchedulerService;

    // ========== ENDPOINTS PARA DESCARGA DIRECTA ==========

    @GetMapping("/pdf/weekly")
    @Operation(summary = "Descargar reporte semanal en PDF",
               description = "Genera y descarga un reporte PDF de las notificaciones de la última semana")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte PDF generado exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno generando el reporte")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<byte[]> downloadWeeklyPdfReport() {
        try {
            log.info("Generando reporte semanal PDF para descarga directa");
            byte[] pdfReport = reportGenerationService.generateWeeklyPdfReport();

            String filename = String.format("reporte_semanal_%s.pdf",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                    .body(pdfReport);

        } catch (Exception e) {
            log.error("Error generando reporte semanal PDF: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/pdf/monthly")
    @Operation(summary = "Descargar reporte mensual en PDF",
               description = "Genera y descarga un reporte PDF de las notificaciones del último mes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<byte[]> downloadMonthlyPdfReport() {
        try {
            log.info("Generando reporte mensual PDF para descarga directa");
            byte[] pdfReport = reportGenerationService.generateMonthlyPdfReport();

            String filename = String.format("reporte_mensual_%s.pdf",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                    .body(pdfReport);

        } catch (Exception e) {
            log.error("Error generando reporte mensual PDF: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/excel/weekly")
    @Operation(summary = "Descargar reporte semanal en Excel",
               description = "Genera y descarga un reporte Excel de las notificaciones de la última semana")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<byte[]> downloadWeeklyExcelReport() {
        try {
            log.info("Generando reporte semanal Excel para descarga directa");
            byte[] excelReport = reportGenerationService.generateWeeklyExcelReport();

            String filename = String.format("reporte_semanal_%s.xlsx",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(excelReport);

        } catch (Exception e) {
            log.error("Error generando reporte semanal Excel: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/excel/monthly")
    @Operation(summary = "Descargar reporte mensual en Excel",
               description = "Genera y descarga un reporte Excel de las notificaciones del último mes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<byte[]> downloadMonthlyExcelReport() {
        try {
            log.info("Generando reporte mensual Excel para descarga directa");
            byte[] excelReport = reportGenerationService.generateMonthlyExcelReport();

            String filename = String.format("reporte_mensual_%s.xlsx",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(excelReport);

        } catch (Exception e) {
            log.error("Error generando reporte mensual Excel: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/pdf/custom")
    @Operation(summary = "Descargar reporte personalizado en PDF",
               description = "Genera y descarga un reporte PDF para un período de fechas personalizado")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<byte[]> downloadCustomPdfReport(
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-dd'T'HH:mm:ss)", example = "2023-12-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin (formato: yyyy-MM-dd'T'HH:mm:ss)", example = "2023-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        try {
            log.info("Generando reporte personalizado PDF desde {} hasta {}", startDate, endDate);
            byte[] pdfReport = reportGenerationService.generateCustomPdfReport(startDate, endDate);

            String filename = String.format("reporte_personalizado_%s_%s.pdf",
                startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                    .body(pdfReport);

        } catch (Exception e) {
            log.error("Error generando reporte personalizado PDF: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/excel/custom")
    @Operation(summary = "Descargar reporte personalizado en Excel",
               description = "Genera y descarga un reporte Excel para un período de fechas personalizado")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<byte[]> downloadCustomExcelReport(
            @Parameter(description = "Fecha de inicio", example = "2023-12-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin", example = "2023-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        try {
            log.info("Generando reporte personalizado Excel desde {} hasta {}", startDate, endDate);
            byte[] excelReport = reportGenerationService.generateCustomExcelReport(startDate, endDate);

            String filename = String.format("reporte_personalizado_%s_%s.xlsx",
                startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(excelReport);

        } catch (Exception e) {
            log.error("Error generando reporte personalizado Excel: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== ENDPOINTS PARA ENVÍO POR EMAIL ==========

    @PostMapping("/email/custom")
    @Operation(summary = "Enviar reporte personalizado por email",
               description = "Genera y envía un reporte personalizado por correo electrónico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte enviado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
        @ApiResponse(responseCode = "500", description = "Error enviando el reporte")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendCustomReportByEmail(
            @Parameter(description = "Email de destino", example = "usuario@empresa.com")
            @RequestParam String email,
            @Parameter(description = "Fecha de inicio", example = "2023-12-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin", example = "2023-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Incluir PDF", example = "true")
            @RequestParam(defaultValue = "true") boolean includePdf,
            @Parameter(description = "Incluir Excel", example = "true")
            @RequestParam(defaultValue = "true") boolean includeExcel) {

        try {
            log.info("Enviando reporte personalizado por email a {} desde {} hasta {}", email, startDate, endDate);

            reportSchedulerService.sendCustomReport(email, startDate, endDate, includePdf, includeExcel);

            return ResponseEntity.ok("Reporte enviado exitosamente a " + email);

        } catch (IllegalStateException e) {
            log.error("Configuración de email inválida: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error de configuración: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error enviando reporte personalizado: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error enviando reporte: " + e.getMessage());
        }
    }

    @PostMapping("/email/weekly/now")
    @Operation(summary = "Enviar reporte semanal inmediato",
               description = "Fuerza el envío inmediato del reporte semanal por correo (normalmente se envía automáticamente los lunes)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendWeeklyReportNow() {
        try {
            log.info("Enviando reporte semanal inmediato por solicitud manual");
            reportSchedulerService.sendWeeklyReport();
            return ResponseEntity.ok("Reporte semanal enviado exitosamente");
        } catch (Exception e) {
            log.error("Error enviando reporte semanal inmediato: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error enviando reporte: " + e.getMessage());
        }
    }

    @PostMapping("/email/monthly/now")
    @Operation(summary = "Enviar reporte mensual inmediato",
               description = "Fuerza el envío inmediato del reporte mensual por correo (normalmente se envía automáticamente el primer día del mes)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendMonthlyReportNow() {
        try {
            log.info("Enviando reporte mensual inmediato por solicitud manual");
            reportSchedulerService.sendMonthlyReport();
            return ResponseEntity.ok("Reporte mensual enviado exitosamente");
        } catch (Exception e) {
            log.error("Error enviando reporte mensual inmediato: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error enviando reporte: " + e.getMessage());
        }
    }

    // ========== ENDPOINTS DE CONFIGURACIÓN Y ESTADO ==========

    @GetMapping("/status")
    @Operation(summary = "Estado del sistema de reportes",
               description = "Retorna el estado actual del sistema de reportes automáticos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getReportSystemStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            // Aquí podrías agregar más verificaciones de estado
            status.put("reportesSemanalesHabilitados", true);  // Desde configuración
            status.put("reportesMensualesHabilitados", true);  // Desde configuración
            status.put("emailHabilitado", true);               // Desde configuración
            status.put("ultimaEjecucion", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            status.put("estado", "OPERACIONAL");

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            status.put("estado", "ERROR");
            status.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(status);
        }
    }

    @GetMapping("/schedule/info")
    @Operation(summary = "Información de programación",
               description = "Muestra información sobre cuándo se ejecutarán los próximos reportes automáticos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> getScheduleInfo() {
        Map<String, String> scheduleInfo = new HashMap<>();

        scheduleInfo.put("reporteSemanal", "Cada lunes a las 8:00 AM");
        scheduleInfo.put("reporteMensual", "Primer día de cada mes a las 9:00 AM");
        scheduleInfo.put("chequeoSalud", "Todos los días a las 6:00 AM");
        scheduleInfo.put("zonaHoraria", "Hora del servidor");
        scheduleInfo.put("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        return ResponseEntity.ok(scheduleInfo);
    }
}
