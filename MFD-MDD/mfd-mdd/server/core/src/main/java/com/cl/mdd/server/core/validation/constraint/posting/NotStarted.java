package com.cl.mdd.server.core.validation.constraint.posting;

import com.cl.mdd.server.core.data.model.ComplexTemporaryJobPosting;
import com.cl.mdd.server.core.data.model.SimplePermanentJobPosting;
import com.cl.mdd.server.core.data.model.WeeklyTemporaryJobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Constraint against {@link JobPosting#id}.
 * <p>
 * Validates {@link JobPosting#getZonedStartDateTime()} .
 *
 * @see com.cl.mdd.server.core.service.posting.JobPostingService#update(SimplePermanentJobPosting)
 * @see com.cl.mdd.server.core.service.posting.JobPostingService#update(WeeklyTemporaryJobPosting)
 * @see com.cl.mdd.server.core.service.posting.JobPostingService#update(ComplexTemporaryJobPosting)
 */
@Constraint(validatedBy = {})
@Documented
@Retention(RUNTIME)
@Target({FIELD})
@ExpressionConstraint(expression = "@jobPostingDao.findOne(#this)?.getZonedStartDateTime().isAfter((T(java.time.ZonedDateTime).now()))")
@ReportAsSingleViolation
public @interface NotStarted {

    String message() default "{com.cl.mdd.server.core.validation.constraint.posting.NotStarted.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
