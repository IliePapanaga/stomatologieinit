package com.cl.sns.server.core.service.msg.sms.aws;

import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.cl.sns.server.core.service.msg.MsgRequestBuilder;
import com.cl.sns.server.core.service.msg.content.MessageDetails;
import com.cl.sns.server.core.service.msg.sms.SmsMessageDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Aws SNS request builder
 * <p />
 * Performs conversion operations for SNS publish
 */
@Service
public class AwsSnsRequestBuilder implements MsgRequestBuilder<PublishRequest> {
     public static final String SENDER_ID_ATTRIBUTE = "AWS.SNS.SMS.SenderID";
    /**
     * Build publish request
     * <p/>
     * @param messageDetails - message details
     */
    @Override
    public PublishRequest build(MessageDetails messageDetails) {
        SmsMessageDetails details = (SmsMessageDetails) messageDetails;
        Map<String, MessageAttributeValue> attributes = constructMessageAttributes(details);

        return constructRequest(details, attributes);
    }

    protected Map<String, MessageAttributeValue> constructMessageAttributes(SmsMessageDetails messageDetails){
        Map<String, MessageAttributeValue> smsAttributes = null;
        String from = messageDetails.getFrom();

        if(StringUtils.isNotBlank(from)) {
            smsAttributes = new HashMap<>();
            smsAttributes.put(SENDER_ID_ATTRIBUTE, new MessageAttributeValue()
                    .withStringValue(from)
                    .withDataType("String"));
        }

        return smsAttributes;
    }

    protected PublishRequest constructRequest(SmsMessageDetails messageDetails, Map<String, MessageAttributeValue> attributes){
        return new PublishRequest()
                .withMessageAttributes(attributes)
                .withMessage(messageDetails.getBody())
                .withPhoneNumber(messageDetails.getPhoneNumber());
    }

}
