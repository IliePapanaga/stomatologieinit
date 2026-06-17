package com.cl.mdd.server.core.event.impl.handler.posting.cancel;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.JobPostingCancelledEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Consumer
public class NotifyApplicantsAboutCancelledJobPostingHandler implements EventHandler<JobPostingCancelledEvent> {

    private static final String JOB_POSTING_CANCELLED_FOR_APPLICANT = "JOB_POSTING_CANCELLED_FOR_APPLICANT";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobPostingDao jobPostingDao;

    @Autowired
    private JobPostingApplicationDao findJobPostingApplicants;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private JobPostingStartDateTimeVariables jobPostingStartDateTimeVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional
    @NotificationDefinition(value = JOB_POSTING_CANCELLED_FOR_APPLICANT,
            predefined = {
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    JobPostingStartDateTimeVariables.class,
                    PracticeLocationVariables.class,
                    AdminVariables.class
            })
    public void onEvent(JobPostingCancelledEvent event, long sequence, boolean endOfBatch) {
        JobPosting jobPosting = jobPostingDao.findOne(event.getJobPostingId());
        List<Professional> jobPostingApplicants = findJobPostingApplicants.findCancelledApplicantsDuringCancelJobPosting(event.getJobPostingId());

        PracticeLocation location = jobPosting.getLocation();

        jobPostingApplicants.stream().filter(professional1 -> professional1.isNotificationsEnabled() && !professional1.getStatus().equalsIgnoreCase(User.INACTIVE)).forEach(professional -> {
            Map<String, String> context = Maps.newHashMap();

            Notification notification = new Notification();
            notification.setEmail(professional.getUsername());
            notification.setPhone(professional.getContact().getPhone());
            notification.setType(JOB_POSTING_CANCELLED_FOR_APPLICANT);

            professionalNameVariables.supply(professional, context);
            jobPostingVariables.supply(jobPosting, context);
            jobPostingStartDateTimeVariables.supply(jobPosting, context);
            practiceLocationVariables.supply(location, context);
            adminVariables.supply(null, context);

            notification.setContext(context);
            notificationService.send(notification);
        });
    }
}
