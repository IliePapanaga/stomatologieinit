package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import com.cl.mdd.server.core.service.notification.definition.NotificationContextSupplier;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.cl.mdd.server.core.service.notification.definition.impl.BasePredefinedVariables;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class JobInterviewStartDateVariables extends BasePredefinedVariables implements NotificationContextSupplier<JobInterview> {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a z");

    public static final String JOB_POSTING_INTERVIEW_START_DATE = "{job.posting.interview.start.date}";
    public static final String JOB_POSTING_INTERVIEW_START_TIME = "{job.posting.interview.start.time}";

    private static final List<Variable> VARIABLES = Arrays.asList(
            variable("notification.var.job.posting.interview.start.date", JOB_POSTING_INTERVIEW_START_DATE),
            variable("notification.var.job.posting.interview.start.time", JOB_POSTING_INTERVIEW_START_TIME)
    );

    @Override
    public List<Variable> expand() {
        return VARIABLES;
    }

    @Override
    public void supply(JobInterview jobInterview, Map<String, String> context) {
        ZoneId timeZone = jobInterview.getApplication().getJobPosting().getTimeZone();

        context.put(JOB_POSTING_INTERVIEW_START_DATE, DATE_FORMATTER.format(jobInterview.getAcceptedOption().getZonedStartDateTime().withZoneSameInstant(timeZone)));
        context.put(JOB_POSTING_INTERVIEW_START_TIME, TIME_FORMATTER.format(jobInterview.getAcceptedOption().getZonedStartDateTime().withZoneSameInstant(timeZone)));
    }
}
