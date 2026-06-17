package com.cl.mdd.server.core.event.impl.handler.posting.application.review;

import com.cl.mdd.server.core.data.persistent.access.posting.PracticeLocationProfessionalReviewDao;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.review.PracticeLocationProfessionalReview;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.posting.application.review.LocationHasReviewedProfessionalEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.util.Objects.isNull;

@Consumer
public class NotifyProfessionalAboutPracticeOwnerWouldHirePermanentlyEventHandler implements EventHandler<LocationHasReviewedProfessionalEvent> {

    private static final String PRACTICE_OWNER_WOULD_HIRE_PROFESSIONAL_PERMANENTLY_FOR_PROFESSIONAL = "PRACTICE_OWNER_WOULD_HIRE_PROFESSIONAL_PERMANENTLY_FOR_PROFESSIONAL";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PracticeLocationProfessionalReviewDao practiceLocationProfessionalReviewDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @Transactional(readOnly = true)
    @NotificationDefinition(value = PRACTICE_OWNER_WOULD_HIRE_PROFESSIONAL_PERMANENTLY_FOR_PROFESSIONAL,
            predefined = {
                    ProfessionalNameVariables.class,
                    PracticeLocationVariables.class,
                    AdminVariables.class
            })
    public void onEvent(LocationHasReviewedProfessionalEvent event, long sequence, boolean endOfBatch) {
        PracticeLocationProfessionalReview review = practiceLocationProfessionalReviewDao.findOne(event.getId());
        if (isNull(review)) {
            return;
        }

        if (review.isWouldHire()) {
            TemporaryJobPostingApplication jobPostingApplication = review.getApplication();
            Professional professional = jobPostingApplication.getProfessional();
            PracticeLocation location = jobPostingApplication.getJobPosting().getLocation();

            if (professional.isNotificationsEnabled() && !professional.getStatus().equalsIgnoreCase(User.INACTIVE)) {
                Map<String, String> context = Maps.newHashMap();

                Notification notification = new Notification();
                notification.setEmail(professional.getUsername());
                notification.setPhone(professional.getContact().getPhone());
                notification.setType(PRACTICE_OWNER_WOULD_HIRE_PROFESSIONAL_PERMANENTLY_FOR_PROFESSIONAL);

                professionalNameVariables.supply(professional, context);
                practiceLocationVariables.supply(location, context);
                adminVariables.supply(null, context);

                notification.setContext(context);
                notificationService.send(notification);
            }
        }
    }
}
