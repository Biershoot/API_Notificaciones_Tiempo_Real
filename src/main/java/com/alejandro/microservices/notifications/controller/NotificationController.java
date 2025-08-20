package com.alejandro.microservices.notifications.controller;

import com.alejandro.microservices.notifications.model.Notification;
import com.alejandro.microservices.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Controlador REST para la gestión de notificaciones básicas.
 *
 * Este controlador proporciona endpoints para enviar y recibir notificaciones,
 * así como para gestionar su estado de lectura. Las notificaciones se envían
 * automáticamente a los clientes conectados a través de WebSockets.
 *
 * @author Alejandro
 * @version 2.0
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Validated
@Tag(name = "Notificaciones", description = "API para gestión avanzada de notificaciones con persistencia y estado de lectura")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Envía una notificación en tiempo real a un usuario específico.
     * La notificación se persiste en la base de datos y se envía automáticamente
     * a través de WebSockets a los clientes conectados.
     *
     * @param username Nombre del usuario destinatario
     * @param message Mensaje de la notificación
     * @return La notificación creada y enviada
     */
    @PostMapping("/send")
    @Operation(
        summary = "Enviar una notificación en tiempo real",
        description = "Crea y envía una nueva notificación a un usuario específico. La notificación se envía automáticamente vía WebSocket a los clientes conectados."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación enviada exitosamente via REST y WebSocket"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Notification> sendNotification(
            @Parameter(description = "Nombre de usuario destinatario", required = true)
            @RequestParam @NotBlank(message = "El nombre de usuario no puede estar vacío") String username,
            @Parameter(description = "Mensaje de la notificación", required = true)
            @RequestParam @NotBlank(message = "El mensaje no puede estar vacío")
            @Size(min = 3, max = 500, message = "El mensaje debe tener entre 3 y 500 caracteres") String message) {
        return ResponseEntity.ok(notificationService.sendNotification(username, message));
    }

    /**
     * Obtiene todas las notificaciones de un usuario ordenadas por timestamp descendente.
     *
     * @param username Nombre del usuario
     * @return Lista de notificaciones del usuario
     */
    @GetMapping("/{username}")
    @Operation(
        summary = "Obtener todas las notificaciones del usuario",
        description = "Obtiene todas las notificaciones de un usuario específico ordenadas por timestamp descendente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<List<Notification>> getAllNotifications(
            @Parameter(description = "Nombre de usuario", required = true)
            @PathVariable @NotBlank(message = "El nombre de usuario no puede estar vacío") String username) {
        return ResponseEntity.ok(notificationService.getAllNotifications(username));
    }

    //  Obtener solo las notificaciones no leídas
    @GetMapping("/{username}/unread")
    @Operation(
        summary = "Obtener notificaciones no leídas",
        description = "Obtiene únicamente las notificaciones no leídas de un usuario específico ordenadas por timestamp descendente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de notificaciones no leídas obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @Parameter(description = "Nombre de usuario", required = true)
            @PathVariable @NotBlank(message = "El nombre de usuario no puede estar vacío") String username) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(username));
    }

    // 📊 Contar notificaciones no leídas
    @GetMapping("/{username}/unread/count")
    @Operation(
        summary = "Contar notificaciones no leídas",
        description = "Obtiene el número total de notificaciones no leídas de un usuario específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conteo obtenido exitosamente")
    })
    public ResponseEntity<Long> countUnreadNotifications(
            @Parameter(description = "Nombre de usuario", required = true)
            @PathVariable String username) {
        return ResponseEntity.ok(notificationService.countUnreadNotifications(username));
    }

    // ✅ Marcar una notificación específica como leída
    @PutMapping("/{id}/read")
    @Operation(
        summary = "Marcar notificación específica como leída",
        description = "Actualiza el estado de una notificación específica marcándola como leída"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación marcada como leída exitosamente"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    public ResponseEntity<Notification> markAsRead(
            @Parameter(description = "ID de la notificación", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    // ✅ Marcar todas las notificaciones como leídas
    @PostMapping("/{username}/mark-all-read")
    @Operation(
        summary = "Marcar todas las notificaciones como leídas",
        description = "Marca todas las notificaciones de un usuario específico como leídas de una sola vez"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Todas las notificaciones marcadas como leídas exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> markAllAsRead(
            @Parameter(description = "Nombre de usuario", required = true)
            @PathVariable @NotBlank(message = "El nombre de usuario no puede estar vacío") String username) {
        notificationService.markAllAsRead(username);
        return ResponseEntity.ok().build();
    }
}
