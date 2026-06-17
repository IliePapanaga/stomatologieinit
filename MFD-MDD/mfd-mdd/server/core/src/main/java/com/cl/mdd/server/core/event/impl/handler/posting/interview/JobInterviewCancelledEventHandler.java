package com.cl.mdd.server.core.event.impl.handler.posting.interview;

import com.cl.mdd.server.core.data.persistent.access.posting.JobInterviewDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.PermanentJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.interview.JobInterviewCancelledEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.util.Objects.isNull;

@Consumer
public class JobInterviewCancelledEventHandler implements EventHandler<JobInterviewCancelledEvent> {

    private static final String JOB_INTERVIEW_CANCELLED = "JOB_INTERVIEW_CANCELLED";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobInterviewDao jobInterviewDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional(readOnly = true)
    @NotificationDefinition(value = JOB_INTERVIEW_CANCELLED,
            predefined = {
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    AdminVariables.class
            })
    public void onEvent(JobInterviewCancelledEvent event, long sequence, boolean endOfBatch) {
        JobInterview jobInterview = jobInterviewDao.findOne(event.getInterviewId());
        if (isNull(jobInterview)) {
            return;
        }

        PermanentJobPostingApplication jobPostingApplication = jobInterview.getApplication();
        Professional professional = jobPostingApplication.getProfessional();
        JobPosting jobPosting = jobPostingApplication.getJobPosting();
        PracticeLocation location = jobPosting.getLocation();

        if (professional.isNotificationsEnabled() && !professional.getStatus().equalsIgnoreCase(User.INACTIVE)) {
            Map<String, String> context = Maps.newHashMap();

            Notification notification = new Notification();
            notification.setEmail(professional.getUsername());
            notification.setPhone(professional.getContact().getPhone());
            notification.setType(JOB_INTERVIEW_CANCELLED);

            professionalNameVariables.supply(professional, context);
            jobPostingVariables.supply(jobPosting, context);
            practiceLocationVariables.supply(location, context);
            adminVariables.supply(null, context);

            notification.setContext(context);
            notificationService.send(notification);
        }
    }
}
