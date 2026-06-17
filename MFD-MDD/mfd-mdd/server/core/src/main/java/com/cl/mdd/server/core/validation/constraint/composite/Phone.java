package com.cl.mdd.server.core.validation.constraint.composite;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Pattern(regexp = "^\\d{10}$")
@Constraint(validatedBy = {})
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE})
@ReportAsSingleViolation
public @interface Phone {

    String message() default "{com.cl.mdd.server.core.validation.constraint.composite.Phone.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
