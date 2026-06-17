package com.cl.sns.server.core.service.msg.content;

import com.cl.sns.server.core.model.api.notification.RecipientDetails;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.core.model.db.msg.NotificationTemplate;

import java.util.List;
import java.util.Map;

/**
 * Message details factory.
 * <p />
 */
public interface MessageDetailsFactory {

    MessageDetails create(NotificationTemplateModel template, RecipientDetails recipientDetails);

}
