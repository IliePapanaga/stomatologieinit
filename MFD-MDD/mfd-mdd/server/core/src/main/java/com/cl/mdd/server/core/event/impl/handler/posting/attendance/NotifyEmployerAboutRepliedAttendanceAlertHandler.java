package com.cl.mdd.server.core.event.impl.handler.posting.attendance;

import com.cl.mdd.server.core.data.persistent.access.posting.AttendanceAlertDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.professional.AttendanceAlert;
import com.cl.mdd.server.core.data.persistent.model.user.professional.AttendanceAlertReply;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.attendance.AttendanceAlertRepliedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Consumer
public class NotifyEmployerAboutRepliedAttendanceAlertHandler implements EventHandler<AttendanceAlertRepliedEvent> {

    private static final String ALERT_ATTENDANCE_REPLIED_FOR_EMPLOYER = "ALERT_ATTENDANCE_REPLIED_FOR_EMPLOYER";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AttendanceAlertDao attendanceAlertDao;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private AttendanceAlertReplyVariables attendanceAlertReplyVariables;

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
    @NotificationDefinition(value = ALERT_ATTENDANCE_REPLIED_FOR_EMPLOYER,
            predefined = {
                    PracticeOwnerVariables.class,
                    ProfessionalNameVariables.class,
                    AttendanceAlertReplyVariables.class,
                    JobPostingVariables.class,
                    JobDayStartDateTimeVariables.class,
                    PracticeLocationVariables.class,
                    AdminVariables.class
            })
    public void onEvent(AttendanceAlertRepliedEvent event, long sequence, boolean endOfBatch) {
        AttendanceAlert attendanceAlert = attendanceAlertDao.findOne(event.getAttendanceAlertId());
        if (Objects.isNull(attendanceAlert)) {
            return;
        }

        AttendanceAlertReply attendanceAlertReply = attendanceAlert.getReply();
        JobDay jobDay = attendanceAlert.getJobDay();
        Professional professional = attendanceAlert.getProfessional();
        JobPosting jobPosting = jobDay.getJobPosting();
        PracticeLocation location = jobPosting.getLocation();
        PracticeOwner practiceOwner = location.getPractice().getOwner();

        Map<String, String> context = Maps.newHashMap();

        Notification notification = new Notification();
        notification.setEmail(practiceOwner.getUsername());
        notification.setPhone(practiceOwner.getContact().getPhone());
        notification.setType(ALERT_ATTENDANCE_REPLIED_FOR_EMPLOYER);

        practiceOwnerVariables.supply(practiceOwner, context);
        professionalNameVariables.supply(professional, context);
        attendanceAlertReplyVariables.supply(attendanceAlertReply, context);
        jobPostingVariables.supply(jobPosting, context);
        jobDayStartDateTimeVariables.supply(jobDay, context);
        practiceLocationVariables.supply(location, context);
        adminVariables.supply(null, context);

        notification.setContext(context);
        notificationService.send(notification);
    }
}
