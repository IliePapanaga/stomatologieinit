package com.cl.sns.server.core.service.msg.sms.aws;

import com.cl.sns.server.core.model.api.notification.RecipientDetails;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.core.service.msg.content.BaseMessageDetailsFactory;
import com.cl.sns.server.core.service.msg.content.MessageDetails;
import com.cl.sns.server.core.service.msg.sms.SmsMessageDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * AWS Sms Message details factory
 * <p />
 */
@Component
public class AwsSmsMessageDetailsFactory extends BaseMessageDetailsFactory {

    private static final String PREFIX = "+";

    private static final String PREFIX_V2 = "00";

    private static final int LENGTH_MIN = 11; // E.164. local (XXX) XXX XXXX and plus 1 for code

    private static final Pattern NON_E164 = Pattern.compile("[\\D+]");

    @Value("${aws.sms.sender}")
    private String sender;

    @Value("${aws.sms.country-code:1}")
    private String defaultCountryCode;

    @Override
    public MessageDetails create(NotificationTemplateModel template, RecipientDetails recipientDetails) {
        SmsMessageDetails messageDetails = new SmsMessageDetails();
        messageDetails.setFrom(sender);
        String content = parse(template.getContent(), recipientDetails.getPlaceHolders());
        messageDetails.setBody(content);
        messageDetails.setPhoneNumber(normalize(recipientDetails.getPhone()));

        return messageDetails;
    }

    String normalize(String phone) {
        if(StringUtils.isBlank(phone)) {
            return phone;
        }
        phone = NON_E164.matcher(phone).replaceAll(StringUtils.EMPTY);
        if(StringUtils.startsWith(phone, PREFIX_V2)) {
            phone = PREFIX + phone.substring(PREFIX_V2.length());
        }
        if(StringUtils.startsWith(phone, PREFIX)) {
            return phone;
        }
        if(StringUtils.isNotBlank(defaultCountryCode) && phone.length() < LENGTH_MIN) {
            phone = defaultCountryCode + phone;
        }
        return PREFIX + phone;
    }
}
