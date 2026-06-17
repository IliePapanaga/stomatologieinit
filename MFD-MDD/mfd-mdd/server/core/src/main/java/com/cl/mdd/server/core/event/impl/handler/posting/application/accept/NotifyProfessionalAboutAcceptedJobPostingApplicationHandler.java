package com.cl.mdd.server.core.event.impl.handler.posting.application.accept;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.model.contact.Address;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.application.JobPostingApplicationAcceptedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.util.Objects.isNull;

@Consumer
public class NotifyProfessionalAboutAcceptedJobPostingApplicationHandler implements EventHandler<JobPostingApplicationAcceptedEvent> {

    private static final String JOB_POSTING_APPLICATION_ACCEPTED_FOR_PROFESSIONAL = "JOB_POSTING_APPLICATION_ACCEPTED_FOR_PROFESSIONAL";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobPostingApplicationDao jobPostingApplicationDao;

    @Autowired
    private PracticeLocationAddressVariables practiceLocationAddressVariables;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private JobPostingVariables jobPostingVariables;

    @Autowired
    private JobPostingApplicationStartDateTimeVariables jobPostingApplicationStartDateTimeVariables;

    @Autowired
    private JobPostingApplicationEndDateTimeVariables jobPostingApplicationEndDateTimeVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional(readOnly = true)
    @NotificationDefinition(value = JOB_POSTING_APPLICATION_ACCEPTED_FOR_PROFESSIONAL,
            vars = {@Variable(macro = "{professional.preferences.rate}", name = "notification.var.professional.preferences.rate"),
                    @Variable(macro = "{practice.location.phonenumber}", name = "notification.var.practice.location.phonenumber")},
            predefined = {
                    PracticeLocationAddressVariables.class,
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    JobPostingApplicationStartDateTimeVariables.class,
                    JobPostingApplicationEndDateTimeVariables.class,
                    PracticeLocationVariables.class,
                    PracticeOwnerVariables.class,
                    AdminVariables.class
            })
    public void onEvent(JobPostingApplicationAcceptedEvent event, long sequence, boolean endOfBatch) {
        JobPostingApplication jobPostingApplication = jobPostingApplicationDao.findOne(event.getJobPostingApplicationId());
        if (isNull(jobPostingApplication)) {
            return;
        }
        Map<String, String> context = Maps.newHashMap();

        PracticeLocation location = jobPostingApplication.getJobPosting().getLocation();
        PracticeOwner practiceOwner = location.getPractice().getOwner();
        Professional professional = jobPostingApplication.getProfessional();
        Address address = location.getContact().getAddress();

        Notification notification = new Notification();
        notification.setEmail(professional.getUsername());
        notification.setPhone(professional.getContact().getPhone());
        notification.setType(JOB_POSTING_APPLICATION_ACCEPTED_FOR_PROFESSIONAL);

        practiceLocationAddressVariables.supply(address, context);
        professionalNameVariables.supply(professional, context);
        jobPostingVariables.supply(jobPostingApplication.getJobPosting(), context);
        jobPostingApplicationStartDateTimeVariables.supply(jobPostingApplication, context);
        jobPostingApplicationEndDateTimeVariables.supply(jobPostingApplication, context);
        practiceLocationVariables.supply(location, context);
        practiceOwnerVariables.supply(practiceOwner, context);
        adminVariables.supply(null, context);

        context.put("{professional.preferences.rate}", String.valueOf(professional.getProfessionalJobPreference().getDesiredRatePerHour()));
        context.put("{practice.location.phonenumber}", location.getContact().getPhone());

        notification.setContext(context);
        notificationService.send(notification);
    }

}
