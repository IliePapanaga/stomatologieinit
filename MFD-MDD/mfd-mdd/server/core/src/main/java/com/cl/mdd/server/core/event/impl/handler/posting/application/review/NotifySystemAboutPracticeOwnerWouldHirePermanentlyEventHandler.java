package com.cl.mdd.server.core.event.impl.handler.posting.application.review;

import com.cl.mdd.server.core.data.persistent.access.posting.PracticeLocationProfessionalReviewDao;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.review.PracticeLocationProfessionalReview;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
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
public class NotifySystemAboutPracticeOwnerWouldHirePermanentlyEventHandler implements EventHandler<LocationHasReviewedProfessionalEvent> {

    private static final String PRACTICE_OWNER_WOULD_HIRE_PROFESSIONAL_PERMANENTLY_FOR_SYSTEM = "PRACTICE_OWNER_WOULD_HIRE_PROFESSIONAL_PERMANENTLY_FOR_SYSTEM";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PracticeLocationProfessionalReviewDao practiceLocationProfessionalReviewDao;

    @Autowired
    private SystemUserDao systemUserDao;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;


    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Override
    @Transactional(readOnly = true)
    @NotificationDefinition(value = PRACTICE_OWNER_WOULD_HIRE_PROFESSIONAL_PERMANENTLY_FOR_SYSTEM,
            predefined = {
                    PracticeOwnerVariables.class,
                    ProfessionalNameVariables.class,
                    PracticeLocationVariables.class
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
            PracticeOwner practiceOwner = location.getPractice().getOwner();

            systemUserDao.findAll().stream().filter(user -> !user.getStatus().equals(User.INACTIVE)).forEach(systemUser -> {
                Map<String, String> context = Maps.newHashMap();

                Notification notification = new Notification();
                notification.setEmail(systemUser.getUsername());
                notification.setPhone(systemUser.getContact().getPhone());
                notification.setType(PRACTICE_OWNER_WOULD_HIRE_PROFESSIONAL_PERMANENTLY_FOR_SYSTEM);

                practiceOwnerVariables.supply(practiceOwner, context);
                professionalNameVariables.supply(professional, context);
                practiceLocationVariables.supply(location, context);

                notification.setContext(context);
                notificationService.send(notification);
            });
        }
    }
}
