package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting;
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
public class JobPostingStartDateTimeVariables extends BasePredefinedVariables implements NotificationContextSupplier<JobPosting> {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");  // 10 Oct 2018
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a z");    // 05:29 PM PDT

    public static final String JOB_POSTING_START_DATE = "{job.posting.start.date}";
    public static final String JOB_POSTING_START_TIME = "{job.posting.start.time}";

    private static final List<Variable> VARIABLES = Arrays.asList(
            variable("notification.var.job.posting.start.date", JOB_POSTING_START_DATE),
            variable("notification.var.job.posting.start.time", JOB_POSTING_START_TIME)
    );

    @Override
    public List<Variable> expand() {
        return VARIABLES;
    }

    @Override
    public void supply(JobPosting jobPosting, Map<String, String> context) {
        computeStartDate(jobPosting)
                .ifPresent(zonedDateTime ->
                        context.put(JOB_POSTING_START_DATE, DATE_FORMATTER.format(zonedDateTime)));

        computeStartDate(jobPosting)
                .ifPresent(zonedDateTime ->
                        context.put(JOB_POSTING_START_TIME, TIME_FORMATTER.format(zonedDateTime)));
    }

    private Optional<ZonedDateTime> computeStartDate(JobPosting jobPosting) {
        ZoneId jobPostingLocationTimeZone = jobPosting.getTimeZone();
        Optional<ZonedDateTime> startTime;

        if (jobPosting instanceof TemporaryJobPosting) {
            startTime = ((TemporaryJobPosting) jobPosting).getJobDays().stream()
                    .filter(jobDay -> !jobDay.isExcluded())
                    .map(JobDay::getZonedStartDateTime)
                    .sorted()
                    .findFirst();
        } else {
            startTime = Optional.of(jobPosting.getZonedStartDateTime());
        }
        return startTime.map(zdt -> zdt.withZoneSameInstant(jobPostingLocationTimeZone));
    }
}
