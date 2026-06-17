package com.cl.mdd.server.core.validation.constraint.certificates;

import com.cl.mdd.server.core.validation.validator.CertificateValidator;
import com.cl.mdd.server.core.validation.validator.multivalue.CertificateCollectionValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = {CertificateValidator.class, CertificateCollectionValidator.class})
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE})
public @interface Certificate {
    String message() default "{com.cl.mdd.server.core.validation.constraint.certificates.Certificate}.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
