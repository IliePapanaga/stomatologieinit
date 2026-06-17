package com.cl.mdd.server.core.service.notification.impl;

import com.cl.mdd.server.core.service.notification.Notification;
import com.cl.mdd.server.core.service.notification.NotificationService;
import com.cl.mdd.server.core.service.notification.client.NotificationServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Simple implementation of {@link NotificationService} that delegates all operations to {@link NotificationServiceClient}
 */
@Service("MDD-NotificationService")
public class NotificationServiceImpl implements NotificationService {

    private final NotificationServiceClient notificationServiceClient;

    @Autowired
    public NotificationServiceImpl(final NotificationServiceClient notificationServiceClient) {
        this.notificationServiceClient = notificationServiceClient;
    }

    @Override
    public void send(Notification notification) {
        notificationServiceClient.send(notification);
    }
}
