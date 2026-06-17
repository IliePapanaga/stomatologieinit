package com.cl.sns.server.core.service.msg.mail.aws;


import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.cl.sns.server.core.service.msg.MsgRequestBuilder;
import com.cl.sns.server.core.service.msg.Messenger;
import com.cl.sns.server.core.service.msg.content.MessageDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AwsMailMessenger implements Messenger {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AmazonSimpleEmailService amazonSimpleEmailService;
    private final MsgRequestBuilder<SendEmailRequest> awsRequestBuilder;

    @Autowired
    public AwsMailMessenger(AmazonSimpleEmailService amazonSimpleEmailService, MsgRequestBuilder<SendEmailRequest> awsRequestBuilder) {
        this.amazonSimpleEmailService = amazonSimpleEmailService;
        this.awsRequestBuilder = awsRequestBuilder;
    }

    /**
     * Assemble and sends the email.
     * <p/>
     *
     * @param details - message details
     */
    @Override
    public void send(MessageDetails details) {
        SendEmailRequest build = awsRequestBuilder.build(details);
        send(build);
    }

    /**
     * Send aws email.
     * <p />
     * @param request - email request
     */
    protected void send(SendEmailRequest request) {
        try {
            amazonSimpleEmailService.sendEmail(request);
        } catch (Exception ex) {
            logger.error("Failed to send the mail : " + ex.getMessage());
            throw ex;
        }
    }

}
