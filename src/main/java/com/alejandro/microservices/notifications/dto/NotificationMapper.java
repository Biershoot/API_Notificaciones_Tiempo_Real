package com.alejandro.microservices.notifications.dto;

import com.alejandro.microservices.notifications.model.Notification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Componente para mapear entre entidades Notification y sus DTOs.
 * Se encarga de la conversión bidireccional entre objetos de modelo y DTOs.
 */
@Component
public class NotificationMapper {

    /**
     * Convierte una entidad Notification a un DTO de respuesta.
     *
     * @param notification La entidad a convertir
     * @return DTO con los datos de la notificación
     */
    public NotificationResponseDto toResponseDto(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationResponseDto.builder()
                .id(notification.getId())
                .username(notification.getUsername())
                .message(notification.getMessage())
                .title(notification.getTitle())
                .type(notification.getType())
                .priority(notification.getPriority())
                .timestamp(notification.getTimestamp())
                .read(notification.isRead())
                .actionUrl(notification.getActionUrl())
                .urgent(notification.isUrgent())
                .build();
    }

    /**
     * Convierte una lista de entidades Notification a una lista de DTOs.
     *
     * @param notifications Lista de entidades a convertir
     * @return Lista de DTOs
     */
    public List<NotificationResponseDto> toResponseDtoList(List<Notification> notifications) {
        return notifications.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Convierte un DTO de solicitud a una entidad Notification.
     *
     * @param requestDto DTO a convertir
     * @return Entidad Notification inicializada con los datos del DTO
     */
    public Notification toEntity(NotificationRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        Notification notification = Notification.builder()
                .username(requestDto.getUsername())
                .message(requestDto.getMessage())
                .type(requestDto.getType())
                .priority(requestDto.getPriority())
                .read(false)
                .build();

        // Establecer campos opcionales si están presentes
        if (requestDto.getTitle() != null) {
            notification.setTitle(requestDto.getTitle());
        }

        if (requestDto.getActionUrl() != null) {
            notification.setActionUrl(requestDto.getActionUrl());
        }

        return notification;
    }
}
