package com.cl.sns.server.core.validation.validator;

import com.cl.sns.server.core.model.api.template.BaseNotificationTemplateModel;
import com.cl.sns.server.core.validation.constraint.SmsNotificationTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Notification template validator specific for notifications with SMS transport
 */
@Component
@Scope(SCOPE_PROTOTYPE)
public class SmsNotificationTemplateValidator implements ConstraintValidator<SmsNotificationTemplate, BaseNotificationTemplateModel> {

    private final String smsTransportName;

    private final int contentMaxLength;

    public SmsNotificationTemplateValidator(@Value("${sms.transport.name}") String smsTransportName,
                                            @Value("${sms.max.length}") int contentMaxLength) {
        this.smsTransportName = smsTransportName;
        this.contentMaxLength = contentMaxLength;
    }

    @Override
    public void initialize(SmsNotificationTemplate constraintAnnotation) {
    }

    @Override
    public boolean isValid(BaseNotificationTemplateModel value, ConstraintValidatorContext context) {
        if (!isSmsTransport(value)) {
            // If transport is not sms - do not perform additional validations and treat value as valid
            return true;
        }

        boolean valid = true;

        valid &= validateSmsContentLength(value, context);

        return valid;
    }

    private boolean isSmsTransport(BaseNotificationTemplateModel value) {
        return smsTransportName.equals(value.getTransport());
    }

    private boolean validateSmsContentLength(BaseNotificationTemplateModel value, ConstraintValidatorContext context) {
        if (StringUtils.length(value.getContent()) > contentMaxLength) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{notification.template.sms.content.length}")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
