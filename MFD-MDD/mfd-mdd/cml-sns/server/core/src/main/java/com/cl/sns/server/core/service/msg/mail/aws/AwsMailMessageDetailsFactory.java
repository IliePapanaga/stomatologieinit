package com.cl.sns.server.core.service.msg.mail.aws;

import com.cl.sns.server.core.model.api.notification.RecipientDetails;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.core.service.msg.content.BaseMessageDetailsFactory;
import com.cl.sns.server.core.service.msg.content.MessageDetails;
import com.cl.sns.server.core.service.msg.content.MessageDetailsFactory;
import com.cl.sns.server.core.service.msg.mail.MailMessageDetails;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

/**
 * AWS Mail Message details factory
 * <p />
 */
@Component
public class AwsMailMessageDetailsFactory extends BaseMessageDetailsFactory {

    @Value("${aws.mail.sender}")
    private String sender;

    @Override
    public MessageDetails create(NotificationTemplateModel template, RecipientDetails recipientDetails) {
        MailMessageDetails messageDetails = new MailMessageDetails();
        messageDetails.setSubject(template.getSubject());
        messageDetails.setFrom(sender);

        String content = parse(template.getContent(), recipientDetails.getPlaceHolders());
        messageDetails.setBody(content);

        messageDetails.setTo(Arrays.asList(recipientDetails.getEmail()));

        return messageDetails;
    }
}
