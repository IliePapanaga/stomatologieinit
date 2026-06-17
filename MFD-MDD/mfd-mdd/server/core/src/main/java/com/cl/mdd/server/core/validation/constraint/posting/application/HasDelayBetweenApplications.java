package com.cl.mdd.server.core.validation.constraint.posting.application;

import com.cl.mdd.server.core.data.model.ApplicationForTemporaryJob;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
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
 * Constraint against {@link Professional#id}.
 *
 * @see JobPostingApplicationService#apply(String, ApplicationForTemporaryJob)
 */
@Constraint(validatedBy = {})
@Documented
@Retention(RUNTIME)
@Target({PARAMETER})
@ExpressionConstraint(expression = "@jobPostingApplicationDao.countApplicationsForProfessionalSince(" +
        " #this," +
        " T(java.time.ZonedDateTime).now().minusSeconds(@environment.getProperty('professional.job.posting.apply.seconds.interval', T(java.lang.Long), 600))" +
        ") == 0")
@ReportAsSingleViolation
public @interface HasDelayBetweenApplications {

    String message() default "{com.cl.mdd.server.core.validation.constraint.posting.application.HasDelayBetweenApplications.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
