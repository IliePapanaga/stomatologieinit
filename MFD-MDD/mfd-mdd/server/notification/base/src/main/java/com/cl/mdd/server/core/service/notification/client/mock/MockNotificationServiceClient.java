package com.cl.mdd.server.core.service.notification.client.mock;

import com.cl.mdd.server.core.service.notification.Notification;
import com.cl.mdd.server.core.service.notification.client.NotificationServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of {@link NotificationServiceClient}
 */
public class MockNotificationServiceClient implements NotificationServiceClient {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void send(Notification notification) {
        logger.debug("Sending notification: " + notification.toString());
    }
}
