package com.cl.mdd.server.core.event.impl.handler.posting;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.JobPostingDeletedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.util.Objects.isNull;

@Consumer
public class JobPostingDeletedEventHandler implements EventHandler<JobPostingDeletedEvent> {

    private static final String JOB_POSTING_DELETED = "JOB_POSTING_DELETED";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobPostingDao jobPostingDao;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private JobPostingStartDateTimeVariables jobPostingStartDateTimeVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional
    @NotificationDefinition(value = JOB_POSTING_DELETED,
            predefined = {
                    PracticeOwnerVariables.class,
                    JobPostingVariables.class,
                    JobPostingStartDateTimeVariables.class,
                    AdminVariables.class
            })
    public void onEvent(JobPostingDeletedEvent event, long sequence, boolean endOfBatch) {
        JobPosting jobPosting = jobPostingDao.findOne(event.getJobPostingId());
        if (isNull(jobPosting)) {
            return;
        }
        Map<String, String> context = Maps.newHashMap();

        PracticeOwner practiceOwner = jobPosting.getLocation().getPractice().getOwner();

        Notification notification = new Notification();
        notification.setEmail(practiceOwner.getUsername());
        notification.setPhone(practiceOwner.getContact().getPhone());
        notification.setType(JOB_POSTING_DELETED);

        practiceOwnerVariables.supply(practiceOwner, context);
        jobPostingVariables.supply(jobPosting, context);
        jobPostingStartDateTimeVariables.supply(jobPosting, context);
        adminVariables.supply(null, context);

        notification.setContext(context);
        notificationService.send(notification);
    }
}
