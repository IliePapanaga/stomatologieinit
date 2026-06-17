package com.cl.mdd.server.core.event.impl.handler.posting.attendance;

import com.cl.mdd.server.core.data.persistent.access.posting.AttendanceAlertDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.AttendanceAlert;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.attendance.AttendanceAlertedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Consumer
public class NotifyEmployeeAboutAlertedAttendanceHandler implements EventHandler<AttendanceAlertedEvent> {

    private static final String ALERTED_ATTENDANCE_FOR_EMPLOYEE = "ALERTED_ATTENDANCE_FOR_EMPLOYEE";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AttendanceAlertDao attendanceAlertDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private JobDayStartDateTimeVariables jobDayStartDateTimeVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional
    @NotificationDefinition(value = ALERTED_ATTENDANCE_FOR_EMPLOYEE,
            predefined = {
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    JobDayStartDateTimeVariables.class,
                    PracticeLocationVariables.class,
                    AdminVariables.class
            })
    public void onEvent(AttendanceAlertedEvent event, long sequence, boolean endOfBatch) {
        AttendanceAlert attendanceAlert = attendanceAlertDao.findOne(event.getAlertId());
        if (Objects.isNull(attendanceAlert)) {
            return;
        }

        Professional professional = attendanceAlert.getProfessional();
        JobDay jobDay = attendanceAlert.getJobDay();
        JobPosting jobPosting = jobDay.getJobPosting();
        PracticeLocation location = jobPosting.getLocation();

        if (professional.isNotificationsEnabled() && !professional.getStatus().equalsIgnoreCase(User.INACTIVE)) {
            Map<String, String> context = Maps.newHashMap();

            Notification notification = new Notification();
            notification.setEmail(professional.getUsername());
            notification.setPhone(professional.getContact().getPhone());
            notification.setType(ALERTED_ATTENDANCE_FOR_EMPLOYEE);

            professionalNameVariables.supply(professional, context);
            jobPostingVariables.supply(jobPosting, context);
            jobDayStartDateTimeVariables.supply(jobDay, context);
            practiceLocationVariables.supply(location, context);
            adminVariables.supply(null, context);

            notification.setContext(context);
            notificationService.send(notification);
        }
    }
}
