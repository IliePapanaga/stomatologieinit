package com.cl.mdd.server.core.validation.constraint.posting.application;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.service.posting.JobPostingApplicationService;
import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Constraint against {@link JobPostingApplication#id}.
 * <p>
 * Validates {@link JobPostingApplication#status}.
 *
 * @see JobPostingApplicationService#reject(String)
 */
@Constraint(validatedBy = {})
@Documented
@Retention(RUNTIME)
@Target({PARAMETER})
@ExpressionConstraint(expression = "{'BOOKED', 'ACCEPTED'}.contains(@jobPostingApplicationDao.findOne(#this)?.status)")
@ReportAsSingleViolation
public @interface AllowedForBookedOrAcceptedApplication {

    String message() default "{com.cl.mdd.server.core.validation.constraint.posting.application.AllowedForBookedOrAcceptedApplication.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
