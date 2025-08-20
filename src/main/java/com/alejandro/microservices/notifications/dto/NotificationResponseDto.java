package com.alejandro.microservices.notifications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para enviar información de notificaciones al cliente.
 * Contiene todos los datos necesarios para representar una notificación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para notificación")
public class NotificationResponseDto {

    @Schema(description = "ID único de la notificación", example = "1")
    private Long id;

    @Schema(description = "Nombre de usuario destinatario", example = "user1")
    private String username;

    @Schema(description = "Título de la notificación", example = "Actualización de pedido")
    private String title;

    @Schema(description = "Contenido del mensaje", example = "Tu pedido ha sido procesado exitosamente")
    private String message;

    @Schema(description = "Tipo de notificación", example = "INFO", allowableValues = {"INFO", "WARNING", "ERROR", "SUCCESS"})
    private String type;

    @Schema(description = "Prioridad de la notificación", example = "NORMAL", allowableValues = {"LOW", "NORMAL", "HIGH", "URGENT"})
    private String priority;

    @Schema(description = "Fecha y hora de creación", example = "2023-12-19T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Estado de lectura", example = "false")
    private boolean read;

    @Schema(description = "URL de acción relacionada", example = "/orders/123")
    private String actionUrl;

    @Schema(description = "Indica si la notificación es urgente", example = "true")
    private boolean urgent;
}
