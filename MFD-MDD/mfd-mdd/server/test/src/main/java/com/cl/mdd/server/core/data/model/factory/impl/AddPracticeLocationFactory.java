package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.AddPracticeLocation;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Component
class AddPracticeLocationFactory extends AbstractModelFactory<AddPracticeLocation> {

    private final TimeZoneContext timeZoneContext;

    @Autowired
    AddPracticeLocationFactory(TimeZoneContext timeZoneContext) {
        this.timeZoneContext = timeZoneContext;
    }


    @Override
    public AddPracticeLocation fillFields(AddPracticeLocation model) {
        model.setTimeZone(timeZoneContext.get().toString());
        LocalTime from = LocalTime.of(RandomUtils.nextInt(0, 24), RandomUtils.nextInt(0, 60));
        LocalTime to = LocalTime.of(RandomUtils.nextInt(0, 24), RandomUtils.nextInt(0, 60));
        return model.setContact(contactModelFactory.create())
                .setWorkingHoursFrom(from)
                .setWorkingHoursTo(to)
                .setName(randomAlphabetic(10));
    }

}
