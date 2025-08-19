package com.alejandro.microservices.notifications.controller;

import com.alejandro.microservices.notifications.model.Notification;
import com.alejandro.microservices.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 📩 Enviar una notificación
    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(
            @RequestParam String username,
            @RequestParam String message) {
        return ResponseEntity.ok(notificationService.sendNotification(username, message));
    }

    // 📋 Listar todas las notificaciones de un usuario
    @GetMapping("/{username}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable String username) {
        return ResponseEntity.ok(notificationService.getNotifications(username));
    }

    // 📬 Listar solo las no leídas
    @GetMapping("/{username}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable String username) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(username));
    }

    // ✅ Marcar una notificación como leída
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }
}
