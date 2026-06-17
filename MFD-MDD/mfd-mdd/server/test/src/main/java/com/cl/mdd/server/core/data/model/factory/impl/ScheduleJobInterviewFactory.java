package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.JobInterviewScheduleOption;
import com.cl.mdd.server.core.data.model.ScheduleJobInterview;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;

@Component
class ScheduleJobInterviewFactory extends AbstractModelFactory<ScheduleJobInterview> {

    @Override
    public ScheduleJobInterview fillFields(ScheduleJobInterview model) {
        model.setComments(randomAlphabetic(255));
        model.setWorking(nextBoolean());
        model.setOptions(generateOptions());
        return model;
    }

    private List<JobInterviewScheduleOption> generateOptions() {
        return Stream.generate(() -> {
            JobInterviewScheduleOption option = new JobInterviewScheduleOption();
            option.setDate(LocalDate.now().plusDays(RandomUtils.nextInt(1, 10)));
            option.setTime(LocalTime.now().plusHours(RandomUtils.nextInt(1, 10)));
            return option;
        }).limit(4).collect(Collectors.toList());
    }

}
