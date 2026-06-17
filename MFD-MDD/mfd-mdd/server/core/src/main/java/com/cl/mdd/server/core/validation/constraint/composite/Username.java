package com.cl.mdd.server.core.validation.constraint.composite;

import com.cl.mdd.server.core.validation.validator.UsernameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = UsernameValidator.class)
@Email
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE})
public @interface Username {

    String message() default "{com.cl.mdd.server.core.validation.constraint.composite.Username.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean unique() default true;
}
