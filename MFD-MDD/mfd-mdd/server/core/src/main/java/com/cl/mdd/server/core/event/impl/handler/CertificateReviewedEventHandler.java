package com.cl.mdd.server.core.event.impl.handler;

import com.cl.mdd.server.core.data.persistent.access.user.CertificateDetailsDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.CertificateReviewedEvent;
import com.cl.mdd.server.core.service.notification.*;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.copyOf;
import static java.util.Objects.isNull;

@Consumer
public class CertificateReviewedEventHandler implements EventHandler<CertificateReviewedEvent> {

    private static final String CERTIFICATE_APPROVED_FOR_PROFESSIONAL = "CERTIFICATE_APPROVED_FOR_PROFESSIONAL";
    private static final String CERTIFICATE_REJECTED_FOR_PROFESSIONAL = "CERTIFICATE_REJECTED_FOR_PROFESSIONAL";

    private static final Map<String, String> CERTIFICATE_REVIEW_STATUSES_TO_NOTIFICATION_TYPE = ImmutableMap.of(
            CertificateDetails.APPROVED, CERTIFICATE_APPROVED_FOR_PROFESSIONAL,
            CertificateDetails.REJECTED, CERTIFICATE_REJECTED_FOR_PROFESSIONAL
    );

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CertificateDetailsDao certificateDetailsDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private CertificateDetailsVariables certificateDetailsVariables;

    @Autowired
    private AdminVariables adminVariables;

    @Override
    @NotificationDefinition(value = CERTIFICATE_APPROVED_FOR_PROFESSIONAL,
            predefined = {
                    ProfessionalNameVariables.class,
                    CertificateDetailsVariables.class,
                    AdminVariables.class
            })
    @NotificationDefinition(value = CERTIFICATE_REJECTED_FOR_PROFESSIONAL,
            predefined = {
                    ProfessionalNameVariables.class,
                    CertificateDetailsVariables.class,
                    AdminVariables.class
            })
    public void onEvent(CertificateReviewedEvent event, long sequence, boolean endOfBatch) {
        CertificateDetails certificateDetails = certificateDetailsDao.findOne(event.getCertificateDetailsId());
        if (isNull(certificateDetails)) {
            return;
        }

        Professional professional = certificateDetails.getProfessional();
        String certificateDetailsStatus = certificateDetails.getStatus();

        if (CERTIFICATE_REVIEW_STATUSES_TO_NOTIFICATION_TYPE.containsKey(certificateDetailsStatus)) {
            String notificationType = CERTIFICATE_REVIEW_STATUSES_TO_NOTIFICATION_TYPE.get(certificateDetailsStatus);

            Notification notification = new Notification();
            notification.setEmail(professional.getUsername());
            notification.setPhone(professional.getContact().getPhone());
            notification.setType(notificationType);

            Map<String, String> context = Maps.newHashMap();

            professionalNameVariables.supply(professional, context);
            certificateDetailsVariables.supply(certificateDetails, context);
            adminVariables.supply(null, context);

            notification.setContext(copyOf(context));
            notificationService.send(notification);
        }
    }
}
