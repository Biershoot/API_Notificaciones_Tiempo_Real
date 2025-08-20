package com.alejandro.microservices.notifications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Schema(description = "DTOs para respuestas de reportes estadísticos")
public class ReportDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Estadísticas generales del sistema")
    public static class GeneralStats {
        @Schema(description = "Total de notificaciones enviadas", example = "1250")
        private long totalNotifications;

        @Schema(description = "Total de notificaciones exitosas", example = "1198")
        private long successfulNotifications;

        @Schema(description = "Total de notificaciones fallidas", example = "52")
        private long failedNotifications;

        @Schema(description = "Tasa de éxito en porcentaje", example = "95.84")
        private double successRate;

        @Schema(description = "Tiempo promedio de procesamiento en ms", example = "142.5")
        private double averageProcessingTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Estadísticas por canal de comunicación")
    public static class ChannelStats {
        @Schema(description = "Nombre del canal", example = "websocket")
        private String channel;

        @Schema(description = "Total de notificaciones por canal", example = "450")
        private long count;

        @Schema(description = "Notificaciones exitosas por canal", example = "430")
        private long successfulCount;

        @Schema(description = "Tasa de éxito del canal", example = "95.56")
        private double successRate;

        @Schema(description = "Tiempo promedio de procesamiento del canal", example = "125.3")
        private double averageProcessingTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Estadísticas por usuario")
    public static class UserStats {
        @Schema(description = "ID del usuario", example = "user1")
        private String userId;

        @Schema(description = "Total de notificaciones del usuario", example = "85")
        private long count;

        @Schema(description = "Última notificación enviada", example = "2023-12-19")
        private String lastNotification;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Punto de datos para tendencias temporales")
    public static class TrendPoint {
        @Schema(description = "Etiqueta temporal", example = "2023-12-19")
        private String label;

        @Schema(description = "Valor del punto", example = "125")
        private long value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Estadísticas por tipo de notificación")
    public static class TypeStats {
        @Schema(description = "Tipo de notificación", example = "INFO")
        private String type;

        @Schema(description = "Cantidad por tipo", example = "320")
        private long count;

        @Schema(description = "Porcentaje del total", example = "25.6")
        private double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Estadísticas por prioridad")
    public static class PriorityStats {
        @Schema(description = "Nivel de prioridad", example = "HIGH")
        private String priority;

        @Schema(description = "Cantidad por prioridad", example = "180")
        private long count;

        @Schema(description = "Porcentaje del total", example = "14.4")
        private double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Reporte completo de estadísticas")
    public static class ComprehensiveReport {
        @Schema(description = "Estadísticas generales")
        private GeneralStats generalStats;

        @Schema(description = "Estadísticas por canal")
        private List<ChannelStats> channelStats;

        @Schema(description = "Top usuarios más activos")
        private List<UserStats> topUsers;

        @Schema(description = "Estadísticas por tipo")
        private List<TypeStats> typeStats;

        @Schema(description = "Estadísticas por prioridad")
        private List<PriorityStats> priorityStats;

        @Schema(description = "Tendencia de los últimos 7 días")
        private List<TrendPoint> weeklyTrend;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Reporte diario específico")
    public static class DailyReport {
        @Schema(description = "Fecha del reporte", example = "2023-12-19")
        private LocalDate date;

        @Schema(description = "Total de notificaciones del día", example = "145")
        private long totalNotifications;

        @Schema(description = "Notificaciones exitosas del día", example = "139")
        private long successfulNotifications;

        @Schema(description = "Distribución por hora del día")
        private List<TrendPoint> hourlyDistribution;

        @Schema(description = "Distribución por canal del día")
        private Map<String, Long> channelDistribution;

        @Schema(description = "Distribución por tipo del día")
        private Map<String, Long> typeDistribution;
    }
}
