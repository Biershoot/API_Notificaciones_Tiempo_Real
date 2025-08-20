package com.alejandro.microservices.notifications.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que registra el historial de envío de notificaciones")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del log", example = "1")
    private Long id;

    @Column(name = "notification_id")
    @Schema(description = "ID de la notificación relacionada", example = "1")
    private Long notificationId;

    @Column(name = "username", nullable = false)
    @Schema(description = "Nombre de usuario destinatario", example = "user1", required = true)
    private String username;

    @Column(nullable = false)
    @Schema(description = "Operación realizada", example = "SEND_NOTIFICATION", required = true)
    private String operation;

    @Column(nullable = false)
    @Schema(description = "Canal de envío utilizado", example = "websocket",
            allowableValues = {"websocket", "email", "sms", "push"}, required = true)
    private String channel;

    @Column(nullable = false)
    @Schema(description = "Tipo de notificación", example = "INFO",
            allowableValues = {"INFO", "WARNING", "ERROR", "SUCCESS"}, required = true)
    private String type;

    @Column(nullable = false)
    @Schema(description = "Prioridad de la notificación", example = "NORMAL",
            allowableValues = {"LOW", "NORMAL", "HIGH", "URGENT"}, required = true)
    private String priority;

    @Builder.Default
    @Column(nullable = false)
    @Schema(description = "Indica si la notificación se envió exitosamente", example = "true", required = true)
    private boolean success = false;

    @Column(length = 1000)
    @Schema(description = "Mensaje de error en caso de fallo", example = "Connection timeout")
    private String errorMessage;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora de envío", example = "2023-12-19T10:30:00", required = true)
    private LocalDateTime sentAt;

    @Schema(description = "Tiempo de procesamiento en milisegundos", example = "150")
    private Long processingTimeMs;

    // Metadatos adicionales para análisis avanzado
    @Column(length = 500)
    @Schema(description = "Información adicional del contexto", example = "{\"source\":\"api\",\"version\":\"1.0\"}")
    private String metadata;

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }
}
