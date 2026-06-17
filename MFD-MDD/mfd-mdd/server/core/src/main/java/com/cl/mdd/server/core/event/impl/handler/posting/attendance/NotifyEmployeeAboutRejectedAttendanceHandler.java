package com.cl.mdd.server.core.event.impl.handler.posting.attendance;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.access.user.EmployeeRejectedDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.EmployeeRejected;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.attendance.AttendanceRejectedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Consumer
public class NotifyEmployeeAboutRejectedAttendanceHandler implements EventHandler<AttendanceRejectedEvent> {

    private static final String REJECTED_ATTENDANCE_FOR_EMPLOYEE = "REJECTED_ATTENDANCE_FOR_EMPLOYEE";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmployeeRejectedDao employeeRejectedDao;

    @Autowired
    private JobPostingApplicationDao jobPostingApplicationDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private JobPostingApplicationStartDateTimeVariables jobPostingApplicationStartDateTimeVariables;

    @Override
    @Transactional
    @NotificationDefinition(value = REJECTED_ATTENDANCE_FOR_EMPLOYEE,
            predefined = {
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    JobPostingApplicationStartDateTimeVariables.class
            })
    public void onEvent(AttendanceRejectedEvent event, long sequence, boolean endOfBatch) {
        EmployeeRejected employeeRejected = employeeRejectedDao.findOne(event.getAttendanceRejectionId());
        JobPostingApplication jobPostingApplication = jobPostingApplicationDao.findOne(event.getJobPostingApplicationId());
        if (Objects.isNull(employeeRejected)) {
            return;
        }
        Professional professional = employeeRejected.getProfessional();
        JobPosting jobPosting = jobPostingApplication.getJobPosting();
        PracticeLocation practiceLocation = jobPosting.getLocation();

        if (professional.isNotificationsEnabled() && !professional.getStatus().equalsIgnoreCase(User.INACTIVE)) {
            Map<String, String> context = Maps.newHashMap();

            Notification notification = new Notification();
            notification.setEmail(professional.getUsername());
            notification.setPhone(professional.getContact().getPhone());
            notification.setType(REJECTED_ATTENDANCE_FOR_EMPLOYEE);

            professionalNameVariables.supply(professional, context);
            jobPostingVariables.supply(jobPosting, context);
            practiceLocationVariables.supply(practiceLocation, context);
            jobPostingApplicationStartDateTimeVariables.supply(jobPostingApplication, context);

            notification.setContext(context);
            notificationService.send(notification);
        }
    }
}
