package com.cl.mdd.server.core.service.notification;

/**
 * Base exception for notification service integration
 */
public class NotificationServiceException extends RuntimeException {

    private final String code;

    public NotificationServiceException(String message, String code) {
        super(message);
        this.code = code;
    }

    public NotificationServiceException(String message, Throwable cause, String code) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

