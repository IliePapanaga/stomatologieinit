package com.cl.mdd.server.core.event.impl.handler.posting.application;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.application.JobPostingApplicationWithdrawnEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.util.Objects.isNull;

@Consumer
public class JobPostingApplicationWithdrawnEventHandler implements EventHandler<JobPostingApplicationWithdrawnEvent> {

    private static final String JOB_POSTING_APPLICATION_WITHDRAWN = "JOB_POSTING_APPLICATION_WITHDRAWN";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobPostingDao jobPostingDao;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private JobPostingApplicationStartDateTimeVariables jobPostingApplicationStartDateTimeVariables;

    @Override
    @Transactional(readOnly = true)
    @NotificationDefinition(value = JOB_POSTING_APPLICATION_WITHDRAWN,
            predefined = {
                    PracticeOwnerVariables.class,
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    JobPostingApplicationStartDateTimeVariables.class
            })
    public void onEvent(JobPostingApplicationWithdrawnEvent event, long sequence, boolean endOfBatch) {
        JobPostingApplication jobPostingApplication = event.getJobPostingApplication();
        if (isNull(jobPostingApplication)) {
            return;
        }
        Map<String, String> context = Maps.newHashMap();

        JobPosting jobPosting = jobPostingDao.findOne(jobPostingApplication.getJobPosting().getId());
        PracticeOwner practiceOwner = jobPosting.getLocation().getPractice().getOwner();
        Professional professional = jobPostingApplication.getProfessional();

        Notification notification = new Notification();
        notification.setEmail(practiceOwner.getUsername());
        notification.setPhone(practiceOwner.getContact().getPhone());
        notification.setType(JOB_POSTING_APPLICATION_WITHDRAWN);

        practiceOwnerVariables.supply(practiceOwner, context);
        professionalNameVariables.supply(professional, context);
        jobPostingVariables.supply(jobPosting, context);
        jobPostingApplicationStartDateTimeVariables.supply(jobPostingApplication, context);

        notification.setContext(context);
        notificationService.send(notification);
    }
}
