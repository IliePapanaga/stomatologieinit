package com.cl.sns.server.core.validation.validator;

import com.cl.sns.server.core.manager.NotificationTemplateManager;
import com.cl.sns.server.core.model.api.template.BaseNotificationTemplateModel;
import com.cl.sns.server.core.model.api.template.CreateNotificationTemplate;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.core.validation.constraint.NotificationTemplateUnique;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.Objects;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class NotificationTemplateUniquenessValidator implements ConstraintValidator<NotificationTemplateUnique, BaseNotificationTemplateModel> {

    private final NotificationTemplateManager notificationTemplateManager;

    @Autowired
    public NotificationTemplateUniquenessValidator(final NotificationTemplateManager notificationTemplateManager) {
        this.notificationTemplateManager = notificationTemplateManager;
    }

    @Override
    public void initialize(NotificationTemplateUnique constraintAnnotation) {
    }

    @Override
    public boolean isValid(BaseNotificationTemplateModel value, ConstraintValidatorContext context) {
        boolean valid = true;

        if (StringUtils.isNotBlank(value.getType()) && StringUtils.isNotBlank(value.getTransport())) {
            List<NotificationTemplateModel> templatesByTypeAndTransport = notificationTemplateManager.findByTypeAndTransport(value.getType(), value.getTransport());

            if (value instanceof NotificationTemplateModel) {
                valid = (templatesByTypeAndTransport.isEmpty()
                        || templatesByTypeAndTransport.stream().noneMatch(template -> !Objects.equals(((NotificationTemplateModel) value).getId(), template.getId())));
            } else {
                valid = templatesByTypeAndTransport.size() == 0;
            }
        }

        return valid;
    }
}
