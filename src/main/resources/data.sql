-- ===== SCRIPT DE INICIALIZACIÓN DE BASE DE DATOS =====
-- Este archivo se ejecuta automáticamente al iniciar la aplicación

-- Crear base de datos si no existe (ya manejado en application.properties)
-- CREATE DATABASE IF NOT EXISTS notifications_db;

-- Insertar usuarios de prueba si no existen
INSERT IGNORE INTO users (username, password, role) VALUES
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN'),
('user1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER'),
('user2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER');

-- Nota: La contraseña por defecto es "password" encriptada con BCrypt
