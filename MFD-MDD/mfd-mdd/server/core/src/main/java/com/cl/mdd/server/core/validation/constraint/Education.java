package com.cl.mdd.server.core.validation.constraint;

import com.cl.mdd.server.core.validation.validator.EducationValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = EducationValidator.class)
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE})
public @interface Education {

    String message() default "{com.cl.mdd.server.core.validation.constraint.Education.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
