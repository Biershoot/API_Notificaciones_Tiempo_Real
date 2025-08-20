package com.alejandro.microservices.notifications.repository;

import com.alejandro.microservices.notifications.model.Notification;
import com.alejandro.microservices.notifications.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para acceder y manipular entidades Notification en la base de datos.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Métodos con User como parámetro
    List<Notification> findByUser(User user);
    List<Notification> findByUserAndReadOrderByTimestampDesc(User user, boolean read);

    // Métodos con username como parámetro
    List<Notification> findByUsernameOrderByTimestampDesc(String username);
    List<Notification> findByUsernameAndReadOrderByTimestampDesc(String username, boolean read);
    Long countByUsernameAndRead(String username, boolean read);

    // Búsqueda por timestamp
    List<Notification> findByTimestampBefore(LocalDateTime timestamp);

    // Búsquedas por tipo y prioridad
    List<Notification> findByUsernameAndTypeOrderByTimestampDesc(String username, String type);
    List<Notification> findByUsernameAndPriorityOrderByTimestampDesc(String username, String priority);
    List<Notification> findByUsernameAndTypeAndPriorityOrderByTimestampDesc(String username, String type, String priority);

    // Búsqueda por texto
    @Query("SELECT n FROM Notification n WHERE n.username = :username AND (LOWER(n.message) LIKE LOWER(CONCAT('%', :searchText, '%')) OR LOWER(n.title) LIKE LOWER(CONCAT('%', :searchText, '%'))) ORDER BY n.timestamp DESC")
    List<Notification> findByUsernameAndMessageOrTitleContainingIgnoreCase(@Param("username") String username, @Param("searchText") String searchText);

    // Operaciones de actualización
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.username = :username")
    void markAllAsRead(@Param("username") String username);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.id = :id")
    void markAsReadById(@Param("id") Long id);

    // Consultas con paginación
    Page<Notification> findByUsernameOrderByTimestampDesc(String username, Pageable pageable);
    Page<Notification> findByUsernameAndReadOrderByTimestampDesc(String username, boolean read, Pageable pageable);

    // Métodos adicionales necesarios para NotificationServiceAdvanced
    Long countByUsername(String username);
    Long countByUsernameAndType(String username, String type);
    Long countByUsernameAndPriority(String username, String priority);

    // Búsqueda por rango de fechas
    List<Notification> findByUsernameAndTimestampBetweenOrderByTimestampDesc(
            String username, LocalDateTime startDate, LocalDateTime endDate);

    // Contar por tipo y prioridad
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.username = :username AND n.type = :type")
    Long countNotificationsByUsernameAndType(@Param("username") String username, @Param("type") String type);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.username = :username AND n.priority = :priority")
    Long countNotificationsByUsernameAndPriority(@Param("username") String username, @Param("priority") String priority);

    // Contar notificaciones no leídas globalmente
    Long countByReadFalse();
}
