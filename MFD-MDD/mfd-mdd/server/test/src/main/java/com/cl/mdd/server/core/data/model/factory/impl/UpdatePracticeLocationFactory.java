package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.UpdatePracticeLocation;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Component
class UpdatePracticeLocationFactory extends AbstractModelFactory<UpdatePracticeLocation> {

    @Override
    public UpdatePracticeLocation fillFields(UpdatePracticeLocation model) {
        LocalTime from = LocalTime.of(RandomUtils.nextInt(0, 24), RandomUtils.nextInt(0, 60));
        LocalTime to = LocalTime.of(RandomUtils.nextInt(0, 24), RandomUtils.nextInt(0, 60));
        model.setContact(contactModelFactory.create())
                .setWorkingHoursFrom(from)
                .setWorkingHoursTo(to)
                .setName(randomAlphabetic(10));
        return model;
    }

}
