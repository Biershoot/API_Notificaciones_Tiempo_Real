package com.alejandro.microservices.notifications.service;

import com.alejandro.microservices.notifications.exception.NotificationException;
import com.alejandro.microservices.notifications.model.Notification;
import com.alejandro.microservices.notifications.model.NotificationLog;
import com.alejandro.microservices.notifications.model.User;
import com.alejandro.microservices.notifications.repository.NotificationLogRepository;
import com.alejandro.microservices.notifications.repository.NotificationRepository;
import com.alejandro.microservices.notifications.repository.UserRepository;
import com.alejandro.microservices.notifications.metrics.NotificationMetrics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio principal para la gestión de notificaciones en la aplicación.
 *
 * Este servicio implementa la lógica de negocio para el envío y gestión de notificaciones,
 * incluyendo la persistencia en base de datos, el envío en tiempo real a través de WebSockets,
 * y la publicación opcional en Redis para arquitecturas distribuidas.
 *
 * Implementa patrones de diseño recomendados como:
 * - Transacciones adecuadas para garantizar la integridad de los datos
 * - Manejo centralizado de excepciones
 * - Métricas y monitoreo para observabilidad
 * - Caché para optimizar rendimiento
 * - Comunicación asíncrona cuando es apropiado
 *
 * @author Alejandro
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationLogRepository logRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationMetrics metrics;

    // Redis Publisher es opcional
    @Autowired(required = false)
    private RedisPublisher redisPublisher;

    @Value("${notification.websocket.destination.prefix:/topic/notifications}")
    private String websocketDestinationPrefix;

    @Value("${notification.cleanup.enabled:false}")
    private boolean cleanupEnabled;

    @Value("${notification.cleanup.days:30}")
    private int cleanupDays;

    /**
     * Envía una notificación básica a un usuario con tipo INFO y prioridad NORMAL.
     *
     * @param username Nombre del usuario destinatario
     * @param message Mensaje de la notificación
     * @return La notificación creada y enviada
     * @throws NotificationException si el usuario no existe o hay un error de envío
     */
    @Transactional
    public Notification sendNotification(
            @NotBlank(message = "El nombre de usuario no puede estar vacío") String username,
            @NotBlank(message = "El mensaje no puede estar vacío") String message) {
        return sendNotification(username, message, "INFO", "NORMAL");
    }

    /**
     * Envía una notificación con tipo y prioridad específicos a un usuario.
     *
     * Este método:
     * 1. Persiste la notificación en la base de datos
     * 2. Envía la notificación por WebSockets a los clientes conectados
     * 3. Publica la notificación en Redis (si está configurado)
     * 4. Registra la operación en el log de auditoría
     * 5. Actualiza las métricas del sistema
     *
     * @param username Nombre del usuario destinatario
     * @param message Mensaje de la notificación
     * @param type Tipo de notificación (INFO, WARNING, ERROR, SUCCESS)
     * @param priority Prioridad de la notificación (LOW, NORMAL, HIGH, URGENT)
     * @return La notificación creada y enviada
     * @throws NotificationException si el usuario no existe o hay un error de envío
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED,
                   rollbackFor = {Exception.class})
    public Notification sendNotification(
            @NotBlank(message = "El nombre de usuario no puede estar vacío") String username,
            @NotBlank(message = "El mensaje no puede estar vacío") String message,
            @NotBlank(message = "El tipo no puede estar vacío") String type,
            @NotBlank(message = "La prioridad no puede estar vacía") String priority) {

        // Iniciar timer para medir latencia
        var sendTimer = metrics.startNotificationSendTimer();
        long processingStartTime = System.currentTimeMillis();

        String channel = "websocket"; // Canal por defecto
        boolean success = false;
        String errorMessage = null;

        try {
            log.debug("Enviando notificación a usuario: {}, tipo: {}, prioridad: {}",
                     username, type, priority);

            // Validar tipo y prioridad
            validateNotificationType(type);
            validateNotificationPriority(priority);

            // Buscar usuario receptor
            User recipient = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NotificationException(
                        "Usuario no encontrado: " + username,
                        "USER_NOT_FOUND",
                        HttpStatus.NOT_FOUND.value()
                    ));

            // Crear la notificación
            Notification notification = Notification.builder()
                    .username(username)
                    .message(message)
                    .type(type)
                    .priority(priority)
                    .user(recipient)
                    .timestamp(LocalDateTime.now())
                    .read(false)
                    .build();

            // Si el mensaje es demasiado largo, añadir título predeterminado
            if (message.length() > 100 && !StringUtils.hasText(notification.getTitle())) {
                notification.setTitle(message.substring(0, 97) + "...");
            }

            // Persistir la notificación
            notification = notificationRepository.save(notification);

            // Enviar por WebSocket
            messagingTemplate.convertAndSendToUser(
                    username,
                    websocketDestinationPrefix,
                    notification
            );

            // Publicar en Redis si está disponible
            if (redisPublisher != null) {
                channel = "redis";
                redisPublisher.publish(notification);
            }

            // Registrar en el log de auditoría
            NotificationLog notificationLog = NotificationLog.builder()
                    .notificationId(notification.getId())
                    .username(username)
                    .operation("SEND")
                    .channel(channel)
                    .sentAt(LocalDateTime.now())
                    .processingTimeMs(System.currentTimeMillis() - processingStartTime)
                    .success(true)
                    .build();

            logRepository.save(notificationLog);

            // Actualizar métricas
            metrics.incrementSentCounter(type, priority);
            success = true;

            log.info("Notificación ID {} enviada exitosamente a usuario: {}",
                    notification.getId(), username);

            return notification;

        } catch (NotificationException ex) {
            errorMessage = ex.getMessage();
            log.error("Error al enviar notificación a {}: {}", username, errorMessage, ex);
            throw ex;
        } catch (DataAccessException ex) {
            errorMessage = "Error de acceso a datos: " + ex.getMessage();
            log.error("Error de base de datos al enviar notificación a {}: {}",
                     username, errorMessage, ex);
            throw new NotificationException(
                "Error al persistir la notificación: " + ex.getMessage(),
                "DATABASE_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
        } catch (Exception ex) {
            errorMessage = "Error inesperado: " + ex.getMessage();
            log.error("Error inesperado al enviar notificación a {}: {}",
                     username, errorMessage, ex);
            throw new NotificationException(
                "Error inesperado al enviar la notificación",
                "INTERNAL_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
        } finally {
            // Siempre registrar la métrica de latencia
            metrics.recordNotificationSendTime(sendTimer);

            // Si hubo error, registrar en el log de auditoría
            if (!success && errorMessage != null) {
                try {
                    NotificationLog errorLog = NotificationLog.builder()
                            .username(username)
                            .operation("SEND")
                            .channel(channel)
                            .sentAt(LocalDateTime.now())
                            .processingTimeMs(System.currentTimeMillis() - processingStartTime)
                            .success(false)
                            .errorMessage(errorMessage)
                            .build();

                    logRepository.save(errorLog);
                } catch (Exception ex) {
                    log.error("No se pudo registrar el error en el log de auditoría", ex);
                }
            }
        }
    }

    /**
     * Obtiene todas las notificaciones de un usuario ordenadas por fecha de creación descendente.
     *
     * @param username Nombre del usuario
     * @return Lista de notificaciones del usuario
     * @throws NotificationException si el usuario no existe
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "notificationsCache", key = "#username", unless = "#result.isEmpty()")
    public List<Notification> getAllNotifications(
            @NotBlank(message = "El nombre de usuario no puede estar vacío") String username) {

        log.debug("Obteniendo todas las notificaciones para el usuario: {}", username);

        validateUserExists(username);

        // Incrementar métrica de consulta
        metrics.incrementQueryCounter();

        return notificationRepository.findByUsernameOrderByTimestampDesc(username);
    }

    /**
     * Obtiene únicamente las notificaciones no leídas de un usuario.
     *
     * @param username Nombre del usuario
     * @return Lista de notificaciones no leídas
     * @throws NotificationException si el usuario no existe
     */
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(
            @NotBlank(message = "El nombre de usuario no puede estar vacío") String username) {

        log.debug("Obteniendo notificaciones no leídas para el usuario: {}", username);

        validateUserExists(username);

        // Incrementar métrica de consulta
        metrics.incrementQueryCounter();

        return notificationRepository.findByUsernameAndReadOrderByTimestampDesc(username, false);
    }

    /**
     * Cuenta el número de notificaciones no leídas de un usuario.
     *
     * @param username Nombre del usuario
     * @return Número de notificaciones no leídas
     * @throws NotificationException si el usuario no existe
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "unreadCountCache", key = "#username")
    public Long countUnreadNotifications(
            @NotBlank(message = "El nombre de usuario no puede estar vacío") String username) {

        log.debug("Contando notificaciones no leídas para el usuario: {}", username);

        validateUserExists(username);

        return notificationRepository.countByUsernameAndRead(username, false);
    }

    /**
     * Marca una notificación específica como leída.
     *
     * @param id ID de la notificación
     * @return La notificación actualizada
     * @throws NotificationException si la notificación no existe
     */
    @Transactional
    @CacheEvict(value = {"notificationsCache", "unreadCountCache"}, key = "#result.username")
    public Notification markAsRead(@NotNull(message = "El ID no puede ser nulo") Long id) {
        log.debug("Marcando notificación como leída: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationException(
                    "Notificación no encontrada con ID: " + id,
                    "NOTIFICATION_NOT_FOUND",
                    HttpStatus.NOT_FOUND.value()
                ));

        if (!notification.isRead()) {
            notification.markAsRead();
            notification = notificationRepository.save(notification);

            // Registrar en log de auditoría
                            NotificationLog log = NotificationLog.builder()
                        .notificationId(id)
                        .username(notification.getUsername())
                        .operation("MARK_READ")
                        .sentAt(LocalDateTime.now())
                        .success(true)
                        .build();

            logRepository.save(log);

            // Actualizar métrica
            metrics.incrementReadCounter(notification.getType());
        }

        return notification;
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas.
     *
     * @param username Nombre del usuario
     * @throws NotificationException si el usuario no existe
     */
    @Transactional
    @CacheEvict(value = {"notificationsCache", "unreadCountCache"}, key = "#username")
    public void markAllAsRead(
            @NotBlank(message = "El nombre de usuario no puede estar vacío") String username) {

        log.debug("Marcando todas las notificaciones como leídas para el usuario: {}", username);

        validateUserExists(username);

        List<Notification> unreadNotifications =
                notificationRepository.findByUsernameAndReadOrderByTimestampDesc(username, false);

        if (!unreadNotifications.isEmpty()) {
            unreadNotifications.forEach(Notification::markAsRead);
            notificationRepository.saveAll(unreadNotifications);

            // Registrar en log de auditoría
            NotificationLog log = NotificationLog.builder()
                    .username(username)
                    .operation("MARK_ALL_READ")
                    .sentAt(LocalDateTime.now())
                    .success(true)
                    .build();

            logRepository.save(log);

            // Actualizar métrica
            metrics.incrementBulkReadCounter(unreadNotifications.size());
        }
    }

    /**
     * Elimina notificaciones antiguas según la configuración del sistema.
     * Este método está pensado para ser ejecutado periódicamente por un programador.
     *
     * @return Número de notificaciones eliminadas
     */
    @Async
    @Transactional
    public CompletableFuture<Integer> cleanupOldNotifications() {
        if (!cleanupEnabled) {
            log.debug("Limpieza de notificaciones antiguas deshabilitada");
            return CompletableFuture.completedFuture(0);
        }

        log.info("Iniciando limpieza de notificaciones antiguas (más de {} días)", cleanupDays);

        LocalDateTime cutoffDate = LocalDateTime.now().minus(cleanupDays, ChronoUnit.DAYS);
        List<Notification> oldNotifications =
                notificationRepository.findByTimestampBefore(cutoffDate);

        int count = oldNotifications.size();

        if (count > 0) {
            log.info("Eliminando {} notificaciones antiguas", count);
            notificationRepository.deleteAll(oldNotifications);

            // Registrar en log de auditoría
            NotificationLog auditLog = NotificationLog.builder()
                    .operation("CLEANUP")
                    .sentAt(LocalDateTime.now())
                    .success(true)
                    .build();

            logRepository.save(auditLog);

            // Actualizar métrica
            metrics.incrementCleanupCounter(count);
        } else {
            log.info("No se encontraron notificaciones antiguas para eliminar");
        }

        return CompletableFuture.completedFuture(count);
    }

    /**
     * Verifica si un usuario existe en el sistema.
     *
     * @param username Nombre del usuario
     * @throws NotificationException si el usuario no existe
     */
    private void validateUserExists(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new NotificationException(
                "Usuario no encontrado: " + username,
                "USER_NOT_FOUND",
                HttpStatus.NOT_FOUND.value()
            );
        }
    }

    /**
     * Valida que el tipo de notificación sea uno de los permitidos.
     *
     * @param type Tipo de notificación
     * @throws NotificationException si el tipo no es válido
     */
    private void validateNotificationType(String type) {
        List<String> validTypes = List.of("INFO", "WARNING", "ERROR", "SUCCESS");
        if (!validTypes.contains(type)) {
            throw new NotificationException(
                "Tipo de notificación no válido: " + type + ". Valores permitidos: " + validTypes,
                "INVALID_NOTIFICATION_TYPE",
                HttpStatus.BAD_REQUEST.value()
            );
        }
    }

    /**
     * Valida que la prioridad de notificación sea una de las permitidas.
     *
     * @param priority Prioridad de notificación
     * @throws NotificationException si la prioridad no es válida
     */
    private void validateNotificationPriority(String priority) {
        List<String> validPriorities = List.of("LOW", "NORMAL", "HIGH", "URGENT");
        if (!validPriorities.contains(priority)) {
            throw new NotificationException(
                "Prioridad de notificación no válida: " + priority + ". Valores permitidos: " + validPriorities,
                "INVALID_NOTIFICATION_PRIORITY",
                HttpStatus.BAD_REQUEST.value()
            );
        }
    }

    /**
     * Obtiene las notificaciones de un usuario por su relación directa con el objeto User.
     * Método mejorado para mantener consistencia con el resto del servicio.
     *
     * @param username Nombre del usuario
     * @return Lista de notificaciones del usuario
     * @throws NotificationException si el usuario no existe
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotifications(@NotBlank(message = "El nombre de usuario no puede estar vacío") String username) {
        log.debug("Obteniendo notificaciones por relación directa para el usuario: {}", username);

        User recipient = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotificationException(
                    "Usuario no encontrado: " + username,
                    "USER_NOT_FOUND",
                    HttpStatus.NOT_FOUND.value()
                ));

        // Incrementar métrica de consulta
        metrics.incrementQueryCounter();

        return notificationRepository.findByUser(recipient);
    }

    /**
     * Elimina una notificación específica.
     *
     * @param id ID de la notificación a eliminar
     * @throws NotificationException si la notificación no existe
     */
    @Transactional
    @CacheEvict(value = {"notificationsCache", "unreadCountCache"}, key = "#result.username")
    public void deleteNotification(@NotNull(message = "El ID no puede ser nulo") Long id) {
        log.debug("Eliminando notificación con ID: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationException(
                    "Notificación no encontrada con ID: " + id,
                    "NOTIFICATION_NOT_FOUND",
                    HttpStatus.NOT_FOUND.value()
                ));

        notificationRepository.delete(notification);

        // Registrar en log de auditoría
        NotificationLog auditLog = NotificationLog.builder()
                .notificationId(id)
                .username(notification.getUsername())
                .operation("DELETE")
                .sentAt(LocalDateTime.now())
                .success(true)
                .build();

        logRepository.save(auditLog);

        log.info("Notificación ID {} eliminada exitosamente", id);
    }

    /**
     * Elimina todas las notificaciones de un usuario.
     *
     * @param username Nombre del usuario
     * @throws NotificationException si el usuario no existe
     */
    @Transactional
    @CacheEvict(value = {"notificationsCache", "unreadCountCache"}, key = "#username")
    public void deleteAllNotifications(@NotBlank(message = "El nombre de usuario no puede estar vacío") String username) {
        log.debug("Eliminando todas las notificaciones para el usuario: {}", username);

        validateUserExists(username);

        List<Notification> notifications = notificationRepository.findByUsernameOrderByTimestampDesc(username);

        if (!notifications.isEmpty()) {
            int count = notifications.size();
            notificationRepository.deleteAll(notifications);

            // Registrar en log de auditoría
            NotificationLog auditLog = NotificationLog.builder()
                    .username(username)
                    .operation("DELETE_ALL")
                    .sentAt(LocalDateTime.now())
                    .success(true)
                    .build();

            logRepository.save(auditLog);

            log.info("Se eliminaron {} notificaciones del usuario {}", count, username);
        } else {
            log.info("No se encontraron notificaciones para eliminar del usuario {}", username);
        }
    }

    /**
     * Busca notificaciones por texto en el mensaje o título.
     *
     * @param username Nombre del usuario
     * @param searchText Texto a buscar
     * @return Lista de notificaciones que contienen el texto buscado
     * @throws NotificationException si el usuario no existe
     */
    @Transactional(readOnly = true)
    public List<Notification> searchNotifications(
            @NotBlank(message = "El nombre de usuario no puede estar vacío") String username,
            @NotBlank(message = "El texto de búsqueda no puede estar vacío") String searchText) {

        log.debug("Buscando notificaciones para el usuario: {} con texto: {}", username, searchText);

        validateUserExists(username);

        // Incrementar métrica de consulta
        metrics.incrementQueryCounter();

        return notificationRepository.findByUsernameAndMessageOrTitleContainingIgnoreCase(username, searchText);
    }

    /**
     * Obtiene notificaciones filtradas por tipo y/o prioridad.
     *
     * @param username Nombre del usuario
     * @param type Tipo de notificación (opcional)
     * @param priority Prioridad de notificación (opcional)
     * @return Lista de notificaciones filtradas
     * @throws NotificationException si el usuario no existe o los parámetros son inválidos
     */
    @Transactional(readOnly = true)
    public List<Notification> getFilteredNotifications(
            @NotBlank(message = "El nombre de usuario no puede estar vacío") String username,
            String type,
            String priority) {

        log.debug("Obteniendo notificaciones filtradas para usuario: {}, tipo: {}, prioridad: {}",
                 username, type, priority);

        validateUserExists(username);

        // Validar parámetros opcionales si están presentes
        if (type != null && !type.isEmpty()) {
            validateNotificationType(type);
        }

        if (priority != null && !priority.isEmpty()) {
            validateNotificationPriority(priority);
        }

        // Incrementar métrica de consulta
        metrics.incrementQueryCounter();

        // Aplicar filtros según los parámetros proporcionados
        if (type != null && !type.isEmpty() && priority != null && !priority.isEmpty()) {
            return notificationRepository.findByUsernameAndTypeAndPriorityOrderByTimestampDesc(
                    username, type, priority);
        } else if (type != null && !type.isEmpty()) {
            return notificationRepository.findByUsernameAndTypeOrderByTimestampDesc(
                    username, type);
        } else if (priority != null && !priority.isEmpty()) {
            return notificationRepository.findByUsernameAndPriorityOrderByTimestampDesc(
                    username, priority);
        } else {
            return getAllNotifications(username);
        }
    }

    /**
     * Actualiza las métricas globales del sistema.
     */
    public void updateGlobalMetrics() {
        log.debug("Actualizando métricas globales del sistema");
        
        // Actualizar contador total de usuarios
        long totalUsers = userRepository.count();
        metrics.setTotalUsers(totalUsers);
        
        // Actualizar contador total de notificaciones activas
        long totalActiveNotifications = notificationRepository.count();
        
        // Actualizar contador total de notificaciones no leídas
        long totalUnreadNotifications = notificationRepository.countByReadFalse();
        
        log.debug("Métricas globales actualizadas: {} usuarios, {} notificaciones activas, {} no leídas",
                totalUsers, totalActiveNotifications, totalUnreadNotifications);
    }
}
