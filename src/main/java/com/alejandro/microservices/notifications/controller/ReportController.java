package com.alejandro.microservices.notifications.controller;

import com.alejandro.microservices.notifications.dto.ReportDTOs.*;
import com.alejandro.microservices.notifications.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reportes y Estadísticas", description = "Endpoints para generar reportes históricos y estadísticas del sistema de notificaciones")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/general")
    @Operation(summary = "Obtener estadísticas generales",
               description = "Retorna estadísticas generales del sistema incluyendo totales, tasas de éxito y tiempos de procesamiento")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<GeneralStats> getGeneralStats() {
        return ResponseEntity.ok(reportService.getGeneralStats());
    }

    @GetMapping("/channels")
    @Operation(summary = "Estadísticas por canal de comunicación",
               description = "Retorna estadísticas detalladas agrupadas por canal (websocket, email, sms, etc.)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ChannelStats>> getChannelStats() {
        return ResponseEntity.ok(reportService.getChannelStats());
    }

    @GetMapping("/users/top")
    @Operation(summary = "Top usuarios más activos",
               description = "Retorna los usuarios que más notificaciones han recibido")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserStats>> getTopUsers(
            @Parameter(description = "Número máximo de usuarios a retornar", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(reportService.getTopUsers(limit));
    }

    @GetMapping("/types")
    @Operation(summary = "Estadísticas por tipo de notificación",
               description = "Retorna distribución de notificaciones por tipo (INFO, WARNING, ERROR, SUCCESS)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<TypeStats>> getTypeStats() {
        return ResponseEntity.ok(reportService.getTypeStats());
    }

    @GetMapping("/priorities")
    @Operation(summary = "Estadísticas por prioridad",
               description = "Retorna distribución de notificaciones por prioridad (LOW, NORMAL, HIGH, URGENT)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<PriorityStats>> getPriorityStats() {
        return ResponseEntity.ok(reportService.getPriorityStats());
    }

    @GetMapping("/trends/weekly")
    @Operation(summary = "Tendencia semanal",
               description = "Retorna la tendencia de notificaciones enviadas en los últimos 7 días")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<TrendPoint>> getWeeklyTrend() {
        return ResponseEntity.ok(reportService.getWeeklyTrend());
    }

    @GetMapping("/daily/{date}")
    @Operation(summary = "Reporte diario específico",
               description = "Retorna estadísticas detalladas para una fecha específica")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<DailyReport> getDailyReport(
            @Parameter(description = "Fecha del reporte en formato YYYY-MM-DD", example = "2023-12-19")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.getDailyReport(date));
    }

    @GetMapping("/daily/{date}/count")
    @Operation(summary = "Conteo diario simple",
               description = "Retorna el número total de notificaciones enviadas en una fecha específica")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Long> getDailyCount(
            @Parameter(description = "Fecha en formato YYYY-MM-DD", example = "2023-12-19")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.getDailyCount(date));
    }

    @GetMapping("/users/{userId}/daily/{date}")
    @Operation(summary = "Conteo diario por usuario",
               description = "Retorna el número de notificaciones enviadas a un usuario específico en una fecha")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.name)")
    public ResponseEntity<Long> getUserDailyCount(
            @Parameter(description = "ID del usuario", example = "user1")
            @PathVariable String userId,
            @Parameter(description = "Fecha en formato YYYY-MM-DD", example = "2023-12-19")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.getUserDailyCount(userId, date));
    }

    @GetMapping("/performance")
    @Operation(summary = "Métricas de rendimiento",
               description = "Retorna métricas avanzadas de rendimiento del sistema")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics() {
        return ResponseEntity.ok(reportService.getPerformanceMetrics());
    }

    @GetMapping("/comprehensive")
    @Operation(summary = "Reporte completo",
               description = "Retorna un reporte completo con todas las estadísticas principales del sistema")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComprehensiveReport> getComprehensiveReport() {
        return ResponseEntity.ok(reportService.getComprehensiveReport());
    }

    @GetMapping("/export/daily/{date}")
    @Operation(summary = "Exportar reporte diario",
               description = "Retorna datos estructurados para exportación de reportes diarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> exportDailyReport(
            @Parameter(description = "Fecha para exportar", example = "2023-12-19")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        DailyReport report = reportService.getDailyReport(date);

        // Estructurar datos para exportación
        Map<String, Object> exportData = Map.of(
            "reportDate", date.toString(),
            "summary", Map.of(
                "totalNotifications", report.getTotalNotifications(),
                "successfulNotifications", report.getSuccessfulNotifications(),
                "successRate", report.getTotalNotifications() > 0 ?
                    (report.getSuccessfulNotifications() * 100.0 / report.getTotalNotifications()) : 0.0
            ),
            "hourlyDistribution", report.getHourlyDistribution(),
            "channelDistribution", report.getChannelDistribution(),
            "typeDistribution", report.getTypeDistribution(),
            "generatedAt", java.time.LocalDateTime.now().toString()
        );

        return ResponseEntity.ok(exportData);
    }
}
