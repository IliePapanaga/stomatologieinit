package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.WorkExperienceModel;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Component
class ProfessionalWorkingExperienceFactory extends AbstractModelFactory<WorkExperienceModel> {

    @Override
    public WorkExperienceModel fillFields(WorkExperienceModel model) {
        model.setCompanyName(randomAlphanumeric(50));
        model.setHireDate(ZonedDateTime.now());
        model.setLeaveDate(ZonedDateTime.now());
        model.setResponsibilities(randomAlphanumeric(50));
        return model;
    }

}
