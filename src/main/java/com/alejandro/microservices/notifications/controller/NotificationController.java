package com.alejandro.microservices.notifications.controller;

import com.alejandro.microservices.notifications.model.Notification;
import com.alejandro.microservices.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "API para gestión de notificaciones en tiempo real con WebSockets")
public class NotificationController {

    private final NotificationService notificationService;

    // 📩 Enviar una notificación (ahora con WebSocket automático)
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
            @RequestParam String username,
            @Parameter(description = "Mensaje de la notificación", required = true)
            @RequestParam String message) {
        return ResponseEntity.ok(notificationService.sendNotification(username, message));
    }

    // 📋 Listar todas las notificaciones de un usuario
    @GetMapping("/{username}")
    @Operation(
        summary = "Obtener notificaciones del usuario",
        description = "Obtiene todas las notificaciones de un usuario específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<List<Notification>> getNotifications(
            @Parameter(description = "Nombre de usuario", required = true)
            @PathVariable String username) {
        return ResponseEntity.ok(notificationService.getNotifications(username));
    }

    // 📬 Listar solo las no leídas
    @GetMapping("/{username}/unread")
    @Operation(
        summary = "Obtener notificaciones no leídas",
        description = "Obtiene únicamente las notificaciones no leídas de un usuario específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de notificaciones no leídas obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @Parameter(description = "Nombre de usuario", required = true)
            @PathVariable String username) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(username));
    }

    // ✅ Marcar una notificación como leída
    @PutMapping("/{id}/read")
    @Operation(
        summary = "Marcar notificación como leída",
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
}
