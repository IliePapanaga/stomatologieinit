package com.cl.mdd.server.core.event.impl.handler.posting.attendance;

import com.cl.mdd.server.core.data.persistent.access.posting.SosRequestDao;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.SosRequest;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.attendance.AttendanceSosRequestedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Consumer
public class NotifySystemAboutAttendanceSosRequestedHandler implements EventHandler<AttendanceSosRequestedEvent> {

    private static final String ATTENDANCE_SOS_REQUESTED_FOR_SYSTEM = "ATTENDANCE_SOS_REQUESTED_FOR_SYSTEM";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SosRequestDao sosRequestDao;

    @Autowired
    private SystemUserDao systemUserDao;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

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
    @NotificationDefinition(value = ATTENDANCE_SOS_REQUESTED_FOR_SYSTEM,
            predefined = {
                    PracticeOwnerVariables.class,
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    JobDayStartDateTimeVariables.class
            })

    public void onEvent(AttendanceSosRequestedEvent event, long sequence, boolean endOfBatch) {
        SosRequest sosRequest = sosRequestDao.findOne(event.getSosRequestId());
        if (Objects.isNull(sosRequest)) {
            return;
        }

        JobDay jobDay = sosRequest.getJobDay();
        JobPosting jobPosting = jobDay.getJobPosting();
        PracticeLocation location = jobPosting.getLocation();
        PracticeOwner owner = location.getPractice().getOwner();
        Professional professional = sosRequest.getProfessional();

        systemUserDao.findAll().stream().filter(user -> !user.getStatus().equals(User.INACTIVE)).forEach(systemUser -> {
            Map<String, String> context = Maps.newHashMap();

            Notification notification = new Notification();
            notification.setEmail(systemUser.getUsername());
            notification.setPhone(systemUser.getContact().getPhone());
            notification.setType(ATTENDANCE_SOS_REQUESTED_FOR_SYSTEM);

            practiceOwnerVariables.supply(owner, context);
            professionalNameVariables.supply(professional, context);
            jobPostingVariables.supply(jobPosting, context);
            practiceLocationVariables.supply(location, context);
            jobDayStartDateTimeVariables.supply(jobDay, context);

            notification.setContext(context);
            notificationService.send(notification);
        });
    }
}
