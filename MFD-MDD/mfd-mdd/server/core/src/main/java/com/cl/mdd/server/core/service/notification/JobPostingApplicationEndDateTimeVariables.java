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
import java.util.*;

@Component
public class JobPostingApplicationEndDateTimeVariables extends BasePredefinedVariables implements NotificationContextSupplier<JobPostingApplication> {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");  // 10 Oct 2018
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a z");    // 05:29 PM PDT

    public static final String JOB_POSTING_APPLICATION_END_DATE = "{job.posting.application.end.date}";
    public static final String JOB_POSTING_APPLICATION_END_TIME = "{job.posting.application.end.time}";

    private static final List<Variable> VARIABLES = Arrays.asList(
            variable("notification.var.job.posting.application.end.date", JOB_POSTING_APPLICATION_END_DATE),
            variable("notification.var.job.posting.application.end.time", JOB_POSTING_APPLICATION_END_TIME)
    );

    @Override
    public List<Variable> expand() {
        return VARIABLES;
    }

    @Override
    public void supply(JobPostingApplication jobPostingApplication, Map<String, String> context) {
        computeEndDate(jobPostingApplication)
                .ifPresent(zonedDateTime ->
                        context.put(JOB_POSTING_APPLICATION_END_DATE, DATE_FORMATTER.format(zonedDateTime)));

        computeEndDate(jobPostingApplication)
                .ifPresent(zonedDateTime ->
                        context.put(JOB_POSTING_APPLICATION_END_TIME, TIME_FORMATTER.format(zonedDateTime)));
    }

    private Optional<ZonedDateTime> computeEndDate(JobPostingApplication jobPostingApplication) {
        ZoneId jobPostingLocationTimeZone = jobPostingApplication.getTimeZone();
        Optional<ZonedDateTime> endTime;

        if (jobPostingApplication instanceof TemporaryJobPostingApplication) {
            endTime = ((TemporaryJobPostingApplication) jobPostingApplication).getJobDays().stream()
                    .filter(jobDay -> !jobDay.isExcluded())
                    .map(JobDay::getZonedEndDateTime)
                    .max(Comparator.naturalOrder());
        } else {
            endTime = Optional.of(jobPostingApplication.getZonedStartDateTime());
        }
        return endTime.map(zdt -> zdt.withZoneSameInstant(jobPostingLocationTimeZone));
    }
}
