-- Migración para crear la tabla de logs históricos de notificaciones
-- Compatible con H2 Database

CREATE TABLE IF NOT EXISTS notification_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'INFO',
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    success BOOLEAN NOT NULL DEFAULT FALSE,
    error_message VARCHAR(1000),
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processing_time_ms BIGINT,
    metadata VARCHAR(500)
);

-- Crear índices para optimizar consultas de reportes
CREATE INDEX IF NOT EXISTS idx_notification_logs_user_id ON notification_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_notification_logs_channel ON notification_logs(channel);
CREATE INDEX IF NOT EXISTS idx_notification_logs_type ON notification_logs(type);
CREATE INDEX IF NOT EXISTS idx_notification_logs_priority ON notification_logs(priority);
CREATE INDEX IF NOT EXISTS idx_notification_logs_success ON notification_logs(success);
CREATE INDEX IF NOT EXISTS idx_notification_logs_sent_at ON notification_logs(sent_at);

-- Índices compuestos para consultas complejas de reportes
CREATE INDEX IF NOT EXISTS idx_notification_logs_date_success ON notification_logs(sent_at, success);
CREATE INDEX IF NOT EXISTS idx_notification_logs_user_date ON notification_logs(user_id, sent_at);
CREATE INDEX IF NOT EXISTS idx_notification_logs_channel_success ON notification_logs(channel, success);
CREATE INDEX IF NOT EXISTS idx_notification_logs_type_priority ON notification_logs(type, priority);
