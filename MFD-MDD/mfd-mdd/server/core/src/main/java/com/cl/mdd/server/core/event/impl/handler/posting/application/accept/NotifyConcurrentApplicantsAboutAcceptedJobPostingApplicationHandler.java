package com.cl.mdd.server.core.event.impl.handler.posting.application.accept;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.application.JobPostingApplicationAcceptedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Consumer
public class NotifyConcurrentApplicantsAboutAcceptedJobPostingApplicationHandler implements EventHandler<JobPostingApplicationAcceptedEvent> {

    private static final String JOB_POSTING_APPLICATION_ACCEPTED_FOR_CONCURRENT_APPLICANT = "JOB_POSTING_APPLICATION_ACCEPTED_FOR_CONCURRENT_APPLICANT";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobPostingApplicationDao jobPostingApplicationDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private JobPostingApplicationStartDateTimeVariables jobPostingApplicationStartDateTimeVariables;

    @Override
    @Transactional(readOnly = true)
    @NotificationDefinition(value = JOB_POSTING_APPLICATION_ACCEPTED_FOR_CONCURRENT_APPLICANT,
            predefined = {
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    JobPostingApplicationStartDateTimeVariables.class
            })
    public void onEvent(JobPostingApplicationAcceptedEvent event, long sequence, boolean endOfBatch) {
        JobPostingApplication jobPostingApplication = jobPostingApplicationDao.findOne(event.getJobPostingApplicationId());
        if (isNull(jobPostingApplication)) {
            return;
        }

        List<Professional> cancelledApplicants = jobPostingApplicationDao.findCancelledApplicants(jobPostingApplication.getJobPosting().getId());

        cancelledApplicants.stream().filter(professional1 -> professional1.isNotificationsEnabled() && !professional1.getStatus().equalsIgnoreCase(User.INACTIVE)).forEach(professional -> {
            Map<String, String> context = Maps.newHashMap();

            JobPosting jobPosting = jobPostingApplication.getJobPosting();
            PracticeLocation practiceLocation = jobPosting.getLocation();

            Notification notification = new Notification();
            notification.setEmail(professional.getUsername());
            notification.setPhone(professional.getContact().getPhone());
            notification.setType(JOB_POSTING_APPLICATION_ACCEPTED_FOR_CONCURRENT_APPLICANT);

            professionalNameVariables.supply(professional, context);
            jobPostingVariables.supply(jobPosting, context);
            practiceLocationVariables.supply(practiceLocation, context);
            jobPostingApplicationStartDateTimeVariables.supply(jobPostingApplication, context);

            notification.setContext(context);
            notificationService.send(notification);
        });
    }
}
