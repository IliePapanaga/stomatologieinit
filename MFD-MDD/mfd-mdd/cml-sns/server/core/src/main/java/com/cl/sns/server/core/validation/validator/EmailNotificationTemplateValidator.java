package com.cl.sns.server.core.validation.validator;

import com.cl.sns.server.core.model.api.template.BaseNotificationTemplateModel;
import com.cl.sns.server.core.validation.constraint.EmailNotificationTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Notification template validator specific for notifications with E-Mail transport
 */
@Component
@Scope(SCOPE_PROTOTYPE)
public class EmailNotificationTemplateValidator implements ConstraintValidator<EmailNotificationTemplate, BaseNotificationTemplateModel> {

    private final String emailTransportName;

    public EmailNotificationTemplateValidator(@Value("${email.transport.name}") String emailTransportName) {
        this.emailTransportName = emailTransportName;
    }

    @Override
    public void initialize(EmailNotificationTemplate constraintAnnotation) {
    }

    @Override
    public boolean isValid(BaseNotificationTemplateModel value, ConstraintValidatorContext context) {
        if (!isEmailTransport(value)) {
            // If transport is not email - do not perform additional validations and treat value as valid
            return true;
        }

        boolean valid = true;

        valid &= validateEmailSubject(value, context);

        return valid;
    }

    private boolean isEmailTransport(BaseNotificationTemplateModel value) {
        return emailTransportName.equals(value.getTransport());
    }

    private boolean validateEmailSubject(BaseNotificationTemplateModel value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value.getSubject())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{notification.template.email.subject.empty}")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
