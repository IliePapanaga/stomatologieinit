package com.cl.mdd.server.core.validation.constraint.composite;

import org.hibernate.validator.constraints.Length;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Length(max = 255)
@Pattern(regexp = "^(http://|https://)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$")
@Constraint(validatedBy = {})
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE})
@ReportAsSingleViolation
public @interface Website {

    String message() default "{com.cl.mdd.server.core.validation.constraint.composite.Website.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
