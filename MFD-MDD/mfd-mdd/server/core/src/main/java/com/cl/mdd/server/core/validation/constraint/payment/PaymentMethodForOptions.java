package com.cl.mdd.server.core.validation.constraint.payment;

import com.cl.mdd.server.core.validation.validator.payment.PaymentMethodForOptionsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Make sure that the payment method ID refers to an existing credit card.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PaymentMethodForOptionsValidator.class)
public @interface PaymentMethodForOptions {

    String message() default "{com.cl.mdd.server.core.validation.constraint.payment.PaymentMethodForOptions.message}";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
