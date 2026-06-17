package com.cl.sns.server.core.service;

import com.cl.sns.server.core.model.api.notification.SendNotificationRequest;

import javax.validation.Valid;

/**
 * Notification service interface
 * <p />
 */
public interface NotificationService {
    void send(@Valid SendNotificationRequest sendNotificationRequest);
}
