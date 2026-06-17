package com.cl.mdd.server.core.event.impl.handler.posting.update;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.JobPostingUpdatedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

@Consumer
public class NotifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler implements EventHandler<JobPostingUpdatedEvent> {

    private static final String JOB_POSTING_UPDATED_FOR_CANCELLED_APPLICANTS = "JOB_POSTING_UPDATED_FOR_CANCELLED_APPLICANTS";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobPostingDao jobPostingDao;

    @Autowired
    private JobPostingApplicationDao jobPostingApplicationDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private JobPostingStartDateTimeVariables jobPostingStartDateTimeVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional
    @NotificationDefinition(value = JOB_POSTING_UPDATED_FOR_CANCELLED_APPLICANTS,
            predefined = {
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    JobPostingStartDateTimeVariables.class,
                    AdminVariables.class
            })
    public void onEvent(JobPostingUpdatedEvent event, long sequence, boolean endOfBatch) {
        JobPosting jobPosting = jobPostingDao.findOne(event.getJobPostingId());
        if (isNull(jobPosting)) {
            return;
        }
        List<JobPostingApplication> jobPostingApplications = jobPostingApplicationDao.findCancelledApplicationDuringJobPostingUpdateApplicants(event.getJobPostingId());

        Map<Professional, JobPostingApplication> professionalToLatestApplication = jobPostingApplications.stream()
                .collect(groupingBy(JobPostingApplication::getProfessional,
                        reducing(null, this::reduceToLatestApplication)));

        professionalToLatestApplication.entrySet().stream()
                .filter(entry -> isNotificationCanBeSent(entry.getValue()))
                .forEach(entry -> {

                    Map<String, String> context = Maps.newHashMap();

                    Professional professional = entry.getKey();
                    PracticeLocation location = jobPosting.getLocation();

                    Notification notification = new Notification();
                    notification.setEmail(professional.getUsername());
                    notification.setPhone(professional.getContact().getPhone());
                    notification.setType(JOB_POSTING_UPDATED_FOR_CANCELLED_APPLICANTS);

                    professionalNameVariables.supply(professional, context);
                    jobPostingVariables.supply(jobPosting, context);
                    practiceLocationVariables.supply(location, context);
                    jobPostingStartDateTimeVariables.supply(jobPosting, context);
                    adminVariables.supply(null, context);

                    notification.setContext(context);
                    notificationService.send(notification);
                });
    }

    private JobPostingApplication reduceToLatestApplication(JobPostingApplication left, JobPostingApplication right) {
        if (left == null) {
            return right;
        }
        return left.getCreated().isAfter(right.getCreated()) ? left : right;
    }

    private boolean isNotificationCanBeSent(JobPostingApplication jobPostingApplication) {
        if (jobPostingApplication == null) {
            return false;
        }
        Professional professional = jobPostingApplication.getProfessional();
        return professional.isNotificationsEnabled()
                && !professional.getStatus().equalsIgnoreCase(User.INACTIVE);
    }
}
