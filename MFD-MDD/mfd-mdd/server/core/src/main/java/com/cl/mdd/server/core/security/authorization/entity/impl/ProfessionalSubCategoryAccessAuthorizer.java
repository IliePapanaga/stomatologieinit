package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.user.ProfessionalDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.security.authorization.annotation.AccessAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@AccessAuthorizer
public class ProfessionalSubCategoryAccessAuthorizer extends AbstractEntityAccessAuthorizer<Professional> {

    @Autowired
    private ProfessionalDao professionalDao;

    @Override
    public boolean readAllowed(String professionalId) {
        if (Objects.isNull(professionalId)
                || securityAccess.isCurrentSystemUser()
                || securityAccess.isCurrentPracticeOwner()) {
            return true;
        } else {
            final String currentUserId = securityAccess.currentUserId();
            Professional professional = professionalDao.findOne(professionalId);
            return professional == null || currentUserId.equals(professional.getId());
        }
    }

}
