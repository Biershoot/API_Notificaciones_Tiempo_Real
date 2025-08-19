package com.alejandro.microservices.notifications.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa una notificación del sistema")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la notificación", example = "1")
    private Long id;

    @Schema(description = "Contenido del mensaje de la notificación", example = "Tu pedido ha sido procesado exitosamente")
    private String message;

    @Builder.Default
    @Schema(description = "Estado de lectura de la notificación", example = "false")
    private boolean read = false;

    @Schema(description = "Fecha y hora de creación de la notificación", example = "2023-12-19T10:30:00")
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Schema(description = "Usuario destinatario de la notificación")
    private User recipient;
}
