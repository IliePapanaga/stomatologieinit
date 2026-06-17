package com.cl.sns.server.core.service.impl;

import com.cl.sns.server.core.manager.NotificationTemplateManager;
import com.cl.sns.server.core.model.api.notification.SendNotificationRequest;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.core.service.NotificationService;
import com.cl.sns.server.core.service.msg.Messenger;
import com.cl.sns.server.core.service.msg.MessengerProvider;
import com.cl.sns.server.core.service.msg.content.MessageDetails;
import com.cl.sns.server.core.service.msg.content.MessageDetailsFactory;
import com.cl.sns.server.core.service.msg.content.MessageFactoryProvider;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

/**
 * Notification service
 * <p/>
 * Notification operations orchestrator.
 */
@Service
@Validated
public class NotificationServiceImpl implements NotificationService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final NotificationTemplateManager notificationTemplateManager;
    private final MessengerProvider messengerProvider;
    private final MessageFactoryProvider messageFactoryProvider;

    @Autowired
    public NotificationServiceImpl(NotificationTemplateManager notificationTemplateManager,
                                   MessengerProvider messengerProvider,
                                   MessageFactoryProvider messageFactoryProvider) {
        this.notificationTemplateManager = notificationTemplateManager;
        this.messengerProvider = messengerProvider;
        this.messageFactoryProvider = messageFactoryProvider;
    }


    @Override
    @Validated
    public void send(@Valid SendNotificationRequest sendNotificationRequest) {
        Validate.notNull(sendNotificationRequest, "Send notification request cannot be null");
        List<NotificationTemplateModel> templates = notificationTemplateManager.findByType(sendNotificationRequest.getNotificationType());

        templates.forEach(template -> {
            try {
                Messenger messenger = messengerProvider.lookUp(template.getTransport());
                MessageDetailsFactory messageDetailsFactory = messageFactoryProvider.lookUp(messenger.getClass().getSimpleName());
                MessageDetails messageDetails = messageDetailsFactory.create(template, sendNotificationRequest.getRecipientDetails());
                logger.debug("sending {}: factory {} produced {}, via {}",
                        template.getTransport(), messageDetailsFactory, messageDetails, messenger);
                messenger.send(messageDetails);
            } catch (Exception e) {
                logger.error("error sending {}", sendNotificationRequest, e);
            }
        });
    }
}
