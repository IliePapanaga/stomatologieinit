package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.ProfessionalToLocationReview;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Component
class ProfessionalToLocationReviewFactory extends AbstractModelFactory<ProfessionalToLocationReview> {

    @Override
    public ProfessionalToLocationReview fillFields(ProfessionalToLocationReview model) {
        model.setComment(randomAlphabetic(255));
        model.setRate(RandomUtils.nextInt(0, 6));
        model.setWouldWorkPermanently(RandomUtils.nextBoolean());
        return model;
    }

}

