package com.cl.mdd.server.core.validation.constraint.payment;

import com.cl.mdd.server.core.validation.validator.payment.GatewayCredentialsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Make sure that the payment gateway credentials are okay.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GatewayCredentialsValidator.class)
public @interface GatewayCredentials {

    String message() default "{com.cl.mdd.server.core.validation.constraint.payment.GatewayCredentials.message}";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
