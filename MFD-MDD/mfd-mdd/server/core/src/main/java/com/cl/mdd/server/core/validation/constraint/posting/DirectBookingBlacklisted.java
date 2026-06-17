package com.cl.mdd.server.core.validation.constraint.posting;

import com.cl.mdd.server.core.validation.validator.posting.application.DirectBookingBlacklistedValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = {DirectBookingBlacklistedValidator.class})
@Documented
@Retention(RUNTIME)
@Target({TYPE})
public @interface DirectBookingBlacklisted {

    String message() default "{com.cl.mdd.server.core.validation.constraint.posting.DirectBookingBlacklisted.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
