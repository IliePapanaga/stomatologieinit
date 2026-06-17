package com.cl.mdd.server.core.validation.validator.posting.application;

import com.cl.mdd.server.core.data.model.ApplicationForPermanentJob;
import com.cl.mdd.server.core.data.model.ApplicationForTemporaryJob;
import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.access.posting.TemporaryJobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.validation.constraint.posting.application.ValidJobPostingApplication;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import java.time.LocalDate;
import java.util.Set;

import static javax.validation.constraintvalidation.ValidationTarget.PARAMETERS;

@SupportedValidationTarget(PARAMETERS)
public class JobPostingApplicationValidator implements ConstraintValidator<ValidJobPostingApplication, Object[]> {

    @Autowired
    private TemporaryJobPostingApplicationDao temporaryJobPostingApplicationDao;

    @Autowired
    private JobPostingApplicationDao jobPostingApplicationDao;

    private ValidJobPostingApplication.ApplicationType strategy;

    @Override
    public void initialize(ValidJobPostingApplication constraintAnnotation) {
        strategy = constraintAnnotation.applicationType();
    }

    @Override
    public boolean isValid(Object[] arguments, ConstraintValidatorContext context) {
        String professionalId = professionalId(arguments);
        switch (strategy) {
            case TEMPORARY:
                return verifyTemporary(arguments, context, professionalId);
            case PERMANENT:
                return verifyPermanent(arguments, context, professionalId);
            default:
                return true;
        }
    }

    private boolean verifyTemporary(Object[] arguments, ConstraintValidatorContext context, String professionalId) {
        ApplicationForTemporaryJob applicationForTemporaryJob = applicationForTemporaryJob(arguments);

        if (alreadyAppliedForThePosting(professionalId, applicationForTemporaryJob.getJobPostingId())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{job.posting.application.already.applied}").addConstraintViolation();
            return false;
        }

        if (alreadyAppliedForJobDays(professionalId, applicationForTemporaryJob.getWorkingDays())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{job.posting.application.days.already.applied}").addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean verifyPermanent(Object[] arguments, ConstraintValidatorContext context, String professionalId) {
        ApplicationForPermanentJob applicationForTemporaryJob = applicationForPermanentJob(arguments);

        if (alreadyAppliedForThePosting(professionalId, applicationForTemporaryJob.getJobPostingId())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{job.posting.application.already.applied}").addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean alreadyAppliedForThePosting(String professionalId, String jobPostingId) {
        return jobPostingApplicationDao.countApplicationsForPostingByProfessionalInStatus(professionalId, jobPostingId, JobPostingApplication.ACTIVE_STATUSES) != 0L;
    }

    private boolean alreadyAppliedForJobDays(String professionalId, Set<LocalDate> workingDays) {
        return temporaryJobPostingApplicationDao.countApplicationsForProfessionalInSpecifiedJobDays(professionalId, workingDays) != 0L;
    }

    private String professionalId(Object[] arguments) {
        return (String) arguments[0];
    }

    private ApplicationForTemporaryJob applicationForTemporaryJob(Object[] arguments) {
        return (ApplicationForTemporaryJob) arguments[1];
    }

    private ApplicationForPermanentJob applicationForPermanentJob(Object[] arguments) {
        return (ApplicationForPermanentJob) arguments[1];
    }
}
