package com.cl.mdd.server.core.event.impl.handler.posting.cancel;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingDao;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.JobPostingCancelledEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Consumer
public class NotifySystemUsersAboutCancelledJobPostingHandler implements EventHandler<JobPostingCancelledEvent> {

    private static final String JOB_POSTING_CANCELLED_FOR_SYSTEM_USER = "JOB_POSTING_CANCELLED_FOR_SYSTEM_USER";

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
    private JobPostingStartDateTimeVariables jobPostingStartDateTimeVariables;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Override
    @Transactional
    @NotificationDefinition(value = JOB_POSTING_CANCELLED_FOR_SYSTEM_USER,
            predefined = {
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    JobPostingStartDateTimeVariables.class,
                    PracticeOwnerVariables.class
            })
    public void onEvent(JobPostingCancelledEvent event, long sequence, boolean endOfBatch) {
        JobPosting jobPosting = jobPostingDao.findOne(event.getJobPostingId());
        if (Objects.isNull(jobPosting)) {
            return;
        }

        PracticeLocation practiceLocation = jobPosting.getLocation();
        PracticeOwner practiceOwner = practiceLocation.getPractice().getOwner();

        systemUserDao.findAll().stream().filter(user -> !user.getStatus().equals(User.INACTIVE)).forEach(systemUser -> {
            Map<String, String> context = Maps.newHashMap();

            Notification notification = new Notification();
            notification.setEmail(systemUser.getUsername());
            notification.setPhone(systemUser.getContact().getPhone());
            notification.setType(JOB_POSTING_CANCELLED_FOR_SYSTEM_USER);

            jobPostingVariables.supply(jobPosting, context);
            practiceLocationVariables.supply(practiceLocation, context);
            jobPostingStartDateTimeVariables.supply(jobPosting, context);
            practiceOwnerVariables.supply(practiceOwner, context);

            notification.setContext(context);
            notificationService.send(notification);
        });
    }
}
