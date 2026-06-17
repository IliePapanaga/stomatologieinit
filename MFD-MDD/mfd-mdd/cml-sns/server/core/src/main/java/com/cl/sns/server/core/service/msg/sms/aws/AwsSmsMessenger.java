package com.cl.sns.server.core.service.msg.sms.aws;


import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.cl.sns.server.core.service.msg.MsgRequestBuilder;
import com.cl.sns.server.core.service.msg.Messenger;
import com.cl.sns.server.core.service.msg.content.MessageDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AwsSmsMessenger implements Messenger {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AmazonSNS amazonSNS;
    private final MsgRequestBuilder<PublishRequest> awsSnsRequestBuilder;

    @Autowired
    public AwsSmsMessenger(AmazonSNS amazonSNS, MsgRequestBuilder awsSnsRequestBuilder) {
        this.amazonSNS = amazonSNS;
        this.awsSnsRequestBuilder = awsSnsRequestBuilder;
    }

    /**
     * Assemble and sends the email.
     * <p/>
     *
     * @param details - message details
     */
    @Override
    public void send(MessageDetails details) {
        PublishRequest publishRequest = awsSnsRequestBuilder.build(details);
        send(publishRequest);
    }

    /**
     * Publish the notification
     * <p />
     * @param publishRequest
     */
    protected void send(PublishRequest publishRequest) {
        try {
            amazonSNS.publish(publishRequest);
        } catch (Exception ex) {
            logger.error("Publish action failed  : " + ex.getMessage());
            throw ex;
        }
    }




}
