package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.PublishSimplePermanentJobPosting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Component
class PublishSimplePermanentJobPostingFactory extends AbstractModelFactory<PublishSimplePermanentJobPosting> {

    private final TimeZoneContext timeZoneContext;

    @Autowired
    PublishSimplePermanentJobPostingFactory(TimeZoneContext timeZoneContext) {
        this.timeZoneContext = timeZoneContext;
    }

    @Override
    public PublishSimplePermanentJobPosting fillFields(PublishSimplePermanentJobPosting model) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now().withZoneSameInstant(timeZoneContext.get());
        LocalDate today = zonedDateTime.toLocalDate();

        model.setComment(randomAlphabetic(255));
        model.setName(randomAlphabetic(255));
        model.setStartDate(today.plusDays(1));
        model.setRequiredLanguages(mddRandomUtils.randomLanguages());
        model.setRequiredSubcategories(mddRandomUtils.randomSubcategories());
        model.setWorkSchedules(mddRandomUtils.getWorkSchedules());
        return model;
    }

}
