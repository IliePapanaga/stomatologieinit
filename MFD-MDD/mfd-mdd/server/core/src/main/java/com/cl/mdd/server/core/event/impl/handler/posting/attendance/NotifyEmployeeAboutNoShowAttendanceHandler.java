package com.cl.mdd.server.core.event.impl.handler.posting.attendance;

import com.cl.mdd.server.core.data.persistent.access.user.NoShowDao;
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
public class NotifyEmployeeAboutNoShowAttendanceHandler implements EventHandler<AttendanceNoShowEvent> {

    private static final String NO_SHOW_ATTENDANCE_FOR_EMPLOYEE = "NO_SHOW_ATTENDANCE_FOR_EMPLOYEE";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NoShowDao noShowDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private JobDayStartDateTimeVariables jobDayStartDateTimeVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional
    @NotificationDefinition(value = NO_SHOW_ATTENDANCE_FOR_EMPLOYEE,
            predefined = {
                    ProfessionalNameVariables.class,
                    PracticeLocationVariables.class,
                    JobDayStartDateTimeVariables.class,
                    AdminVariables.class
            })
    public void onEvent(AttendanceNoShowEvent event, long sequence, boolean endOfBatch) {
        NoShow noShow = noShowDao.findOne(event.getNoShowId());
        if (Objects.isNull(noShow)) {
            return;
        }

        Professional professional = noShow.getProfessional();
        JobDay jobDay = noShow.getJobDay();
        PracticeLocation location = jobDay.getJobPosting().getLocation();

        if (professional.isNotificationsEnabled() && !professional.getStatus().equalsIgnoreCase(User.INACTIVE)) {
            Map<String, String> context = Maps.newHashMap();

            Notification notification = new Notification();
            notification.setEmail(professional.getUsername());
            notification.setPhone(professional.getContact().getPhone());
            notification.setType(NO_SHOW_ATTENDANCE_FOR_EMPLOYEE);

            professionalNameVariables.supply(professional, context);
            practiceLocationVariables.supply(location, context);
            jobDayStartDateTimeVariables.supply(jobDay, context);
            adminVariables.supply(null, context);

            notification.setContext(context);
            notificationService.send(notification);
        }
    }
}
