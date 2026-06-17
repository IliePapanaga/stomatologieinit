package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.PracticeOwnerModel;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Component
class PracticeOwnerModelFactory extends UserModelFactory<PracticeOwnerModel> {
    @Override
    public PracticeOwnerModel fillFields(PracticeOwnerModel userModel) {
        super.fillFields(userModel);
        userModel.setComments(randomAlphabetic(50));
        return userModel;
    }
}
