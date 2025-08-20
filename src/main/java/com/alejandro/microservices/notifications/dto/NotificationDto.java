package com.alejandro.microservices.notifications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DTO base para notificaciones.
 * Contiene campos comunes para la transferencia de datos de notificaciones.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO base para notificaciones")
public class NotificationDto {

    @Schema(description = "ID único de la notificación", example = "1")
    private Long id;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre de usuario destinatario", example = "user1", required = true)
    private String username;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 500, message = "El mensaje no puede exceder los 500 caracteres")
    @Schema(description = "Contenido del mensaje de la notificación", example = "Tu pedido ha sido procesado exitosamente")
    private String message;

    @NotNull(message = "El tipo de notificación es obligatorio")
    @Schema(description = "Tipo de notificación", example = "INFO", allowableValues = {"INFO", "WARNING", "ERROR", "SUCCESS"})
    @Builder.Default
    private String type = "INFO";

    @NotNull(message = "La prioridad de la notificación es obligatoria")
    @Schema(description = "Prioridad de la notificación", example = "NORMAL", allowableValues = {"LOW", "NORMAL", "HIGH", "URGENT"})
    @Builder.Default
    private String priority = "NORMAL";

    @Schema(description = "Fecha y hora de creación de la notificación", example = "2023-12-19T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Estado de lectura de la notificación", example = "false")
    @Builder.Default
    private boolean read = false;

    @Schema(description = "Título opcional para la notificación", example = "Actualización de pedido")
    private String title;

    @Schema(description = "URL opcional relacionada con la notificación", example = "/orders/123")
    private String actionUrl;

    @Schema(description = "Indica si la notificación es urgente", example = "false")
    public boolean isUrgent() {
        return "URGENT".equals(this.priority) || "HIGH".equals(this.priority);
    }
}
