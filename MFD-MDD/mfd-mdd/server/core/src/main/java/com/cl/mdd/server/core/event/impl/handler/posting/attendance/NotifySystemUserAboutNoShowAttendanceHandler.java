package com.cl.mdd.server.core.event.impl.handler.posting.attendance;

import com.cl.mdd.server.core.data.persistent.access.user.NoShowDao;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.NoShow;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.attendance.AttendanceNoShowEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Consumer
public class NotifySystemUserAboutNoShowAttendanceHandler implements EventHandler<AttendanceNoShowEvent> {

    private static final String NO_SHOW_ATTENDANCE_FOR_SYSTEM_USER = "NO_SHOW_ATTENDANCE_FOR_SYSTEM_USER";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NoShowDao noShowDao;

    @Autowired
    private SystemUserDao systemUserDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private JobDayStartDateTimeVariables jobDayStartDateTimeVariables;

    @Override
    @Transactional
    @NotificationDefinition(value = NO_SHOW_ATTENDANCE_FOR_SYSTEM_USER,
            predefined = {
                    ProfessionalNameVariables.class,
                    PracticeLocationVariables.class,
                    JobPostingVariables.class,
                    JobDayStartDateTimeVariables.class
            })
    public void onEvent(AttendanceNoShowEvent event, long sequence, boolean endOfBatch) {
        NoShow noShow = noShowDao.findOne(event.getNoShowId());
        if (Objects.isNull(noShow)) {
            return;
        }

        Professional professional = noShow.getProfessional();
        JobDay jobDay = noShow.getJobDay();
        JobPosting jobPosting = jobDay.getJobPosting();
        PracticeLocation location = jobPosting.getLocation();

        systemUserDao.findAll().stream().filter(user -> !user.getStatus().equals(User.INACTIVE)).forEach(systemUser -> {
            Map<String, String> context = Maps.newHashMap();

            Notification notification = new Notification();
            notification.setEmail(systemUser.getUsername());
            notification.setPhone(systemUser.getContact().getPhone());
            notification.setType(NO_SHOW_ATTENDANCE_FOR_SYSTEM_USER);

            professionalNameVariables.supply(professional, context);
            practiceLocationVariables.supply(location, context);
            jobPostingVariables.supply(jobPosting, context);
            jobDayStartDateTimeVariables.supply(jobDay, context);

            notification.setContext(context);
            notificationService.send(notification);
        });
    }
}
