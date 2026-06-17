package com.cl.sns.server.core.validation.constraint;

import com.cl.sns.server.core.validation.validator.NotificationTransportValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotificationTransportValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NotificationTransport {

    String message() default "{com.cl.sns.server.core.validation.constraint.NotificationTransport.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}