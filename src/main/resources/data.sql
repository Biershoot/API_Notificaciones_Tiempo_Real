-- ===== SCRIPT DE INICIALIZACIÓN DE BASE DE DATOS =====
-- Este archivo se ejecuta automáticamente al iniciar la aplicación

-- Insertar usuarios de prueba si no existen (H2 compatible)
INSERT INTO users (username, password, role) VALUES
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN'),
('user1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER'),
('user2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER');

-- Nota: La contraseña por defecto es "password" encriptada con BCrypt

-- Insertar notificaciones de prueba con los nuevos campos type y priority
INSERT INTO notifications (username, message, type, priority, timestamp, last_updated, is_read) VALUES
('admin', 'Sistema iniciado correctamente', 'SUCCESS', 'LOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
('admin', 'Actualización de seguridad disponible', 'WARNING', 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
('user1', 'Bienvenido al sistema de notificaciones', 'INFO', 'NORMAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
('user1', 'Tu perfil ha sido actualizado', 'SUCCESS', 'NORMAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
('user1', 'Error al procesar tu última solicitud', 'ERROR', 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
('user2', 'Notificación de mantenimiento programado', 'WARNING', 'NORMAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
('user2', 'Tu sesión expirará en 5 minutos', 'WARNING', 'URGENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
('user2', 'Configuración guardada exitosamente', 'SUCCESS', 'LOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true);
