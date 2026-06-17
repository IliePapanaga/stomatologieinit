package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.ProfessionalProfileModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Component
class ProfessionalProfileFactory extends AbstractModelFactory<ProfessionalProfileModel> {

    @Autowired
    private ProfessionalWorkingExperienceFactory professionalWorkingExperienceFactory;

    @Autowired
    private ProfessionalWorkReferenceReferenceFactory professionalWorkReferenceReferenceFactory;

    @Override
    public ProfessionalProfileModel fillFields(ProfessionalProfileModel model) {
        model.setSkillSummary(randomAlphanumeric(50));
        model.setLanguages(mddRandomUtils.randomLanguages());
        model.setHighestDegree(mddRandomUtils.randomAcademicDegree());
        model.setEducation(mddRandomUtils.randomEducation());
        model.setWorkExperiences(Stream.generate(professionalWorkingExperienceFactory::create).limit(3).collect(Collectors.toList()));
        model.setWorkReferences(Stream.generate(professionalWorkReferenceReferenceFactory::create).limit(3).collect(Collectors.toList()));
        return model;
    }

}
