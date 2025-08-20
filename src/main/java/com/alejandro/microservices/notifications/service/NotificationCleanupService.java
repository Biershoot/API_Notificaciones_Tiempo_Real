package com.alejandro.microservices.notifications.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCleanupService {

    private final NotificationServiceAdvanced notificationService;

    @Value("${notifications.cleanup.days-old:30}")
    private int defaultDaysOld;

    @Value("${notifications.cleanup.enabled:true}")
    private boolean cleanupEnabled;

    // Ejecutar limpieza automática todos los días a las 2:00 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledCleanup() {
        if (!cleanupEnabled) {
            log.debug("Limpieza automática deshabilitada");
            return;
        }

        try {
            log.info("Iniciando limpieza automática de notificaciones antiguas (>{} días)", defaultDaysOld);

            int deletedCount = notificationService.cleanupOldNotifications(defaultDaysOld).join();

            if (deletedCount > 0) {
                log.info("Limpieza completada: {} notificaciones eliminadas", deletedCount);
            } else {
                log.debug("No se encontraron notificaciones para limpiar");
            }

        } catch (Exception e) {
            log.error("Error durante la limpieza automática de notificaciones: {}", e.getMessage(), e);
        }
    }

    // Limpieza manual con parámetros específicos
    public int performCleanup(int daysOld) {
        log.info("Iniciando limpieza manual de notificaciones antiguas (>{} días)", daysOld);

        try {
            int deletedCount = notificationService.cleanupOldNotifications(daysOld).join();
            log.info("Limpieza manual completada: {} notificaciones eliminadas", deletedCount);
            return deletedCount;
        } catch (Exception e) {
            log.error("Error durante la limpieza manual: {}", e.getMessage(), e);
            throw e;
        }
    }
}
