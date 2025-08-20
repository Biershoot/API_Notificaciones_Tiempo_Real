package com.alejandro.microservices.notifications.service;

import com.alejandro.microservices.notifications.dto.*;
import com.alejandro.microservices.notifications.model.Notification;
import com.alejandro.microservices.notifications.model.User;
import com.alejandro.microservices.notifications.repository.NotificationRepository;
import com.alejandro.microservices.notifications.repository.UserRepository;
import com.alejandro.microservices.notifications.metrics.NotificationMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Servicio avanzado para la gestión de notificaciones.
 * Proporciona funcionalidades extendidas como paginación, estadísticas y envío con prioridad.
 */
@Service
@Slf4j
public class NotificationServiceAdvanced {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationMetrics metrics;

    // Redis Publisher es opcional
    @Autowired(required = false)
    private RedisPublisher redisPublisher;

    /**
     * Constructor con inyección de dependencias.
     */
    @Autowired
    public NotificationServiceAdvanced(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            NotificationMapper notificationMapper,
            SimpMessagingTemplate messagingTemplate,
            NotificationMetrics metrics) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
        this.messagingTemplate = messagingTemplate;
        this.metrics = metrics;
    }

    /**
     * Crea una notificación avanzada con tipo y prioridad.
     *
     * @param requestDto DTO con los datos de la notificación
     * @return DTO con la notificación creada
     */
    @Transactional
    public NotificationResponseDto createAdvancedNotification(NotificationRequestDto requestDto) {
        var sendTimer = metrics.startNotificationSendTimer();

        try {
            User recipient = userRepository.findByUsername(requestDto.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + requestDto.getUsername()));

            Notification notification = Notification.builder()
                    .username(requestDto.getUsername())
                    .message(requestDto.getMessage())
                    .type(requestDto.getType())
                    .priority(requestDto.getPriority())
                    .user(recipient)
                    .timestamp(LocalDateTime.now())
                    .read(false)
                    .build();

            // Añadir título si no lo tiene y el mensaje es largo
            if (requestDto.getTitle() != null) {
                notification.setTitle(requestDto.getTitle());
            } else if (requestDto.getMessage().length() > 100) {
                notification.setTitle(requestDto.getMessage().substring(0, 97) + "...");
            }

            // Añadir URL de acción si está presente
            if (requestDto.getActionUrl() != null) {
                notification.setActionUrl(requestDto.getActionUrl());
            }

            var dbTimer = metrics.startDatabaseQueryTimer();
            Notification saved = notificationRepository.save(notification);
            metrics.recordDatabaseQueryTime(dbTimer);

            // Métricas avanzadas
            metrics.incrementNotificationsSent(requestDto.getUsername());
            metrics.incrementUnreadNotifications();
            metrics.recordNotificationMessageSize(requestDto.getMessage().getBytes().length);

            // Envío por WebSocket/Redis según prioridad
            if ("URGENT".equals(requestDto.getPriority())) {
                sendUrgentNotification(saved);
            } else {
                sendRegularNotification(saved);
            }

            return notificationMapper.toResponseDto(saved);

        } catch (Exception e) {
            log.error("Error al crear notificación avanzada: {}", e.getMessage(), e);
            metrics.incrementFailedNotifications();
            throw e;
        } finally {
            metrics.recordNotificationSendTime(sendTimer);
        }
    }

    /**
     * Obtiene las notificaciones de un usuario con paginación.
     */
    public PagedNotificationResponseDto getNotificationsPaginated(String username, Pageable pageable) {
        var dbTimer = metrics.startDatabaseQueryTimer();

        try {
            Page<Notification> notificationsPage = notificationRepository
                    .findByUsernameOrderByTimestampDesc(username, pageable);

            List<NotificationResponseDto> notificationDtos = notificationMapper
                    .toResponseDtoList(notificationsPage.getContent());

            return PagedNotificationResponseDto.builder()
                    .notifications(notificationDtos)
                    .currentPage(notificationsPage.getNumber())
                    .pageSize(notificationsPage.getSize())
                    .totalElements(notificationsPage.getTotalElements())
                    .totalPages(notificationsPage.getTotalPages())
                    .first(notificationsPage.isFirst())
                    .last(notificationsPage.isLast())
                    .build();
        } finally {
            metrics.recordDatabaseQueryTime(dbTimer);
        }
    }

    // Métodos privados para envío de notificaciones
    private void sendUrgentNotification(Notification notification) {
        // Envío urgente con alta prioridad
        log.info("Enviando notificación URGENTE a usuario: {}", notification.getUsername());

        // Enviar vía WebSocket con marcador de urgente
        messagingTemplate.convertAndSendToUser(
                notification.getUsername(),
                "/topic/urgent-notifications",
                notificationMapper.toResponseDto(notification)
        );

        // Publicar en Redis para garantizar entrega en clusters
        if (redisPublisher != null) {
            redisPublisher.publish(notification);
        }
    }

    private void sendRegularNotification(Notification notification) {
        // Envío normal
        log.info("Enviando notificación regular a usuario: {}", notification.getUsername());

        // Enviar vía WebSocket
        messagingTemplate.convertAndSendToUser(
                notification.getUsername(),
                "/topic/notifications",
                notificationMapper.toResponseDto(notification)
        );

        // Publicar en Redis si está disponible
        if (redisPublisher != null && !"LOW".equals(notification.getPriority())) {
            // Solo notificaciones de prioridad media o alta
            redisPublisher.publish(notification);
        }
    }

    /**
     * Obtiene estadísticas de notificaciones para un usuario.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStats(String username) {
        Map<String, Object> stats = new HashMap<>();

        long totalCount = notificationRepository.countByUsername(username);
        long unreadCount = notificationRepository.countByUsernameAndRead(username, false);

        stats.put("totalNotifications", totalCount);
        stats.put("unreadNotifications", unreadCount);
        stats.put("readPercentage", totalCount > 0 ? (totalCount - unreadCount) * 100.0 / totalCount : 0);

        // Contar por tipo
        Map<String, Long> byType = new HashMap<>();
        byType.put("INFO", notificationRepository.countByUsernameAndType(username, "INFO"));
        byType.put("WARNING", notificationRepository.countByUsernameAndType(username, "WARNING"));
        byType.put("ERROR", notificationRepository.countByUsernameAndType(username, "ERROR"));
        byType.put("SUCCESS", notificationRepository.countByUsernameAndType(username, "SUCCESS"));
        stats.put("byType", byType);

        // Contar por prioridad
        Map<String, Long> byPriority = new HashMap<>();
        byPriority.put("LOW", notificationRepository.countByUsernameAndPriority(username, "LOW"));
        byPriority.put("NORMAL", notificationRepository.countByUsernameAndPriority(username, "NORMAL"));
        byPriority.put("HIGH", notificationRepository.countByUsernameAndPriority(username, "HIGH"));
        byPriority.put("URGENT", notificationRepository.countByUsernameAndPriority(username, "URGENT"));
        stats.put("byPriority", byPriority);

        return stats;
    }

    /**
     * Busca notificaciones por texto en el mensaje o título.
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> searchNotifications(String username, String searchText) {
        List<Notification> notifications = notificationRepository
                .findByUsernameAndMessageOrTitleContainingIgnoreCase(username, searchText);

        return notificationMapper.toResponseDtoList(notifications);
    }

    /**
     * Obtiene notificaciones urgentes no leídas.
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getUrgentUnreadNotifications(String username) {
        List<Notification> notifications = notificationRepository.findByUsernameAndReadOrderByTimestampDesc(username, false)
                .stream()
                .filter(n -> "URGENT".equals(n.getPriority()) || "HIGH".equals(n.getPriority()))
                .collect(Collectors.toList());

        return notificationMapper.toResponseDtoList(notifications);
    }

    /**
     * Obtiene notificaciones filtradas por tipo y prioridad.
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByTypeAndPriority(String username, String type, String priority) {
        List<Notification> notifications;

        if (type != null && priority != null) {
            notifications = notificationRepository.findByUsernameAndTypeAndPriorityOrderByTimestampDesc(
                    username, type, priority);
        } else if (type != null) {
            notifications = notificationRepository.findByUsernameAndTypeOrderByTimestampDesc(
                    username, type);
        } else if (priority != null) {
            notifications = notificationRepository.findByUsernameAndPriorityOrderByTimestampDesc(
                    username, priority);
        } else {
            notifications = notificationRepository.findByUsernameOrderByTimestampDesc(username);
        }

        return notificationMapper.toResponseDtoList(notifications);
    }

    /**
     * Obtiene notificaciones en un rango de fechas.
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByDateRange(
            String username, LocalDateTime startDate, LocalDateTime endDate) {

        List<Notification> notifications = notificationRepository.findByUsernameAndTimestampBetweenOrderByTimestampDesc(
                username, startDate, endDate);

        return notificationMapper.toResponseDtoList(notifications);
    }

    /**
     * Marca múltiples notificaciones como leídas.
     */
    @Transactional
    public void markMultipleAsRead(List<Long> notificationIds) {
        for (Long id : notificationIds) {
            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Notificación no encontrada con ID: " + id));

            if (!notification.isRead()) {
                notification.markAsRead();
                notificationRepository.save(notification);
                metrics.incrementReadCounter(notification.getType());
            }
        }
    }

    /**
     * Limpia notificaciones antiguas basadas en días.
     */
    @Transactional
    public CompletableFuture<Integer> cleanupOldNotifications(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        List<Notification> oldNotifications = notificationRepository.findByTimestampBefore(cutoffDate);

        int count = oldNotifications.size();

        if (count > 0) {
            log.info("Eliminando {} notificaciones antiguas", count);
            notificationRepository.deleteAll(oldNotifications);
            metrics.incrementCleanupCounter(count);
        }

        return CompletableFuture.completedFuture(count);
    }
}
