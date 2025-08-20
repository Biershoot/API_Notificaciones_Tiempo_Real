package com.alejandro.microservices.notifications.controller;

import com.alejandro.microservices.notifications.dto.ExternalNotificationRequestDto;
import com.alejandro.microservices.notifications.model.Notification;
import com.alejandro.microservices.notifications.service.NotificationDispatcher;
import com.alejandro.microservices.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador para manejar notificaciones externas (SMS, Email, etc.).
 */
@RestController
@RequestMapping("/api/external/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notificaciones Externas", description = "Endpoints para enviar notificaciones por SMS, Email y otros canales")
public class ExternalNotificationController {

    private final NotificationService notificationService;
    private final NotificationDispatcher notificationDispatcher;

    @PostMapping("/send")
    @Operation(summary = "Enviar notificación externa", description = "Envía una notificación por el canal especificado (SMS, Email, APP, ALL)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación enviada exitosamente",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Map<String, Object>> sendExternalNotification(
            @Parameter(description = "Datos de la notificación externa", required = true)
            @Valid @RequestBody ExternalNotificationRequestDto request) {

        try {
            log.info("Recibida solicitud de notificación externa para usuario: {} por canal: {}", 
                    request.getUsername(), request.getChannel());

            // Crear la notificación
            Notification notification = Notification.builder()
                    .username(request.getUsername())
                    .message(request.getMessage())
                    .title(request.getTitle())
                    .type(request.getType())
                    .priority(request.getPriority())
                    .actionUrl(request.getActionUrl())
                    .build();

            // Guardar la notificación en la base de datos
            Notification savedNotification = notificationService.sendNotification(
                    notification.getUsername(),
                    notification.getMessage(),
                    notification.getType(),
                    notification.getPriority()
            );

            // Despachar por el canal especificado
            boolean sent = false;
            Map<String, Boolean> results = null;

            if (request.getChannels() != null && !request.getChannels().isEmpty()) {
                // Enviar a múltiples canales
                results = notificationDispatcher.dispatchToChannels(savedNotification, request.getChannels());
                sent = results.values().stream().anyMatch(Boolean::booleanValue);
            } else {
                // Enviar a un solo canal
                sent = notificationDispatcher.dispatchNotification(savedNotification, request.getChannel());
            }

            Map<String, Object> response = Map.of(
                    "success", sent,
                    "notificationId", savedNotification.getId(),
                    "channel", request.getChannel(),
                    "message", sent ? "Notificación enviada exitosamente" : "Error enviando notificación"
            );

            if (results != null) {
                response = Map.of(
                        "success", sent,
                        "notificationId", savedNotification.getId(),
                        "channels", results,
                        "message", sent ? "Notificación enviada exitosamente" : "Error enviando notificación"
                );
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error enviando notificación externa: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Error enviando notificación: " + e.getMessage()
                    ));
        }
    }

    @PostMapping("/send/bulk")
    @Operation(summary = "Enviar notificaciones masivas", description = "Envía múltiples notificaciones a diferentes usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificaciones enviadas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Map<String, Object>> sendBulkNotifications(
            @Parameter(description = "Lista de notificaciones a enviar", required = true)
            @Valid @RequestBody List<ExternalNotificationRequestDto> requests) {

        try {
            log.info("Recibida solicitud de envío masivo de {} notificaciones", requests.size());

            int successCount = 0;
            int failureCount = 0;

            for (ExternalNotificationRequestDto request : requests) {
                try {
                    Notification notification = Notification.builder()
                            .username(request.getUsername())
                            .message(request.getMessage())
                            .title(request.getTitle())
                            .type(request.getType())
                            .priority(request.getPriority())
                            .actionUrl(request.getActionUrl())
                            .build();

                    Notification savedNotification = notificationService.sendNotification(
                            notification.getUsername(),
                            notification.getMessage(),
                            notification.getType(),
                            notification.getPriority()
                    );
                    boolean sent = notificationDispatcher.dispatchNotification(savedNotification, request.getChannel());

                    if (sent) {
                        successCount++;
                    } else {
                        failureCount++;
                    }

                } catch (Exception e) {
                    log.error("Error procesando notificación para usuario {}: {}", 
                            request.getUsername(), e.getMessage());
                    failureCount++;
                }
            }

            return ResponseEntity.ok(Map.of(
                    "total", requests.size(),
                    "success", successCount,
                    "failures", failureCount,
                    "message", String.format("Procesadas %d notificaciones: %d exitosas, %d fallidas", 
                            requests.size(), successCount, failureCount)
            ));

        } catch (Exception e) {
            log.error("Error en envío masivo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Error en envío masivo: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/channels/status")
    @Operation(summary = "Estado de canales", description = "Verifica el estado de configuración de todos los canales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de canales obtenido exitosamente")
    })
    public ResponseEntity<Map<String, Boolean>> getChannelsStatus() {
        Map<String, Boolean> status = notificationDispatcher.getChannelsStatus();
        return ResponseEntity.ok(status);
    }

    @PostMapping("/test/sms")
    @Operation(summary = "Probar envío SMS", description = "Envía un SMS de prueba")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SMS de prueba enviado"),
            @ApiResponse(responseCode = "400", description = "Número de teléfono inválido"),
            @ApiResponse(responseCode = "500", description = "Error enviando SMS")
    })
    public ResponseEntity<Map<String, Object>> testSms(
            @Parameter(description = "Número de teléfono para la prueba", required = true)
            @RequestParam String phoneNumber) {

        try {
            boolean sent = notificationDispatcher.dispatchNotification(
                    Notification.builder()
                            .username("test")
                            .message("Este es un SMS de prueba del sistema de notificaciones")
                            .title("SMS de Prueba")
                            .type("INFO")
                            .priority("NORMAL")
                            .build(),
                    "SMS"
            );

            return ResponseEntity.ok(Map.of(
                    "success", sent,
                    "phoneNumber", phoneNumber,
                    "message", sent ? "SMS de prueba enviado exitosamente" : "Error enviando SMS de prueba"
            ));

        } catch (Exception e) {
            log.error("Error en SMS de prueba: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Error enviando SMS de prueba: " + e.getMessage()
                    ));
        }
    }

    @PostMapping("/test/email")
    @Operation(summary = "Probar envío Email", description = "Envía un email de prueba")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email de prueba enviado"),
            @ApiResponse(responseCode = "400", description = "Email inválido"),
            @ApiResponse(responseCode = "500", description = "Error enviando email")
    })
    public ResponseEntity<Map<String, Object>> testEmail(
            @Parameter(description = "Dirección de email para la prueba", required = true)
            @RequestParam String email) {

        try {
            boolean sent = notificationDispatcher.dispatchNotification(
                    Notification.builder()
                            .username("test")
                            .message("Este es un email de prueba del sistema de notificaciones")
                            .title("Email de Prueba")
                            .type("INFO")
                            .priority("NORMAL")
                            .build(),
                    "EMAIL"
            );

            return ResponseEntity.ok(Map.of(
                    "success", sent,
                    "email", email,
                    "message", sent ? "Email de prueba enviado exitosamente" : "Error enviando email de prueba"
            ));

        } catch (Exception e) {
            log.error("Error en email de prueba: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Error enviando email de prueba: " + e.getMessage()
                    ));
        }
    }
}
