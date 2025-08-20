-- Migración para agregar campos type y priority a la tabla notifications
-- Compatible con H2 Database

ALTER TABLE notifications ADD COLUMN IF NOT EXISTS type VARCHAR(20) NOT NULL DEFAULT 'INFO';
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL';

-- Crear índices para mejorar rendimiento en consultas por tipo y prioridad
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications(type);
CREATE INDEX IF NOT EXISTS idx_notifications_priority ON notifications(priority);
CREATE INDEX IF NOT EXISTS idx_notifications_username_type ON notifications(username, type);
CREATE INDEX IF NOT EXISTS idx_notifications_username_priority ON notifications(username, priority);
CREATE INDEX IF NOT EXISTS idx_notifications_username_read_priority ON notifications(username, read, priority);
