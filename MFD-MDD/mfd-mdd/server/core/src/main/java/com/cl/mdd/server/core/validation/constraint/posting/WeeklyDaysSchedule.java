package com.cl.mdd.server.core.validation.constraint.posting;

import com.cl.mdd.server.core.validation.validator.WeeklyDaysScheduleValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = {WeeklyDaysScheduleValidator.class})
@Documented
@Retention(RUNTIME)
@Target({TYPE})
public @interface WeeklyDaysSchedule {

    String message() default "{com.cl.mdd.server.core.validation.constraint.posting.WeeklyDaysSchedule.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
