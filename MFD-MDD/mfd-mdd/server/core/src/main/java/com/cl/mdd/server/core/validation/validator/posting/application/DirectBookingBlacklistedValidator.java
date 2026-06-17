package com.cl.mdd.server.core.validation.validator.posting.application;

import com.cl.mdd.server.core.data.model.PublishAbstractJobPosting;
import com.cl.mdd.server.core.data.persistent.access.user.BlackListedLocationDao;
import com.cl.mdd.server.core.data.persistent.access.user.BlackListedProfessionalDao;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.validation.annotation.Validator;
import com.cl.mdd.server.core.validation.constraint.posting.DirectBookingBlacklisted;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

import static java.util.Objects.isNull;

@Validator
public class DirectBookingBlacklistedValidator implements ConstraintValidator<DirectBookingBlacklisted, PublishAbstractJobPosting> {

    private final BlackListedLocationDao blackListedLocationDao;
    private final BlackListedProfessionalDao blackListedProfessionalDao;

    @Autowired
    public DirectBookingBlacklistedValidator(BlackListedLocationDao blackListedLocationDao, BlackListedProfessionalDao blackListedProfessionalDao) {
        this.blackListedLocationDao = blackListedLocationDao;
        this.blackListedProfessionalDao = blackListedProfessionalDao;
    }

    @Override
    public void initialize(DirectBookingBlacklisted directBookingBlacklisted) {
    }

    @Override
    public boolean isValid(PublishAbstractJobPosting value, ConstraintValidatorContext context) {
        return Objects.nonNull(value) && isValid(value);
    }

    private boolean isValid(PublishAbstractJobPosting publishAbstractJobPosting) {
        String preferredCandidateId = publishAbstractJobPosting.getPreferredCandidateId();
        String practiceLocationId = publishAbstractJobPosting.getPracticeLocationId();
        PracticeLocation practiceLocation = new PracticeLocation();
        practiceLocation.setId(practiceLocationId);
        if (isNull(preferredCandidateId)) {
            return true;
        }
        boolean locationBlacklistedByProfessional = blackListedLocationDao.isLocationBlacklistedByProfessional(preferredCandidateId, practiceLocationId);
        boolean professionalBlacklistedInPractice = blackListedProfessionalDao.isProfessionalBlacklistedInPractice(preferredCandidateId, practiceLocation);

        return !locationBlacklistedByProfessional && !professionalBlacklistedInPractice;
    }
}
