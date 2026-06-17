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

@Length(min = 10, max = 60)
@Pattern(regexp = Password.PATTERN)
@Constraint(validatedBy = {})
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE})
@ReportAsSingleViolation
public @interface Password {

    String HAS_UPPER_LOWER_SPECIAL = "((?=.*\\p{Upper})(?=.*\\p{Lower})(?=.*[@#$%^&+=()!]).*)";

    String HAS_UPPER_LOWER_DIGIT = "((?=.*\\p{Upper})(?=.*\\p{Lower})(?=.*\\d).*)";

    String HAS_UPPER_SPECIAL_DIGIT = "((?=.*\\p{Upper})(?=.*[@#$%^&+=()!])(?=.*\\d).*)";

    String HAS_LOWER_SPECIAL_DIGIT = "((?=.*\\p{Lower})(?=.*[@#$%^&+=()!])(?=.*\\d).*)";

    String OR = "|";

    String START = "^";

    String END = "$";

    String PATTERN = START + HAS_UPPER_LOWER_SPECIAL + OR + HAS_UPPER_LOWER_DIGIT + OR + HAS_UPPER_SPECIAL_DIGIT + OR + HAS_LOWER_SPECIAL_DIGIT + END;

    String message() default "{com.cl.mdd.server.core.validation.constraint.composite.Password.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
