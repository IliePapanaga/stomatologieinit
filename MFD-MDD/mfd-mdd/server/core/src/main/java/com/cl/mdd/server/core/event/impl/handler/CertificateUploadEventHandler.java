package com.cl.mdd.server.core.event.impl.handler;

import com.cl.mdd.server.core.data.persistent.access.user.CertificateDetailsDao;
import com.cl.mdd.server.core.data.persistent.access.user.SystemUserDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.CertificateUploadEvent;
import com.cl.mdd.server.core.service.notification.CertificateDetailsVariables;
import com.cl.mdd.server.core.service.notification.Notification;
import com.cl.mdd.server.core.service.notification.NotificationService;
import com.cl.mdd.server.core.service.notification.ProfessionalNameVariables;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.copyOf;
import static java.util.Objects.isNull;

@Consumer
public class CertificateUploadEventHandler implements EventHandler<CertificateUploadEvent> {

    private static final String PROFESSIONAL_UPLOAD_CERTIFICATE_FOR_SYSTEM_USER = "PROFESSIONAL_UPLOAD_CERTIFICATE_FOR_SYSTEM_USER";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CertificateDetailsDao certificateDetailsDao;

    @Autowired
    private ProfessionalNameVariables professionalNameVariables;

    @Autowired
    private CertificateDetailsVariables certificateDetailsVariables;

    @Autowired
    private SystemUserDao systemUserDao;

    @Override
    @Transactional
    @NotificationDefinition(value = PROFESSIONAL_UPLOAD_CERTIFICATE_FOR_SYSTEM_USER,
            predefined = {
                    ProfessionalNameVariables.class,
                    CertificateDetailsVariables.class
            })
    public void onEvent(CertificateUploadEvent event, long sequence, boolean endOfBatch) {
        CertificateDetails certificateDetails = certificateDetailsDao.findOne(event.getCertificateDetailsId());
        if (isNull(certificateDetails)) {
            return;
        }

        Professional professional = certificateDetails.getProfessional();

        systemUserDao.findAll().stream().filter(user -> !user.getStatus().equals(User.INACTIVE)).forEach(systemUser -> {
            Notification notification = new Notification();
            notification.setEmail(systemUser.getUsername());
            notification.setPhone(systemUser.getContact().getPhone());
            notification.setType(PROFESSIONAL_UPLOAD_CERTIFICATE_FOR_SYSTEM_USER);

            Map<String, String> context = Maps.newHashMap();

            professionalNameVariables.supply(professional, context);
            certificateDetailsVariables.supply(certificateDetails, context);

            notification.setContext(copyOf(context));
            notificationService.send(notification);
        });
    }
}
