package com.cl.mdd.server.core.service.notification.client;

import com.cl.mdd.server.core.service.notification.Notification;

/**
 * Client of Notification service that supports sending notifications
 */
public interface NotificationServiceClient {

    void send(Notification notification);
}
