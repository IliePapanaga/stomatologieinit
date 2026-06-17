package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.PracticeLocationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Component
class PracticeLocationModelFactory extends AbstractModelFactory<PracticeLocationModel> {
    @Autowired
    private ContactModelFactory contactModelFactory;

    @Override
    public PracticeLocationModel fillFields(PracticeLocationModel model) {
        return model.setContact(contactModelFactory.create())
                .setWorkingHoursFrom(LocalTime.MIN)
                .setWorkingHoursTo(LocalTime.MAX)
                .setName(randomAlphabetic(10));

    }
}
