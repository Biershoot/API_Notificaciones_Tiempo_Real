package com.alejandro.microservices.notifications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para solicitudes de notificaciones externas (SMS, Email, etc.).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud de notificación externa")
public class ExternalNotificationRequestDto {

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Schema(description = "Mensaje de la notificación", example = "Tu pedido ha sido confirmado", required = true)
    private String message;

    @Schema(description = "Título de la notificación", example = "Confirmación de Pedido")
    private String title;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Schema(description = "Nombre de usuario destinatario", example = "user1", required = true)
    private String username;

    @Schema(description = "Dirección de email del destinatario", example = "user1@example.com")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Formato de email inválido")
    private String email;

    @Schema(description = "Número de teléfono del destinatario", example = "+1234567890")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Formato de teléfono inválido (debe incluir código de país)")
    private String phone;

    @Schema(description = "Canal de envío", example = "EMAIL", allowableValues = {"APP", "EMAIL", "SMS", "ALL"})
    @Builder.Default
    private String channel = "APP";

    @Schema(description = "Lista de canales múltiples", example = "[\"EMAIL\", \"SMS\"]")
    private List<String> channels;

    @Schema(description = "Tipo de notificación", example = "INFO", allowableValues = {"INFO", "WARNING", "ERROR", "SUCCESS"})
    @Builder.Default
    private String type = "INFO";

    @Schema(description = "Prioridad de la notificación", example = "NORMAL", allowableValues = {"LOW", "NORMAL", "HIGH", "URGENT"})
    @Builder.Default
    private String priority = "NORMAL";

    @Schema(description = "URL de acción opcional", example = "https://app.example.com/orders/123")
    private String actionUrl;

    @Schema(description = "Metadatos adicionales en formato JSON", example = "{\"orderId\": \"123\", \"amount\": 99.99}")
    private String metadata;
}
