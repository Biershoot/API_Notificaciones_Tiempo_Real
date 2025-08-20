package com.alejandro.microservices.notifications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Estadísticas completas de notificaciones de un usuario")
public class NotificationStatsDto {

    @Schema(description = "Nombre del usuario", example = "user1")
    private String username;

    @Schema(description = "Total de notificaciones recibidas", example = "145")
    private long totalNotifications;

    @Schema(description = "Notificaciones no leídas", example = "12")
    private long unreadNotifications;

    @Schema(description = "Notificaciones leídas", example = "133")
    private long readNotifications;

    @Schema(description = "Porcentaje de notificaciones leídas", example = "91.7")
    private double readPercentage;

    @Schema(description = "Fecha de la primera notificación", example = "2023-11-15T09:30:00")
    private LocalDateTime firstNotificationDate;

    @Schema(description = "Fecha de la última notificación", example = "2023-12-19T14:45:00")
    private LocalDateTime lastNotificationDate;

    @Schema(description = "Distribución por tipo de notificación")
    private Map<String, Long> typeDistribution;

    @Schema(description = "Distribución por prioridad")
    private Map<String, Long> priorityDistribution;

    @Schema(description = "Notificaciones recibidas hoy", example = "5")
    private long todayNotifications;

    @Schema(description = "Notificaciones recibidas esta semana", example = "28")
    private long thisWeekNotifications;

    @Schema(description = "Notificaciones recibidas este mes", example = "94")
    private long monthNotifications;

    @Schema(description = "Promedio de notificaciones por día", example = "3.2")
    private double dailyAverage;

    @Schema(description = "Tipo de notificación más común", example = "INFO")
    private String mostCommonType;

    @Schema(description = "Prioridad más común", example = "NORMAL")
    private String mostCommonPriority;
}
