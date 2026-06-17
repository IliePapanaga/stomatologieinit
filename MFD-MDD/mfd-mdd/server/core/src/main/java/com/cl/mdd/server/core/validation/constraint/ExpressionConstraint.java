package com.cl.mdd.server.core.validation.constraint;


import com.cl.mdd.server.core.validation.validator.ExpressionConstraintValidator;

import javax.validation.Constraint;
import javax.validation.ConstraintTarget;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constraint in form of an expression language.
 */
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ExpressionConstraintValidator.class})
public @interface ExpressionConstraint {

    ConstraintTarget validationAppliesTo() default ConstraintTarget.IMPLICIT;

    String message() default "{com.cl.mdd.server.core.validation.constraint.ExpressionConstraint.message}";

    Class[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String expression();

    /**
     * For multiple independent expression constraints.
     */
    @Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {

        ExpressionConstraint[] value();
    }
}
