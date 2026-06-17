package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
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
public class JobPostingApplicationStartDateTimeVariables extends BasePredefinedVariables implements NotificationContextSupplier<JobPostingApplication> {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");  // 10 Oct 2018
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a z");    // 05:29 PM PDT

    public static final String JOB_POSTING_APPLICATION_START_DATE = "{job.posting.application.start.date}";
    public static final String JOB_POSTING_APPLICATION_START_TIME = "{job.posting.application.start.time}";

    private static final List<Variable> VARIABLES = Arrays.asList(
            variable("notification.var.job.posting.application.start.date", JOB_POSTING_APPLICATION_START_DATE),
            variable("notification.var.job.posting.application.start.time", JOB_POSTING_APPLICATION_START_TIME)
    );

    @Override
    public List<Variable> expand() {
        return VARIABLES;
    }

    @Override
    public void supply(JobPostingApplication jobPostingApplication, Map<String, String> context) {
        computeStartDate(jobPostingApplication)
                .ifPresent(zonedDateTime ->
                        context.put(JOB_POSTING_APPLICATION_START_DATE, DATE_FORMATTER.format(zonedDateTime)));

        computeStartDate(jobPostingApplication)
                .ifPresent(zonedDateTime ->
                        context.put(JOB_POSTING_APPLICATION_START_TIME, TIME_FORMATTER.format(zonedDateTime)));
    }

    private Optional<ZonedDateTime> computeStartDate(JobPostingApplication jobPostingApplication) {
        ZoneId jobPostingLocationTimeZone = jobPostingApplication.getTimeZone();
        Optional<ZonedDateTime> startTime;

        if (jobPostingApplication instanceof TemporaryJobPostingApplication) {
            startTime = ((TemporaryJobPostingApplication) jobPostingApplication).getJobDays().stream()
                    .filter(jobDay -> !jobDay.isExcluded())
                    .map(JobDay::getZonedStartDateTime)
                    .sorted()
                    .findFirst();
        } else {
            startTime = Optional.of(jobPostingApplication.getZonedStartDateTime());
        }
        return startTime.map(zdt -> zdt.withZoneSameInstant(jobPostingLocationTimeZone));
    }
}
