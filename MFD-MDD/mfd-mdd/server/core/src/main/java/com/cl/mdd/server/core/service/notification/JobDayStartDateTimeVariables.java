package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.service.notification.definition.NotificationContextSupplier;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.cl.mdd.server.core.service.notification.definition.impl.BasePredefinedVariables;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JobDayStartDateTimeVariables extends BasePredefinedVariables implements NotificationContextSupplier<JobDay> {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");  // 13 Nov 2018
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a z");    // 09:30 AM EST

    public static final String JOB_DAY_START_DATE = "{job.day.start.date}";
    public static final String JOB_DAY_START_TIME = "{job.day.start.time}";

    private static final List<Variable> VARIABLES = Arrays.asList(
            variable("notification.var.job.day.start.date", JOB_DAY_START_DATE),
            variable("notification.var.job.day.start.time", JOB_DAY_START_TIME)
    );

    @Override
    public List<Variable> expand() {
        return VARIABLES;
    }

    @Override
    public void supply(JobDay jobDay, Map<String, String> context) {
        computeStartDate(jobDay)
                .ifPresent(zonedDateTime ->
                        context.put(JOB_DAY_START_DATE, DATE_FORMATTER.format(zonedDateTime)));

        computeStartDate(jobDay)
                .ifPresent(zonedDateTime ->
                        context.put(JOB_DAY_START_TIME, TIME_FORMATTER.format(zonedDateTime)));
    }

    private Optional<ZonedDateTime> computeStartDate(JobDay jobDay) {
        JobPosting jobPosting = jobDay.getJobPosting();
        ZoneId jobPostingLocationTimeZone = jobPosting.getTimeZone();
        Optional<ZonedDateTime> startTime = Optional.of(jobDay.getZonedStartDateTime());

        return startTime.map(zdt -> zdt.withZoneSameInstant(jobPostingLocationTimeZone));
    }
}
