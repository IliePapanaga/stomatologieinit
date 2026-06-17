package com.cl.mdd.server.core.event.impl.handler.posting.update;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingDao;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.SystemUser;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.JobPostingUpdatedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.util.Objects.isNull;

@Consumer
public class NotifySystemUsersAboutJobPostingUpdateHandler implements EventHandler<JobPostingUpdatedEvent> {

    private static final String JOB_POSTING_UPDATED_FOR_SYSTEM_USER = "JOB_POSTING_UPDATED_FOR_SYSTEM_USER";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemUserDao systemUserDao;

    @Autowired
    private JobPostingDao jobPostingDao;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Autowired
    private JobPostingStartDateTimeVariables jobPostingStartDateTimeVariables;

    @Override
    @Transactional
    @NotificationDefinition(value = JOB_POSTING_UPDATED_FOR_SYSTEM_USER,
            predefined = {
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    PracticeOwnerVariables.class,
                    JobPostingStartDateTimeVariables.class
            })
    public void onEvent(JobPostingUpdatedEvent event, long sequence, boolean endOfBatch) {
        JobPosting jobPosting = jobPostingDao.findOne(event.getJobPostingId());
        if (isNull(jobPosting)) {
            return;
        }

        PracticeLocation location = jobPosting.getLocation();
        PracticeOwner practiceOwner = location.getPractice().getOwner();

        systemUserDao.findAll().stream().filter(this::isUserActive).forEach(systemUser -> {
            Map<String, String> context = Maps.newHashMap();

            Notification notification = new Notification();
            notification.setEmail(systemUser.getUsername());
            notification.setPhone(systemUser.getContact().getPhone());
            notification.setType(JOB_POSTING_UPDATED_FOR_SYSTEM_USER);

            jobPostingVariables.supply(jobPosting, context);
            practiceLocationVariables.supply(location, context);
            practiceOwnerVariables.supply(practiceOwner, context);
            jobPostingStartDateTimeVariables.supply(jobPosting, context);

            notification.setContext(context);
            notificationService.send(notification);
        });
    }

    private boolean isUserActive(SystemUser user) {
        return !user.getStatus().equals(User.INACTIVE);
    }
}
