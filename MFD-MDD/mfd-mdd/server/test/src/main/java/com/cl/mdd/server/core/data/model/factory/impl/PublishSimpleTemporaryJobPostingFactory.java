package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.PublishSimpleTemporaryJobPosting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Component
class PublishSimpleTemporaryJobPostingFactory extends AbstractModelFactory<PublishSimpleTemporaryJobPosting> {

    private final TimeZoneContext timeZoneContext;

    @Autowired
    PublishSimpleTemporaryJobPostingFactory(TimeZoneContext timeZoneContext) {
        this.timeZoneContext = timeZoneContext;
    }


    @Override
    public PublishSimpleTemporaryJobPosting fillFields(PublishSimpleTemporaryJobPosting model) {
        ZoneId zoneId = timeZoneContext.get();

        ZonedDateTime zonedDateTime = ZonedDateTime.now().withZoneSameInstant(zoneId);
        LocalDate today = zonedDateTime.toLocalDate();

        model.setComment(randomAlphabetic(255));
        model.setName(randomAlphabetic(255));
        model.setEndDate(today.plusDays(1));
        model.setStartDate(today.plusDays(1));
        model.setStartTime(zonedDateTime.toLocalTime().plusSeconds(15));
        model.setEndTime(zonedDateTime.toLocalTime().plusMinutes(1));
        model.setRequiredLanguages(mddRandomUtils.randomLanguages());
        model.setRequiredSubcategories(mddRandomUtils.randomSubcategories());
        return model;
    }

}
