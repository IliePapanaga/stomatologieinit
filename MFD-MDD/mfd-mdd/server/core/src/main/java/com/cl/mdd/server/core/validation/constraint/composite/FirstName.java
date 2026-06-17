package com.cl.mdd.server.core.validation.constraint.composite;

import org.hibernate.validator.constraints.Length;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Pattern(regexp = "^(\\p{Alpha}*|'|-| )*$")
@Length
@Constraint(validatedBy = {})
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE})
@ReportAsSingleViolation
public @interface FirstName {

    String message() default "{com.cl.mdd.server.core.validation.constraint.composite.FirstName.message}";

    @OverridesAttribute(constraint = Length.class, name = "min")
    int min() default 2;

    @OverridesAttribute(constraint = Length.class, name = "max")
    int max() default 60;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
