package com.cl.mdd.server.core.event.impl.handler.posting.attendance;

import com.cl.mdd.server.core.data.persistent.access.posting.JobDayDao;
import com.cl.mdd.server.core.data.persistent.access.posting.TemporaryJobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.attendance.WorkStartedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Consumer
public class NotifyEmployeeAboutWorkStartedHandler implements EventHandler<WorkStartedEvent> {

    private static final String WORK_STARTED_FOR_EMPLOYEE = "WORK_STARTED_FOR_EMPLOYEE";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TemporaryJobPostingApplicationDao temporaryJobPostingApplicationDao;

    @Autowired
    private JobDayDao jobDayDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private JobPostingApplicationStartDateTimeVariables jobPostingApplicationStartDateTimeVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional
    @NotificationDefinition(value = WORK_STARTED_FOR_EMPLOYEE,
            predefined = {
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    JobPostingApplicationStartDateTimeVariables.class,
                    AdminVariables.class
            })
    public void onEvent(WorkStartedEvent event, long sequence, boolean endOfBatch) {
        JobDay day = jobDayDao.findOne(event.getJobDayId());
        if (day == null || day.isNotifiedAboutStarted()) {
            return;
        }

        TemporaryJobPostingApplication temporaryJobPostingApplication = temporaryJobPostingApplicationDao.findOneByAttendanceId(event.getJobDayId());
        if (Objects.isNull(temporaryJobPostingApplication)) {
            return;
        }
        Professional professional = temporaryJobPostingApplication.getProfessional();
        JobPosting jobPosting = temporaryJobPostingApplication.getJobPosting();
        PracticeLocation location = jobPosting.getLocation();

        if (professional.isNotificationsEnabled() && !professional.getStatus().equalsIgnoreCase(User.INACTIVE)) {
            Map<String, String> context = Maps.newHashMap();

            Notification notification = new Notification();
            notification.setEmail(professional.getUsername());
            notification.setPhone(professional.getContact().getPhone());
            notification.setType(WORK_STARTED_FOR_EMPLOYEE);

            professionalNameVariables.supply(professional, context);
            jobPostingVariables.supply(jobPosting, context);
            practiceLocationVariables.supply(location, context);
            jobPostingApplicationStartDateTimeVariables.supply(temporaryJobPostingApplication, context);
            adminVariables.supply(null, context);

            notification.setContext(context);
            notificationService.send(notification);
        }
    }
}
