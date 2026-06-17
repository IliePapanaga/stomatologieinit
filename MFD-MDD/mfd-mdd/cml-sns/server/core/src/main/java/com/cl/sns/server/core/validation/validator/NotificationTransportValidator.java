package com.cl.sns.server.core.validation.validator;

import com.cl.sns.server.core.dao.MessagingTransportDao;
import com.cl.sns.server.core.validation.constraint.NotificationTransport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class NotificationTransportValidator implements ConstraintValidator<NotificationTransport, String> {

    private final MessagingTransportDao messagingTransportDao;

    @Autowired
    public NotificationTransportValidator(final MessagingTransportDao messagingTransportDao) {
        this.messagingTransportDao = messagingTransportDao;
    }

    @Override
    public void initialize(NotificationTransport constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isBlank(value) || messagingTransportDao.findByName(value) != null;
    }
}