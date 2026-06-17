package com.cl.mdd.server.core.service.notification.client.embedded;

import com.amazonaws.AmazonServiceException;
import com.cl.mdd.server.core.service.notification.Notification;
import com.cl.mdd.server.core.service.notification.NotificationServiceException;
import com.cl.mdd.server.core.service.notification.client.NotificationServiceClient;
import com.cl.sns.server.core.model.api.notification.RecipientDetails;
import com.cl.sns.server.core.model.api.notification.SendNotificationRequest;
import com.cl.sns.server.core.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link NotificationServiceClient} that works with notification service deployed as a part of application
 */
@Service
public class EmbeddedNotificationServiceClient implements NotificationServiceClient {

    private final NotificationService notificationService;

    @Autowired
    public EmbeddedNotificationServiceClient(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Notification notification) {
        SendNotificationRequest request = buildSendRequest(notification);

        try {
            notificationService.send(request);
        } catch (final AmazonServiceException ex) {
            throw new NotificationServiceException(ex.getErrorMessage(), ex, "NOTIFICATION_INTERNAL_ERROR");
        }
    }

    protected SendNotificationRequest buildSendRequest(Notification notification) {
        SendNotificationRequest request = new SendNotificationRequest();

        request.setNotificationType(notification.getType());
        request.setRecipientDetails(buildRecipient(notification));

        return request;
    }

    protected RecipientDetails buildRecipient(Notification notification) {
        RecipientDetails recipient = new RecipientDetails();

        recipient.setEmail(notification.getEmail());
        recipient.setPhone(notification.getPhone());
        recipient.setPlaceHolders(notification.getContext());

        return recipient;
    }
}
