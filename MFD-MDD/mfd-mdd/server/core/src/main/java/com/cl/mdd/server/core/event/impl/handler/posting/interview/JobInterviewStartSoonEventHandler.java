package com.cl.mdd.server.core.event.impl.handler.posting.interview;

import com.cl.mdd.server.core.data.persistent.access.posting.JobInterviewDao;
import com.cl.mdd.server.core.data.persistent.model.contact.Address;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.PermanentJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.interview.JobInterviewStartSoonEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.util.Objects.isNull;

@Consumer
public class JobInterviewStartSoonEventHandler implements EventHandler<JobInterviewStartSoonEvent> {

    private static final String JOB_INTERVIEW_START_SOON = "JOB_INTERVIEW_START_SOON";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobInterviewDao jobInterviewDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private JobInterviewStartDateVariables jobInterviewStartDateVariables;

    @Autowired
    private PracticeLocationAddressVariables practiceLocationAddressVariables;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional(readOnly = true)
    @NotificationDefinition(value = JOB_INTERVIEW_START_SOON,
            predefined = {
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    JobInterviewStartDateVariables.class,
                    PracticeLocationAddressVariables.class,
                    PracticeOwnerVariables.class,
                    AdminVariables.class
            })

    public void onEvent(JobInterviewStartSoonEvent event, long sequence, boolean endOfBatch) {
        JobInterview jobInterview = jobInterviewDao.findOne(event.getInterviewId());
        if (isNull(jobInterview) || jobInterview.isNotifiedAboutStartSoon()) {
            return;
        }

        PermanentJobPostingApplication jobPostingApplication = jobInterview.getApplication();
        JobPosting jobPosting = jobPostingApplication.getJobPosting();
        PracticeLocation location = jobPosting.getLocation();
        PracticeOwner practiceOwner = location.getPractice().getOwner();
        Address address = location.getContact().getAddress();

        Professional professional = jobPostingApplication.getProfessional();

        if (professional.isNotificationsEnabled() && !professional.getStatus().equalsIgnoreCase(User.INACTIVE)) {
            Map<String, String> context = Maps.newHashMap();

            Notification notification = new Notification();
            notification.setEmail(professional.getUsername());
            notification.setPhone(professional.getContact().getPhone());
            notification.setType(JOB_INTERVIEW_START_SOON);

            professionalNameVariables.supply(professional, context);
            jobPostingVariables.supply(jobPosting, context);
            practiceLocationVariables.supply(location, context);
            jobInterviewStartDateVariables.supply(jobInterview, context);
            practiceLocationAddressVariables.supply(address, context);
            practiceOwnerVariables.supply(practiceOwner, context);
            adminVariables.supply(null, context);

            notification.setContext(context);
            notificationService.send(notification);
        }
    }
}
