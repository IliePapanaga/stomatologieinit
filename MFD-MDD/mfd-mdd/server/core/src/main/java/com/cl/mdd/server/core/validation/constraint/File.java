package com.cl.mdd.server.core.validation.constraint;

import com.cl.mdd.server.core.validation.validator.Base64FileValidator;
import com.cl.mdd.server.core.validation.validator.ScalarByteArrayFileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.cl.mdd.server.core.validation.constraint.File.FileType.ANY;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = {ScalarByteArrayFileValidator.class, Base64FileValidator.class})
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE})
public @interface File {

    FileType[] allowedTypes() default ANY;

    String message() default "{com.cl.mdd.server.core.validation.constraint.File.message}";

    /**
     * @return specifies maximum allowed size in Kilobytes
     */
    long maxSize() default Long.MAX_VALUE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    enum FileType {
        ANY, IMAGE
    }
}
