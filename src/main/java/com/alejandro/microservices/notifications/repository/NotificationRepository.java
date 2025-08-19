package com.alejandro.microservices.notifications.repository;

import com.alejandro.microservices.notifications.model.Notification;
import com.alejandro.microservices.notifications.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Métodos existentes con User
    List<Notification> findByRecipient(User user);
    List<Notification> findByRecipientAndReadFalse(User user);

    // Nuevos métodos con username directo
    List<Notification> findByUsernameOrderByTimestampDesc(String username);

    List<Notification> findByUsernameAndReadFalseOrderByTimestampDesc(String username);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.username = :username")
    void markAllAsRead(@Param("username") String username);

    // Método adicional para marcar una notificación específica como leída
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.id = :id")
    void markAsReadById(@Param("id") Long id);

    // Contar notificaciones no leídas
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.username = :username AND n.read = false")
    long countUnreadByUsername(@Param("username") String username);
}
