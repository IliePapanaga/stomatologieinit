package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.PublishWeeklyTemporaryJobPosting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Component
class PublishWeeklyTemporaryJobPostingFactory extends AbstractModelFactory<PublishWeeklyTemporaryJobPosting> {

    private final TimeZoneContext timeZoneContext;

    @Autowired
    PublishWeeklyTemporaryJobPostingFactory(TimeZoneContext timeZoneContext) {
        this.timeZoneContext = timeZoneContext;
    }

    @Override
    public PublishWeeklyTemporaryJobPosting fillFields(PublishWeeklyTemporaryJobPosting model) {
        ZoneId zoneId = timeZoneContext.get();

        ZonedDateTime zonedDateTime = ZonedDateTime.now().withZoneSameInstant(zoneId);
        LocalDate today = zonedDateTime.toLocalDate();

        model.setComment(randomAlphabetic(255));
        model.setName(randomAlphabetic(255));
        model.setEndDate(today.plusDays(1));
        model.setStartDate(today.plusDays(1));
        model.setRequiredLanguages(mddRandomUtils.randomLanguages());
        model.setRequiredSubcategories(mddRandomUtils.randomSubcategories());
        model.setWorkSchedules(mddRandomUtils.getWorkSchedules());
        return model;
    }


}
