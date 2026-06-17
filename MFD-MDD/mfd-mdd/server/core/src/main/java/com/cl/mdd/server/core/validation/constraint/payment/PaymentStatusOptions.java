package com.cl.mdd.server.core.validation.constraint.payment;

import com.cl.mdd.server.core.validation.validator.payment.PaymentStatusOptionsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Make sure that the payment options match the current payment status.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PaymentStatusOptionsValidator.class)
public @interface PaymentStatusOptions {

    String message() default "{com.cl.mdd.server.core.validation.constraint.payment.PaymentStatusOptions.message}";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
