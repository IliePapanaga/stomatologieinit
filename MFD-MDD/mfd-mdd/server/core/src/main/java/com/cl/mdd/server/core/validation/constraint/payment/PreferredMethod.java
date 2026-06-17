package com.cl.mdd.server.core.validation.constraint.payment;

import com.cl.mdd.server.core.data.model.payment.PaymentInstrumentBase;
import com.cl.mdd.server.core.validation.validator.payment.PreferredMethodValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Make sure that the {@link PaymentInstrumentBase} is preferred
 * or there exists another preferred one.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PreferredMethodValidator.class)
public @interface PreferredMethod {

    String message() default "{com.cl.mdd.server.core.validation.constraint.payment.PreferredMethod.message}";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
