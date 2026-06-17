package com.cl.sns.server.core.service.msg.mail.aws;

import com.amazonaws.services.simpleemail.model.*;
import com.cl.sns.server.core.service.msg.MsgRequestBuilder;
import com.cl.sns.server.core.service.msg.content.MessageDetails;
import com.cl.sns.server.core.service.msg.mail.MailMessageDetails;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

/**
 * Aws SES request builder class
 * <p />
 * Performs conversion operations for SES email request assembly.
 */
@Service
public class AwsSesRequestBuilder implements MsgRequestBuilder<SendEmailRequest> {
    /**
     * Construct email request
     * <p/>
     * @param messageDetails - message details
     */
    public SendEmailRequest build(MessageDetails messageDetails) {
        MailMessageDetails details = (MailMessageDetails)messageDetails;
        Destination destination = constructDestination(details.getTo(), details.getCc(), details.getBcc());
        Message message = constructMessage(details.getSubject(), details.getBody());
        return constructEmailRequest(details.getFrom(), destination, message);
    }
    /**
     * Construct email request
     * <p/>
     *
     * @param from        - to recipients
     * @param destination - cc recipients
     * @param message     - bcc recipients
     */
    protected SendEmailRequest constructEmailRequest(String from, Destination destination, Message message) {
        return new SendEmailRequest()
                .withSource(from)
                .withDestination(destination)
                .withMessage(message);
    }

    /**
     * Construct email destination object
     * <p/>
     *
     * @param to  - to recipients
     * @param cc  - cc recipients
     * @param bcc - bcc recipients
     */
    protected Destination constructDestination(Collection<String> to, Collection<String> cc, Collection<String> bcc) {
        return new Destination()
                .withToAddresses(emptyIfNull(to))
                .withCcAddresses(emptyIfNull(cc))
                .withBccAddresses(emptyIfNull(bcc));
    }

    /**
     * Construct email message object
     * <p/>
     *
     * @param subject - message subject
     * @param body    - message body
     * @return {@link Message }
     */
    protected Message constructMessage(String subject, String body) {
        Validate.notBlank(subject);
        Validate.notBlank(body);

        Content subjectContent = new Content().withData(subject);
        Content bodyContent = new Content().withCharset("UTF-8").withData(body);
        Body emailBody = new Body().withHtml(bodyContent);

        return new Message().withSubject(subjectContent).withBody(emailBody);
    }
}
