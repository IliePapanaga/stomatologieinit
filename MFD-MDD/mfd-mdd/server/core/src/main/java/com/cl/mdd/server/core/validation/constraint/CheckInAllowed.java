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
        @ExpressionConstraint(expression = " not @checkInDao.existsById(#this)", message = "{job.attendance.already.checkIn}"),
        @ExpressionConstraint(expression = " @jobDayDao.findOne(#this)?.getZonedStartDateTime()?.minusSeconds(@environment.getProperty('job.attendance.checkIn.allowed.before.seconds', T(java.lang.Long), 3600 ))?.isBefore(T(java.time.ZonedDateTime).now())", message = "{job.attendance.checkIn.too.early}"),
        @ExpressionConstraint(expression = " @jobDayDao.findOne(#this)?.getZonedStartDateTime()?.plusSeconds(@environment.getProperty('job.attendance.checkIn.allowed.after.seconds', T(java.lang.Long), 3600 ))?.isAfter(T(java.time.ZonedDateTime).now())", message = "{job.attendance.checkIn.too.late}")
})
public @interface CheckInAllowed {

    String message() default "{com.cl.mdd.server.core.validation.constraint.CheckInAllowed.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
