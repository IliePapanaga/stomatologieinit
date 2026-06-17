package com.cl.mdd.server.core.validation.constraint.payment;

import com.cl.mdd.server.core.validation.validator.payment.PaymentExistsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Make sure that the payment ID refers to an existing payment.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PaymentExistsValidator.class)
public @interface PaymentExists {

    String message() default "{com.cl.mdd.server.core.validation.constraint.payment.PaymentExists.message}";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
