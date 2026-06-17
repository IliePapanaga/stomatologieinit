package com.cl.mdd.server.core.event.impl.handler.posting.application.review;

import com.cl.mdd.server.core.data.persistent.access.posting.ProfessionalPracticeLocationReviewDao;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.review.ProfessionalPracticeLocationReview;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.User;
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
public class NotifySystemAboutProfessionalWouldWorkPermanentlyEventHandler implements EventHandler<ProfessionalHasReviewedLocationEvent> {

    private static final String PROFESSIONAL_WOULD_WORK_PERMANENTLY_FOR_SYSTEM = "PROFESSIONAL_WOULD_WORK_PERMANENTLY_FOR_SYSTEM";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProfessionalPracticeLocationReviewDao professionalPracticeLocationReviewDao;

    @Autowired
    private SystemUserDao systemUserDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private PracticeLocationVariables practiceLocationVariables;

    @Autowired
    private PracticeOwnerVariables practiceOwnerVariables;

    @Override
    @Transactional(readOnly = true)
    @NotificationDefinition(value = PROFESSIONAL_WOULD_WORK_PERMANENTLY_FOR_SYSTEM,
            predefined = {
                    ProfessionalNameVariables.class,
                    PracticeLocationVariables.class,
                    PracticeOwnerVariables.class
            })
    public void onEvent(ProfessionalHasReviewedLocationEvent event, long sequence, boolean endOfBatch) {
        ProfessionalPracticeLocationReview review = professionalPracticeLocationReviewDao.findOne(event.getId());
        if (isNull(review)) {
            return;
        }

        if (review.isWouldWorkPermanently()) {
            TemporaryJobPostingApplication jobPostingApplication = review.getApplication();
            Professional professional = jobPostingApplication.getProfessional();
            PracticeLocation location = jobPostingApplication.getJobPosting().getLocation();
            PracticeOwner practiceOwner = location.getPractice().getOwner();

            systemUserDao.findAll().stream().filter(user -> !user.getStatus().equals(User.INACTIVE)).forEach(systemUser -> {
                Map<String, String> context = Maps.newHashMap();

                Notification notification = new Notification();
                notification.setEmail(systemUser.getUsername());
                notification.setPhone(systemUser.getContact().getPhone());
                notification.setType(PROFESSIONAL_WOULD_WORK_PERMANENTLY_FOR_SYSTEM);

                professionalNameVariables.supply(professional, context);
                practiceLocationVariables.supply(location, context);
                practiceOwnerVariables.supply(practiceOwner, context);

                notification.setContext(context);
                notificationService.send(notification);
            });
        }
    }
}
