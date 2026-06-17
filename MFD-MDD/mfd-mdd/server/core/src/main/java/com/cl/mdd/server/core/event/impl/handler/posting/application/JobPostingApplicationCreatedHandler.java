package com.cl.mdd.server.core.event.impl.handler.posting.application;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.application.JobPostingApplicationCreatedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.util.Objects.isNull;

@Consumer
public class JobPostingApplicationCreatedHandler implements EventHandler<JobPostingApplicationCreatedEvent> {

    private static final String JOB_POSTING_APPLICATION_CREATED = "JOB_POSTING_APPLICATION_CREATED";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobPostingApplicationDao temporaryJobPostingApplicationDao;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private JobPostingApplicationStartDateTimeVariables jobPostingApplicationStartDateTimeVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional
    @NotificationDefinition(value = JOB_POSTING_APPLICATION_CREATED,
            predefined = {
                    PracticeOwnerVariables.class,
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    JobPostingApplicationStartDateTimeVariables.class,
                    AdminVariables.class
            })
    public void onEvent(JobPostingApplicationCreatedEvent event, long sequence, boolean endOfBatch) {
        JobPostingApplication jobPostingApplication = temporaryJobPostingApplicationDao.findOne(event.getJobPostingApplicationId());
        if (isNull(jobPostingApplication)) {
            return;
        }
        Map<String, String> context = Maps.newHashMap();

        Professional professional = jobPostingApplication.getProfessional();
        JobPosting jobPosting = jobPostingApplication.getJobPosting();
        PracticeLocation location = jobPosting.getLocation();
        PracticeOwner practiceOwner = location.getPractice().getOwner();

        Notification notification = new Notification();
        notification.setEmail(practiceOwner.getUsername());
        notification.setPhone(practiceOwner.getContact().getPhone());
        notification.setType(JOB_POSTING_APPLICATION_CREATED);

        practiceOwnerVariables.supply(practiceOwner, context);
        professionalNameVariables.supply(professional, context);
        jobPostingVariables.supply(jobPosting, context);
        practiceLocationVariables.supply(location, context);
        jobPostingApplicationStartDateTimeVariables.supply(jobPostingApplication, context);
        adminVariables.supply(null, context);

        notification.setContext(context);
        notificationService.send(notification);
    }
}
