package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.common.FullNameModel;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Component
class FullNameModelFactory extends AbstractModelFactory<FullNameModel> {

    @Override
    public FullNameModel fillFields(FullNameModel model) {
        return model.setFirst(randomAlphabetic(10))
        .setLast(randomAlphabetic(10))
        .setMiddle(randomAlphabetic(10))
        .setTitle(randomAlphabetic(3));
    }
}
