package com.cl.mdd.server.core.event.impl.handler.posting.update;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.JobPostingUpdatedEvent;
import com.cl.mdd.server.core.service.notification.Notification;
import com.cl.mdd.server.core.service.notification.NotificationService;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.collect.ImmutableMap.of;

@Consumer
public class NotifyApplicantsAboutPrematurelyCompletedApplicationsDuringJobPostingUpdateHandler implements EventHandler<JobPostingUpdatedEvent> {

    private static final String JOB_POSTING_UPDATED_FOR_PREMATURELY_COMPLETED_APPLICANTS = "JOB_POSTING_UPDATED_FOR_PREMATURELY_COMPLETED_APPLICANTS";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobPostingApplicationDao findJobPostingApplicants;

    @Override
    @Transactional
    @NotificationDefinition(JOB_POSTING_UPDATED_FOR_PREMATURELY_COMPLETED_APPLICANTS)
    public void onEvent(JobPostingUpdatedEvent event, long sequence, boolean endOfBatch) {
        List<Professional> jobPostingApplicants = findJobPostingApplicants.findPrematurelyCompletedApplicationDuringJobPostingUpdateApplicants(event.getJobPostingId());
        jobPostingApplicants.stream().filter(professional1 -> professional1.isNotificationsEnabled() && !professional1.getStatus().equalsIgnoreCase(User.INACTIVE)).forEach(professional -> {
            Notification notification = new Notification();
            notification.setEmail(professional.getUsername());
            notification.setPhone(professional.getContact().getPhone());
            notification.setType(JOB_POSTING_UPDATED_FOR_PREMATURELY_COMPLETED_APPLICANTS);
            notification.setContext(of());
            notificationService.send(notification);
        });
    }

}
