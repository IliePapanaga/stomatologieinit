package com.cl.mdd.server.core.event.impl.handler.posting.interview.accept;

import com.cl.mdd.server.core.data.persistent.access.posting.JobInterviewDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.PermanentJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.interview.JobInterviewAcceptedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.util.Objects.isNull;

@Consumer
public class NotifyPracticeOwnerAboutJobInterviewAcceptedEventHandler implements EventHandler<JobInterviewAcceptedEvent> {

    private static final String JOB_INTERVIEW_ACCEPTED_FOR_PRACTICE_OWNER = "JOB_INTERVIEW_ACCEPTED_FOR_PRACTICE_OWNER";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobInterviewDao jobInterviewDao;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private JobInterviewStartDateVariables jobInterviewStartDateVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional(readOnly = true)
    @NotificationDefinition(value = JOB_INTERVIEW_ACCEPTED_FOR_PRACTICE_OWNER,
            predefined = {
                    PracticeOwnerVariables.class,
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    JobInterviewStartDateVariables.class,
                    AdminVariables.class
            })
    public void onEvent(JobInterviewAcceptedEvent event, long sequence, boolean endOfBatch) {
        JobInterview jobInterview = jobInterviewDao.findOne(event.getInterviewId());
        if (isNull(jobInterview)) {
            return;
        }

        PermanentJobPostingApplication jobPostingApplication = jobInterview.getApplication();
        JobPosting jobPosting = jobPostingApplication.getJobPosting();
        Professional professional = jobPostingApplication.getProfessional();
        PracticeLocation location = jobPosting.getLocation();
        PracticeOwner practiceOwner = location.getPractice().getOwner();

        Map<String, String> context = Maps.newHashMap();

        Notification notification = new Notification();
        notification.setEmail(practiceOwner.getUsername());
        notification.setPhone(practiceOwner.getContact().getPhone());
        notification.setType(JOB_INTERVIEW_ACCEPTED_FOR_PRACTICE_OWNER);

        practiceOwnerVariables.supply(practiceOwner, context);
        professionalNameVariables.supply(professional, context);
        jobPostingVariables.supply(jobPosting, context);
        practiceLocationVariables.supply(location, context);
        jobInterviewStartDateVariables.supply(jobInterview, context);
        adminVariables.supply(null, context);

        notification.setContext(context);
        notificationService.send(notification);
    }
}
