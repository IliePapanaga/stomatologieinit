package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.user.PracticeOwnerDao;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.security.authorization.annotation.AccessAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@AccessAuthorizer
public class PracticeOwnerAccessAuthorizer extends AbstractEntityAccessAuthorizer<PracticeOwner> {

    @Autowired
    private PracticeOwnerDao practiceOwnerDao;

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
            PracticeOwner practiceOwner = practiceOwnerDao.findOne(id);
            return practiceOwner == null || currentUserId.equals(practiceOwner.getId());
        }
    }
}
