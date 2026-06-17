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
 * @see JobPostingApplicationService#withdraw(String)
 * @see JobPostingApplicationService#accept(String)
 */
@Constraint(validatedBy = {})
@Documented
@Retention(RUNTIME)
@Target({PARAMETER})
@ExpressionConstraint(expression = "@jobPostingApplicationDao.findOne(#this)?.status == 'NEW'")
@ReportAsSingleViolation
public @interface AllowedForNewApplication {

    String message() default "{com.cl.mdd.server.core.validation.constraint.posting.application.AllowedForNewApplication.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
