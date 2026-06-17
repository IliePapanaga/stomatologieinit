package com.cl.mdd.server.core.event.impl.handler.posting;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.specialty.Category;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.JobPostingPublishedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Consumer
public class NotifyProfessionalAboutJobPostingPublishedEventHandler implements EventHandler<JobPostingPublishedEvent> {

    private static final String JOB_POSTING_PUBLISHED_FOR_PROFESSIONAL = "JOB_POSTING_PUBLISHED_FOR_PROFESSIONAL";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobPostingDao jobPostingDao;

    @Value("${professional.penalties.denials.tolerance:5}")
    private int maxDenials;

    @Value("${professional.penalties.no.show.tolerance:2}")
    private int maxNoShows;

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
    @NotificationDefinition(value = JOB_POSTING_PUBLISHED_FOR_PROFESSIONAL,
            predefined = {
                    ProfessionalNameVariables.class,
                    JobPostingVariables.class,
                    PracticeLocationVariables.class,
                    JobPostingStartDateTimeVariables.class,
                    AdminVariables.class
            })
    public void onEvent(JobPostingPublishedEvent event, long sequence, boolean endOfBatch) {
        JobPosting jobPosting = jobPostingDao.findOne(event.getJobPostingId());
        if (isNull(jobPosting)) {
            return;
        }
        List<Professional> targetProfessionalsForJobPosting = jobPostingDao.findTargetProfessionalsForJobPosting(event.getJobPostingId(), maxDenials, maxNoShows);

        PracticeLocation practiceLocation = jobPosting.getLocation();

        targetProfessionalsForJobPosting.stream().filter(p -> checkCategory(jobPosting, p)).forEach(professional -> {
            Map<String, String> context = Maps.newHashMap();

            Notification notification = new Notification();
            notification.setEmail(professional.getUsername());
            notification.setPhone(professional.getContact().getPhone());
            notification.setType(JOB_POSTING_PUBLISHED_FOR_PROFESSIONAL);

            professionalNameVariables.supply(professional, context);
            jobPostingVariables.supply(jobPosting, context);
            practiceLocationVariables.supply(practiceLocation, context);
            jobPostingStartDateTimeVariables.supply(jobPosting, context);
            adminVariables.supply(null, context);

            notification.setContext(context);
            notificationService.send(notification);
        });
    }

    // TODO 2020-05-03 : temp, may be done in jpql query
    // allow only if Professional has one of Categories from Job posting
    private boolean checkCategory(JobPosting jobPosting, Professional professional) {
        Set<Category> categories = jobPosting.getSubCategories().stream().map(SubCategory::getCategory).collect(Collectors.toSet());

        return professional.getSubCategories().stream().map(SubCategory::getCategory).anyMatch(categories::contains);
    }

}
