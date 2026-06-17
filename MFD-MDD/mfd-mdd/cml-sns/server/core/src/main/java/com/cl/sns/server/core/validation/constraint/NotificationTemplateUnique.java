package com.cl.sns.server.core.validation.constraint;

import com.cl.sns.server.core.validation.validator.NotificationTemplateUniquenessValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotificationTemplateUniquenessValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NotificationTemplateUnique {

    String message() default "{com.cl.sns.server.core.validation.constraint.NotificationTemplateUnique.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}