package com.alejandro.microservices.notifications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir solicitudes de creación de notificaciones.
 * Contiene todos los campos necesarios para crear una nueva notificación.
 */
@Schema(description = "DTO para solicitud de creación de notificación")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre de usuario destinatario", example = "user1", required = true)
    private String username;

    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    @Schema(description = "Título opcional de la notificación", example = "Actualización de pedido")
    private String title;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 500, message = "El mensaje no puede exceder 500 caracteres")
    @Schema(description = "Contenido del mensaje de la notificación", example = "Tu pedido ha sido procesado exitosamente", required = true)
    private String message;

    @Pattern(regexp = "INFO|WARNING|ERROR|SUCCESS", message = "Tipo debe ser uno de: INFO, WARNING, ERROR, SUCCESS")
    @Schema(description = "Tipo de notificación", example = "INFO", allowableValues = {"INFO", "WARNING", "ERROR", "SUCCESS"})
    @Builder.Default
    private String type = "INFO";

    @Pattern(regexp = "LOW|NORMAL|HIGH|URGENT", message = "Prioridad debe ser una de: LOW, NORMAL, HIGH, URGENT")
    @Schema(description = "Prioridad de la notificación", example = "NORMAL", allowableValues = {"LOW", "NORMAL", "HIGH", "URGENT"})
    @Builder.Default
    private String priority = "NORMAL";

    @Schema(description = "URL opcional relacionada con la acción", example = "/orders/123")
    private String actionUrl;
}
