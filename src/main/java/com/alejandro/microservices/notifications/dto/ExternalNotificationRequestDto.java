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
    private String email;

    @Schema(description = "Número de teléfono del destinatario", example = "+1234567890")
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

    /**
     * Valida que los campos requeridos estén presentes según el canal.
     */
    public boolean isValidForChannel() {
        if (channels != null && !channels.isEmpty()) {
            // Validación para múltiples canales
            return channels.stream().allMatch(this::isValidForSingleChannel);
        } else {
            return isValidForSingleChannel(channel);
        }
    }

    private boolean isValidForSingleChannel(String channel) {
        return switch (channel.toUpperCase()) {
            case "EMAIL" -> email != null && !email.trim().isEmpty() && isValidEmail(email);
            case "SMS" -> phone != null && !phone.trim().isEmpty() && isValidPhone(phone);
            case "APP", "ALL" -> true; // No requiere campos adicionales
            default -> false;
        };
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^\\+[1-9]\\d{1,14}$");
    }

    /**
     * Obtiene el mensaje de error de validación para el canal.
     */
    public String getValidationErrorMessage() {
        if (channels != null && !channels.isEmpty()) {
            for (String ch : channels) {
                if (!isValidForSingleChannel(ch)) {
                    return getChannelValidationMessage(ch);
                }
            }
        } else {
            if (!isValidForSingleChannel(channel)) {
                return getChannelValidationMessage(channel);
            }
        }
        return null;
    }

    private String getChannelValidationMessage(String channel) {
        return switch (channel.toUpperCase()) {
            case "EMAIL" -> {
                if (email == null || email.trim().isEmpty()) {
                    yield "El campo 'email' es requerido para el canal EMAIL";
                } else {
                    yield "El formato del email no es válido. Debe ser: usuario@dominio.com";
                }
            }
            case "SMS" -> {
                if (phone == null || phone.trim().isEmpty()) {
                    yield "El campo 'phone' es requerido para el canal SMS";
                } else {
                    yield "El formato del teléfono no es válido. Debe incluir código de país: +1234567890";
                }
            }
            default -> "Canal no soportado: " + channel;
        };
    }
}
