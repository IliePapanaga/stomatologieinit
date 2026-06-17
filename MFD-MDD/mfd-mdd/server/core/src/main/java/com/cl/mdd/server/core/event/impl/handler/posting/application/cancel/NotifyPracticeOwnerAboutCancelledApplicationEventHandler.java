package com.cl.mdd.server.core.event.impl.handler.posting.application.cancel;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.application.cancel.JobPostingApplicationCancelledEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.util.Objects.isNull;

@Consumer
public class NotifyPracticeOwnerAboutCancelledApplicationEventHandler implements EventHandler<JobPostingApplicationCancelledEvent> {

    private static final String JOB_POSTING_APPLICATION_CANCELLED_FOR_PRACTICE_OWNER = "JOB_POSTING_APPLICATION_CANCELLED_FOR_PRACTICE_OWNER";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobPostingApplicationDao jobPostingApplicationDao;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional(readOnly = true)
    @NotificationDefinition(value = JOB_POSTING_APPLICATION_CANCELLED_FOR_PRACTICE_OWNER,
            predefined = {
                    PracticeOwnerVariables.class,
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    ProfessionalNameVariables.class,
                    AdminVariables.class
            })
    public void onEvent(JobPostingApplicationCancelledEvent event, long sequence, boolean endOfBatch) {
        JobPostingApplication db = jobPostingApplicationDao.findOne(event.getJobPostingApplicationId());
        if (isNull(db)) {
            return;
        }
        Map<String, String> context = Maps.newHashMap();

        JobPosting jobPosting = db.getJobPosting();
        PracticeLocation location = jobPosting.getLocation();
        PracticeOwner practiceOwner = location.getPractice().getOwner();
        Professional professional = db.getProfessional();

        Notification notification = new Notification();
        notification.setEmail(practiceOwner.getUsername());
        notification.setPhone(practiceOwner.getContact().getPhone());
        notification.setType(JOB_POSTING_APPLICATION_CANCELLED_FOR_PRACTICE_OWNER);

        practiceOwnerVariables.supply(practiceOwner, context);
        jobPostingVariables.supply(jobPosting, context);
        practiceLocationVariables.supply(location, context);
        professionalNameVariables.supply(professional, context);
        adminVariables.supply(null, context);

        notification.setContext(context);
        notificationService.send(notification);
    }

}
