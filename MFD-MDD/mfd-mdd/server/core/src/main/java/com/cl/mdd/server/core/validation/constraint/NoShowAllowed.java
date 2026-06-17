package com.cl.mdd.server.core.validation.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = {})
@Documented
@Retention(RUNTIME)
@Target({FIELD})
@ExpressionConstraint.List(value = {
        @ExpressionConstraint(expression = " not @noWorkDao.existsById(#this)", message = "{job.attendance.already.no.work}"),
        @ExpressionConstraint(expression = " @jobDayDao.findOne(#this)?.getZonedStartDateTime()?.minusSeconds(@environment.getProperty('job.attendance.noWork.allowed.before.seconds', T(java.lang.Long), 3600 ))?.isBefore(T(java.time.ZonedDateTime).now())", message = "{job.attendance.noShow.too.early}"),
        @ExpressionConstraint(expression = " @jobDayDao.findOne(#this)?.getZonedStartDateTime()?.plusSeconds(@environment.getProperty('job.attendance.noWork.allowed.after.seconds', T(java.lang.Long), 3600 ))?.isAfter(T(java.time.ZonedDateTime).now())", message = "{job.attendance.noShow.too.late}")
})
public @interface NoShowAllowed {

    String message() default "{com.cl.mdd.server.core.validation.constraint.NoShowAllowed.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
