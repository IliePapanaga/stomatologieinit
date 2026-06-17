package com.cl.mdd.server.core.event.impl.handler.posting.application;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.application.JobPostingApplicationBookedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.util.Objects.isNull;

@Consumer
public class NotifyProfessionalAboutJobPostingApplicationBookedEventHandler implements EventHandler<JobPostingApplicationBookedEvent> {

    private static final String JOB_POSTING_APPLICATION_BOOKED_FOR_PROFESSIONAL = "JOB_POSTING_APPLICATION_BOOKED_FOR_PROFESSIONAL";

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

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional(readOnly = true)
    @NotificationDefinition(value = JOB_POSTING_APPLICATION_BOOKED_FOR_PROFESSIONAL,
            predefined = {
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    JobPostingApplicationStartDateTimeVariables.class,
                    AdminVariables.class
            })
    public void onEvent(JobPostingApplicationBookedEvent event, long sequence, boolean endOfBatch) {
        JobPostingApplication db = jobPostingApplicationDao.findOne(event.getJobPostingApplicationId());
        if (isNull(db)) {
            return;
        }

        Professional professional = db.getProfessional();
        if (professional.isNotificationsEnabled() && !professional.getStatus().equalsIgnoreCase(User.INACTIVE)) {
            Map<String, String> context = Maps.newHashMap();

            JobPosting jobPosting = db.getJobPosting();
            PracticeLocation location = jobPosting.getLocation();

            Notification notification = new Notification();
            notification.setEmail(professional.getUsername());
            notification.setPhone(professional.getContact().getPhone());
            notification.setType(JOB_POSTING_APPLICATION_BOOKED_FOR_PROFESSIONAL);

            professionalNameVariables.supply(professional, context);
            jobPostingVariables.supply(jobPosting, context);
            practiceLocationVariables.supply(location, context);
            jobPostingApplicationStartDateTimeVariables.supply(db, context);
            adminVariables.supply(null, context);

            notification.setContext(context);
            notificationService.send(notification);
        }
    }

}
