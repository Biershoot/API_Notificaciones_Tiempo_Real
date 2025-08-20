package com.alejandro.microservices.notifications.exception;

import lombok.Getter;

@Getter
public class NotificationException extends RuntimeException {
    private final String errorCode;
    private final int httpStatus;

    public NotificationException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public NotificationException(String message, String errorCode, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}

// Excepciones específicas
class UserNotFoundException extends NotificationException {
    public UserNotFoundException(String username) {
        super("Usuario no encontrado: " + username, "USER_NOT_FOUND", 404);
    }
}

class NotificationNotFoundException extends NotificationException {
    public NotificationNotFoundException(Long id) {
        super("Notificación no encontrada: " + id, "NOTIFICATION_NOT_FOUND", 404);
    }
}

class InvalidNotificationDataException extends NotificationException {
    public InvalidNotificationDataException(String message) {
        super("Datos de notificación inválidos: " + message, "INVALID_DATA", 400);
    }
}
