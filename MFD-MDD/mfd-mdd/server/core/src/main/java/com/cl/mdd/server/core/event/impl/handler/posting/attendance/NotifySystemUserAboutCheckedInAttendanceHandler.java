package com.cl.mdd.server.core.event.impl.handler.posting.attendance;

import com.cl.mdd.server.core.data.persistent.access.posting.CheckInDao;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.CheckIn;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.attendance.AttendanceCheckedInEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Consumer
public class NotifySystemUserAboutCheckedInAttendanceHandler implements EventHandler<AttendanceCheckedInEvent> {

    private static final String CHECKED_IN_ATTENDANCE_FOR_SYSTEM_USER = "CHECKED_IN_ATTENDANCE_FOR_SYSTEM_USER";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CheckInDao checkInDao;

    @Autowired
    private SystemUserDao systemUserDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private JobDayStartDateTimeVariables jobDayStartDateTimeVariables;

    @Override
    @Transactional
    @NotificationDefinition(value = CHECKED_IN_ATTENDANCE_FOR_SYSTEM_USER,
    predefined = {
            ProfessionalNameVariables.class,
            JobPostingVariables.class,
            PracticeLocationVariables.class,
            JobDayStartDateTimeVariables.class
    })
    public void onEvent(AttendanceCheckedInEvent event, long sequence, boolean endOfBatch) {
        CheckIn checkIn = checkInDao.findOne(event.getCheckInId());
        if (Objects.isNull(checkIn)) {
            return;
        }

        Professional professional = checkIn.getProfessional();
        JobDay jobDay = checkIn.getJobDay();
        JobPosting jobPosting = jobDay.getJobPosting();
        PracticeLocation location = jobPosting.getLocation();

        systemUserDao.findAll().stream().filter(user -> !user.getStatus().equals(User.INACTIVE)).forEach(systemUser -> {
            Map<String, String> context = Maps.newHashMap();

            Notification notification = new Notification();
            notification.setEmail(systemUser.getUsername());
            notification.setPhone(systemUser.getContact().getPhone());
            notification.setType(CHECKED_IN_ATTENDANCE_FOR_SYSTEM_USER);

            professionalNameVariables.supply(professional, context);
            jobPostingVariables.supply(jobPosting, context);
            practiceLocationVariables.supply(location, context);
            jobDayStartDateTimeVariables.supply(jobDay, context);

            notification.setContext(context);
            notificationService.send(notification);
        });
    }
}
