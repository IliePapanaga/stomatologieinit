package com.cl.mdd.server.core.validation.constraint;

import com.cl.mdd.server.core.validation.validator.SpecialityValidator;
import com.cl.mdd.server.core.validation.validator.multivalue.SpecialityCollectionValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = {SpecialityValidator.class, SpecialityCollectionValidator.class})
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE})
public @interface Speciality {
    String message() default "{com.cl.mdd.server.core.validation.constraint.Speciality.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
