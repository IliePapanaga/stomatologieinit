package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.user.CertificateDetailsDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import com.cl.mdd.server.core.security.authorization.annotation.AccessAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@AccessAuthorizer
public class ProfessionalCertificateAccessAuthorizer extends AbstractEntityAccessAuthorizer {

    @Autowired
    private CertificateDetailsDao certificateDetailsDao;

    @Override
    public boolean readAllowed(String id) {
        return check(id);

    }
    @Override
    public boolean usageAllowed(String id) {
        return check(id);
    }

    @Override
    public boolean updateAllowed(String id) {
        return check(id);
    }

    @Override
    public boolean deleteAllowed(String id) {
        return check(id);
    }

    protected boolean check(String id) {
        if (Objects.isNull(id) || securityAccess.isCurrentSystemUser()) {
            return true;
        } else {
            final String currentUserId = securityAccess.currentUserId();
            CertificateDetails certificateDetails = certificateDetailsDao.findOneByCertificateId(id);
            return certificateDetails == null || certificateDetails.getProfessional().getId().equals(currentUserId);
        }
    }
}
