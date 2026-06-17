package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.user.ProfessionalDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.security.authorization.annotation.AccessAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@AccessAuthorizer
public class ProfessionalAccessAuthorizer extends AbstractEntityAccessAuthorizer<Professional> {

    @Autowired
    private ProfessionalDao professionalDao;

    public boolean readProfileAllowed(String id) {
        return true;
    }

    @Override
    public boolean readAllowed(String id) {
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

    @Override
    public boolean usageAllowed(String id) {
        return check(id);
    }

    private boolean check(String id) {
        if (Objects.isNull(id) || securityAccess.isCurrentSystemUser()) {
            return true;
        } else {
            final String currentUserId = securityAccess.currentUserId();
            Professional professional = professionalDao.findOne(id);
            return professional == null || currentUserId.equals(professional.getId());
        }
    }
}
