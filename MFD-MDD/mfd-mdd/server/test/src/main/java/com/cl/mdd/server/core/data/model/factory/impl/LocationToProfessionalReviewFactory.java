package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.LocationToProfessionalReview;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Component
class LocationToProfessionalReviewFactory extends AbstractModelFactory<LocationToProfessionalReview> {

    @Override
    public LocationToProfessionalReview fillFields(LocationToProfessionalReview model) {
        model.setComment(randomAlphabetic(255));
        model.setProfessionalismRate(RandomUtils.nextInt(0, 6));
        model.setCommunicationRate(RandomUtils.nextInt(0, 6));
        model.setPunctualityRate(RandomUtils.nextInt(0, 6));
        model.setWorkQualityRate(RandomUtils.nextInt(0, 6));
        model.setAppearanceRate(RandomUtils.nextInt(0, 6));
        model.setWouldHire(RandomUtils.nextBoolean());
        return model;
    }

}

