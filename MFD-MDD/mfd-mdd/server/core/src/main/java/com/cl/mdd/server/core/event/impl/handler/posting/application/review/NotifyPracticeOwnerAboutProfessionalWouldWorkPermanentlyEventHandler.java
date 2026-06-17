package com.cl.mdd.server.core.event.impl.handler.posting.application.review;

import com.cl.mdd.server.core.data.persistent.access.posting.ProfessionalPracticeLocationReviewDao;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.review.ProfessionalPracticeLocationReview;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.application.review.ProfessionalHasReviewedLocationEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.util.Objects.isNull;

@Consumer
public class NotifyPracticeOwnerAboutProfessionalWouldWorkPermanentlyEventHandler implements EventHandler<ProfessionalHasReviewedLocationEvent> {

    private static final String PROFESSIONAL_WOULD_WORK_PERMANENTLY_FOR_PRACTICE_OWNER = "PROFESSIONAL_WOULD_WORK_PERMANENTLY_FOR_PRACTICE_OWNER";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProfessionalPracticeLocationReviewDao professionalPracticeLocationReviewDao;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional(readOnly = true)
    @NotificationDefinition(value = PROFESSIONAL_WOULD_WORK_PERMANENTLY_FOR_PRACTICE_OWNER,
            predefined = {
                    PracticeOwnerVariables.class,
                    ProfessionalNameVariables.class,
                    PracticeLocationVariables.class,
                    AdminVariables.class
            })
    public void onEvent(ProfessionalHasReviewedLocationEvent event, long sequence, boolean endOfBatch) {
        ProfessionalPracticeLocationReview review = professionalPracticeLocationReviewDao.findOne(event.getId());
        if (isNull(review)) {
            return;
        }

        if (review.isWouldWorkPermanently()) {
            Map<String, String> context = Maps.newHashMap();

            TemporaryJobPostingApplication jobPostingApplication = review.getApplication();
            Professional professional = jobPostingApplication.getProfessional();
            PracticeLocation location = jobPostingApplication.getJobPosting().getLocation();
            PracticeOwner practiceOwner = jobPostingApplication.getJobPosting().getLocation().getPractice().getOwner();

            Notification notification = new Notification();
            notification.setEmail(practiceOwner.getUsername());
            notification.setPhone(practiceOwner.getContact().getPhone());
            notification.setType(PROFESSIONAL_WOULD_WORK_PERMANENTLY_FOR_PRACTICE_OWNER);

            practiceOwnerVariables.supply(practiceOwner, context);
            professionalNameVariables.supply(professional, context);
            practiceLocationVariables.supply(location, context);
            adminVariables.supply(null, context);

            notification.setContext(context);
            notificationService.send(notification);
        }
    }
}
