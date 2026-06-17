package com.cl.mdd.server.core.event.impl.handler.posting.application;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
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
public class NotifySystemUserAboutJobPostingApplicationBookedEventHandler implements EventHandler<JobPostingApplicationBookedEvent> {

    private static final String JOB_POSTING_APPLICATION_BOOKED_FOR_SYSTEM_USER = "JOB_POSTING_APPLICATION_BOOKED_FOR_SYSTEM_USER";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobPostingApplicationDao jobPostingApplicationDao;

    @Autowired
    private SystemUserDao systemUserDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private JobPostingApplicationStartDateTimeVariables jobPostingApplicationStartDateTimeVariables;

    @Override
    @Transactional(readOnly = true)
    @NotificationDefinition(value = JOB_POSTING_APPLICATION_BOOKED_FOR_SYSTEM_USER,
            predefined = {
                    ProfessionalNameVariables.class,
                    PracticeOwnerVariables.class,
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    JobPostingApplicationStartDateTimeVariables.class
            })
    public void onEvent(JobPostingApplicationBookedEvent event, long sequence, boolean endOfBatch) {
        JobPostingApplication db = jobPostingApplicationDao.findOne(event.getJobPostingApplicationId());
        if (isNull(db)) {
            return;
        }
        systemUserDao.findAll().stream().filter(user -> !user.getStatus().equals(User.INACTIVE)).forEach(systemUser -> {
            Map<String, String> context = Maps.newHashMap();

            Professional professional = db.getProfessional();
            JobPosting jobPosting = db.getJobPosting();
            PracticeOwner practiceOwner = jobPosting.getLocation().getPractice().getOwner();
            PracticeLocation location = jobPosting.getLocation();

            Notification notification = new Notification();
            notification.setEmail(systemUser.getUsername());
            notification.setPhone(systemUser.getContact().getPhone());
            notification.setType(JOB_POSTING_APPLICATION_BOOKED_FOR_SYSTEM_USER);

            professionalNameVariables.supply(professional, context);
            practiceOwnerVariables.supply(practiceOwner, context);
            jobPostingVariables.supply(jobPosting, context);
            practiceLocationVariables.supply(location, context);
            jobPostingApplicationStartDateTimeVariables.supply(db, context);

            notification.setContext(context);
            notificationService.send(notification);
        });

    }

}
