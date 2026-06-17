package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.WorkReferenceModel;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@Component
class ProfessionalWorkReferenceReferenceFactory extends AbstractModelFactory<WorkReferenceModel> {

    @Override
    public WorkReferenceModel fillFields(WorkReferenceModel model) {
        model.setEmail(randomAlphanumeric(20) + "@gmail.com");
        model.setName(randomAlphanumeric(50));
        model.setPhone(randomNumeric(10));
        return model;
    }

}
