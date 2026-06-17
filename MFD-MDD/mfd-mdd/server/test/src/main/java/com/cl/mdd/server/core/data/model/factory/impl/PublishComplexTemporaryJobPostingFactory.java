package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.JobDayModel;
import com.cl.mdd.server.core.data.model.PublishComplexTemporaryJobPosting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalTime.now;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Component
class PublishComplexTemporaryJobPostingFactory extends AbstractModelFactory<PublishComplexTemporaryJobPosting> {

    private final TimeZoneContext timeZoneContext;

    @Autowired
    PublishComplexTemporaryJobPostingFactory(TimeZoneContext timeZoneContext) {
        this.timeZoneContext = timeZoneContext;
    }

    @Override
    public PublishComplexTemporaryJobPosting fillFields(PublishComplexTemporaryJobPosting model) {

        ZonedDateTime zonedDateTime = ZonedDateTime.now().withZoneSameInstant(timeZoneContext.get());
        LocalDate today = zonedDateTime.toLocalDate();

        model.setComment(randomAlphabetic(255));
        model.setName(randomAlphabetic(255));
        model.setEndDate(today.plusDays(1));
        model.setStartDate(today.plusDays(1));
        model.setRequiredLanguages(mddRandomUtils.randomLanguages());
        model.setRequiredSubcategories(mddRandomUtils.randomSubcategories());
        model.setJobDays(randomJobDays(zonedDateTime));
        return model;
    }

    private List<JobDayModel> randomJobDays(ZonedDateTime today) {
        AtomicInteger i = new AtomicInteger(1);
        return Stream.generate(() -> randomJobDay(i, today)).limit(21).collect(Collectors.toList());
    }

    private JobDayModel randomJobDay(AtomicInteger i, ZonedDateTime today) {
        JobDayModel jobDayModel = new JobDayModel();
        jobDayModel.setExcluded(false);
        jobDayModel.setDate(today.toLocalDate().plusDays(i.get()));
        jobDayModel.setStartTime(today.toLocalTime().plusSeconds(15));
        jobDayModel.setEndTime(today.toLocalTime().plusMinutes(1));
        i.getAndIncrement();
        return jobDayModel;
    }

}
