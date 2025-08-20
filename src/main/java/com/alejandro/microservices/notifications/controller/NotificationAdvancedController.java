package com.alejandro.microservices.notifications.controller;

import com.alejandro.microservices.notifications.dto.*;
import com.alejandro.microservices.notifications.service.NotificationServiceAdvanced;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador avanzado para la gestión de notificaciones con funcionalidades extendidas.
 * Proporciona endpoints para filtrado avanzado, búsqueda, paginación y estadísticas.
 */
@RestController
@RequestMapping("/api/v2/notifications")
@Tag(name = "Notificaciones V2", description = "API avanzada para gestión completa de notificaciones con filtros, paginación y estadísticas")
public class NotificationAdvancedController {

    private final NotificationServiceAdvanced notificationService;

    /**
     * Constructor con inyección de dependencias.
     */
    @Autowired
    public NotificationAdvancedController(NotificationServiceAdvanced notificationService) {
        this.notificationService = notificationService;
    }

    // 📩 Crear notificación avanzada con tipo y prioridad
    @PostMapping
    @Operation(
        summary = "Crear notificación avanzada",
        description = "Crea una nueva notificación con tipo, prioridad y envío automático según criticidad"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<NotificationResponseDto> createNotification(
            @Valid @RequestBody NotificationRequestDto requestDto) {
        return ResponseEntity.ok(notificationService.createAdvancedNotification(requestDto));
    }

    // 📄 Obtener notificaciones con paginación
    @GetMapping("/{username}/paginated")
    @Operation(
        summary = "Obtener notificaciones paginadas",
        description = "Obtiene las notificaciones de un usuario con paginación y ordenamiento"
    )
    public ResponseEntity<PagedNotificationResponseDto> getNotificationsPaginated(
            @Parameter(description = "Nombre de usuario", required = true)
            @PathVariable String username,
            @Parameter(description = "Número de página (base 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(notificationService.getNotificationsPaginated(username, pageable));
    }

    // 📊 Obtener estadísticas del usuario
    @GetMapping("/{username}/stats")
    @Operation(
        summary = "Obtener estadísticas de notificaciones",
        description = "Obtiene estadísticas completas de notificaciones del usuario incluyendo conteos y porcentajes"
    )
    public ResponseEntity<Map<String, Object>> getUserStats(
            @Parameter(description = "Nombre de usuario", required = true)
            @PathVariable String username) {
        return ResponseEntity.ok(notificationService.getUserStats(username));
    }

    // 🔍 Buscar notificaciones por texto
    @GetMapping("/{username}/search")
    @Operation(
        summary = "Buscar notificaciones por texto",
        description = "Busca notificaciones que contengan el término especificado en el mensaje"
    )
    public ResponseEntity<List<NotificationResponseDto>> searchNotifications(
            @Parameter(description = "Nombre de usuario", required = true)
            @PathVariable String username,
            @Parameter(description = "Texto a buscar", required = true)
            @RequestParam String searchText) {
        return ResponseEntity.ok(notificationService.searchNotifications(username, searchText));
    }

    // 🚨 Obtener notificaciones urgentes no leídas
    @GetMapping("/{username}/urgent-unread")
    @Operation(
        summary = "Obtener notificaciones urgentes no leídas",
        description = "Obtiene las notificaciones no leídas de prioridad alta o urgente"
    )
    public ResponseEntity<List<NotificationResponseDto>> getUrgentUnreadNotifications(
            @Parameter(description = "Nombre de usuario", required = true)
            @PathVariable String username) {
        return ResponseEntity.ok(notificationService.getUrgentUnreadNotifications(username));
    }

    // 🔖 Filtrar notificaciones por tipo y prioridad
    @GetMapping("/{username}/filter")
    @Operation(
        summary = "Filtrar notificaciones por tipo y prioridad",
        description = "Obtiene notificaciones filtradas por tipo y/o prioridad"
    )
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByTypeAndPriority(
            @Parameter(description = "Nombre de usuario", required = true)
            @PathVariable String username,
            @Parameter(description = "Tipo de notificación (INFO, WARNING, ERROR, SUCCESS)", required = false)
            @RequestParam(required = false) String type,
            @Parameter(description = "Prioridad de notificación (LOW, NORMAL, HIGH, URGENT)", required = false)
            @RequestParam(required = false) String priority) {
        return ResponseEntity.ok(notificationService.getNotificationsByTypeAndPriority(username, type, priority));
    }

    // 📅 Filtrar notificaciones por rango de fechas
    @GetMapping("/{username}/date-range")
    @Operation(
        summary = "Filtrar notificaciones por rango de fechas",
        description = "Obtiene notificaciones entre dos fechas especificadas"
    )
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByDateRange(
            @Parameter(description = "Nombre de usuario", required = true)
            @PathVariable String username,
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-ddTHH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin (formato: yyyy-MM-ddTHH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(notificationService.getNotificationsByDateRange(username, startDate, endDate));
    }

    // ✅ Marcar múltiples notificaciones como leídas
    @PutMapping("/mark-multiple-read")
    @Operation(
        summary = "Marcar múltiples notificaciones como leídas",
        description = "Marca como leídas varias notificaciones especificadas por sus IDs"
    )
    public ResponseEntity<Void> markMultipleAsRead(
            @Parameter(description = "Lista de IDs de notificaciones", required = true)
            @RequestBody List<Long> notificationIds) {
        notificationService.markMultipleAsRead(notificationIds);
        return ResponseEntity.ok().build();
    }

    // 🗑️ Limpieza de notificaciones antiguas
    @DeleteMapping("/cleanup")
    @Operation(
        summary = "Limpiar notificaciones antiguas",
        description = "Elimina notificaciones anteriores al número de días especificado"
    )
    public ResponseEntity<Integer> cleanupOldNotifications(
            @Parameter(description = "Número de días de antigüedad", required = true, example = "30")
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(notificationService.cleanupOldNotifications(days).join());
    }
}
