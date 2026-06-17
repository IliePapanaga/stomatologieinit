package com.cl.mdd.server.core.event.impl.handler.posting.attendance;

import com.cl.mdd.server.core.data.persistent.access.posting.JobDayDao;
import com.cl.mdd.server.core.data.persistent.access.posting.TemporaryJobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.attendance.WorkStartedEvent;
import com.cl.mdd.server.core.service.notification.JobPostingVariables;
import com.cl.mdd.server.core.service.notification.Notification;
import com.cl.mdd.server.core.service.notification.NotificationService;
import com.cl.mdd.server.core.service.notification.PracticeLocationVariables;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Consumer
public class NotifySystemAboutWorkStartedHandler implements EventHandler<WorkStartedEvent> {

    private static final String WORK_STARTED_FOR_SYSTEM = "WORK_STARTED_FOR_SYSTEM";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TemporaryJobPostingApplicationDao temporaryJobPostingApplicationDao;

    @Autowired
    private SystemUserDao systemUserDao;

    @Autowired
    private JobDayDao jobDayDao;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Override
    @Transactional
    @NotificationDefinition(value = WORK_STARTED_FOR_SYSTEM,
            predefined = {
                    JobPostingVariables.class,
                    PracticeLocationVariables.class
            })
    public void onEvent(WorkStartedEvent event, long sequence, boolean endOfBatch) {
        JobDay day = jobDayDao.findOne(event.getJobDayId());
        if (day == null || day.isNotifiedAboutStarted()) {
            return;
        }

        JobPosting jobPosting = day.getJobPosting();
        PracticeLocation location = jobPosting.getLocation();

        TemporaryJobPostingApplication db = temporaryJobPostingApplicationDao.findOneByAttendanceId(event.getJobDayId());
        if (Objects.isNull(db)) {
            return;
        }

        systemUserDao.findAll().stream().filter(user -> !user.getStatus().equals(User.INACTIVE)).forEach(systemUser -> {
            Map<String, String> context = Maps.newHashMap();

            Notification notification = new Notification();
            notification.setEmail(systemUser.getUsername());
            notification.setPhone(systemUser.getContact().getPhone());
            notification.setType(WORK_STARTED_FOR_SYSTEM);

            jobPostingVariables.supply(jobPosting, context);
            practiceLocationVariables.supply(location, context);

            notification.setContext(context);
            notificationService.send(notification);
        });
    }
}
