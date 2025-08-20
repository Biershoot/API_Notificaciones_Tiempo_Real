package com.alejandro.microservices.notifications.repository;

import com.alejandro.microservices.notifications.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    // ========== CONSULTAS POR FECHA ==========

    @Query("SELECT COUNT(n) FROM NotificationLog n WHERE DATE(n.sentAt) = :date")
    long countByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(n) FROM NotificationLog n WHERE DATE(n.sentAt) = :date AND n.success = true")
    long countSuccessfulByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(n) FROM NotificationLog n WHERE n.sentAt BETWEEN :startDate AND :endDate")
    long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // ========== CONSULTAS POR CANAL ==========

    @Query("SELECT n.channel, COUNT(n) FROM NotificationLog n GROUP BY n.channel")
    List<Object[]> countByChannel();

    @Query("SELECT n.channel, COUNT(n) FROM NotificationLog n WHERE n.success = true GROUP BY n.channel")
    List<Object[]> countSuccessfulByChannel();

    // ========== CONSULTAS POR USUARIO ==========

    @Query("SELECT n.username, COUNT(n) FROM NotificationLog n GROUP BY n.username ORDER BY COUNT(n) DESC")
    List<Object[]> countByUser();

    @Query("SELECT COUNT(n) FROM NotificationLog n WHERE n.username = :username AND DATE(n.sentAt) = :date")
    long countByUserAndDate(@Param("username") String username, @Param("date") LocalDate date);

    // ========== CONSULTAS POR TIPO Y PRIORIDAD ==========

    @Query("SELECT n.type, COUNT(n) FROM NotificationLog n GROUP BY n.type")
    List<Object[]> countByType();

    @Query("SELECT n.priority, COUNT(n) FROM NotificationLog n GROUP BY n.priority")
    List<Object[]> countByPriority();

    @Query("SELECT n.type, n.priority, COUNT(n) FROM NotificationLog n GROUP BY n.type, n.priority")
    List<Object[]> countByTypeAndPriority();

    // ========== ANÁLISIS DE RENDIMIENTO ==========

    @Query("SELECT AVG(n.processingTimeMs) FROM NotificationLog n WHERE n.processingTimeMs IS NOT NULL")
    Double getAverageProcessingTime();

    @Query("SELECT n.channel, AVG(n.processingTimeMs) FROM NotificationLog n WHERE n.processingTimeMs IS NOT NULL GROUP BY n.channel")
    List<Object[]> getAverageProcessingTimeByChannel();

    // ========== ANÁLISIS DE ERRORES ==========

    @Query("SELECT COUNT(n) FROM NotificationLog n WHERE n.success = false")
    long countFailures();

    @Query("SELECT (COUNT(n) * 100.0 / (SELECT COUNT(nl) FROM NotificationLog nl)) FROM NotificationLog n WHERE n.success = true")
    Double getSuccessRate();

    @Query("SELECT n.channel, (COUNT(CASE WHEN n.success = true THEN 1 END) * 100.0 / COUNT(n)) FROM NotificationLog n GROUP BY n.channel")
    List<Object[]> getSuccessRateByChannel();

    // ========== TENDENCIAS TEMPORALES ==========

    @Query("SELECT DATE(n.sentAt) as date, COUNT(n) as count FROM NotificationLog n WHERE n.sentAt >= :fromDate GROUP BY DATE(n.sentAt) ORDER BY DATE(n.sentAt)")
    List<Object[]> getDailyTrend(@Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT HOUR(n.sentAt) as hour, COUNT(n) as count FROM NotificationLog n WHERE DATE(n.sentAt) = :date GROUP BY HOUR(n.sentAt) ORDER BY HOUR(n.sentAt)")
    List<Object[]> getHourlyTrend(@Param("date") LocalDate date);

    // ========== CONSULTAS PARA REPORTES AUTOMÁTICOS ==========

    @Query("SELECT n FROM NotificationLog n WHERE n.sentAt >= :startDate")
    List<NotificationLog> findBySentAtGreaterThanEqual(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT n FROM NotificationLog n WHERE n.sentAt BETWEEN :startDate AND :endDate")
    List<NotificationLog> findBySentAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
