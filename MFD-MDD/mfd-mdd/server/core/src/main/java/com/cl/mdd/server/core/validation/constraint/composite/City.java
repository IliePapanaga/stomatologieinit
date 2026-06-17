package com.cl.mdd.server.core.validation.constraint.composite;

import org.hibernate.validator.constraints.Length;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Length(min = 2, max = 60, message = "{city.length}")
@Constraint(validatedBy = {})
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE})
public @interface City {

    String message() default "{com.cl.mdd.server.core.validation.constraint.composite.City.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
