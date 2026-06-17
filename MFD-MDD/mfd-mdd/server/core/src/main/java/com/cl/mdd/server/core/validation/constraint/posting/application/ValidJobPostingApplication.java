package com.cl.mdd.server.core.validation.constraint.posting.application;

import com.cl.mdd.server.core.data.model.ApplicationForTemporaryJob;
import com.cl.mdd.server.core.service.posting.JobPostingApplicationService;
import com.cl.mdd.server.core.validation.validator.posting.application.JobPostingApplicationValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Constraint against {@link JobPostingApplicationService#apply(String, ApplicationForTemporaryJob)} arguments.
 */
@Constraint(validatedBy = JobPostingApplicationValidator.class)
@Documented
@Retention(RUNTIME)
@Target({METHOD})
public @interface ValidJobPostingApplication {

    String message() default "{com.cl.mdd.server.core.validation.constraint.posting.application.ValidJobPostingApplication.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    ApplicationType applicationType();

    enum ApplicationType {
        TEMPORARY,
        PERMANENT
    }
}
