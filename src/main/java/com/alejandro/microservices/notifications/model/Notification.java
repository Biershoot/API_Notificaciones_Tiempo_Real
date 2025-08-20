package com.alejandro.microservices.notifications.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa una notificación en el sistema.
 *
 * Las notificaciones se envían a usuarios específicos y pueden tener diferentes
 * tipos y prioridades. Cada notificación mantiene un estado de lectura y se registra
 * automáticamente su fecha de creación y última actualización.
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notification_username", columnList = "username"),
    @Index(name = "idx_notification_read", columnList = "is_read"),
    @Index(name = "idx_notification_timestamp", columnList = "timestamp")
})
@Schema(description = "Entidad que representa una notificación del sistema")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la notificación", example = "1")
    private Long id;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Column(nullable = false, length = 50)
    @Schema(description = "Nombre de usuario destinatario", example = "user1", required = true)
    private String username;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 500, message = "El mensaje no puede exceder los 500 caracteres")
    @Column(nullable = false, length = 500)
    @Schema(description = "Contenido del mensaje de la notificación", example = "Tu pedido ha sido procesado exitosamente")
    private String message;

    @NotNull(message = "El tipo de notificación es obligatorio")
    @Column(nullable = false, length = 20)
    @Schema(description = "Tipo de notificación", example = "INFO", allowableValues = {"INFO", "WARNING", "ERROR", "SUCCESS"})
    @Builder.Default
    private String type = "INFO";

    @NotNull(message = "La prioridad de la notificación es obligatoria")
    @Column(nullable = false, length = 20)
    @Schema(description = "Prioridad de la notificación", example = "NORMAL", allowableValues = {"LOW", "NORMAL", "HIGH", "URGENT"})
    @Builder.Default
    private String priority = "NORMAL";

    @PastOrPresent(message = "La fecha de creación no puede ser futura")
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Schema(description = "Fecha y hora de creación de la notificación", example = "2023-12-19T10:30:00")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @UpdateTimestamp
    @Column(nullable = false)
    @Schema(description = "Fecha y hora de última actualización", example = "2023-12-19T10:35:00")
    private LocalDateTime lastUpdated;

    @Column(name = "is_read", nullable = false)
    @Schema(description = "Estado de lectura de la notificación", example = "false")
    @Builder.Default
    private boolean read = false;

    @Column(length = 100)
    @Schema(description = "Título opcional para la notificación", example = "Actualización de pedido")
    private String title;

    @Column(length = 255)
    @Schema(description = "URL opcional relacionada con la notificación", example = "/orders/123")
    private String actionUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @Schema(description = "Usuario destinatario de la notificación")
    private User user;

    /**
     * Marca la notificación como leída.
     */
    @JsonIgnore
    public void markAsRead() {
        this.read = true;
    }

    /**
     * Verifica si la notificación es urgente.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public boolean isUrgent() {
        return "URGENT".equals(this.priority) || "HIGH".equals(this.priority);
    }

    /**
     * Método de pre-persistencia que asegura que se establezca la fecha de creación
     * si no se ha establecido explícitamente.
     */
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
