package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.practice.PracticeDao;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.security.authorization.annotation.AccessAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@AccessAuthorizer
public class PracticeAccessAuthorizer extends AbstractEntityAccessAuthorizer<PracticeLocation> {

    @Autowired
    private PracticeDao practiceDao;

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

    private boolean check(String id) {
        if (Objects.isNull(id) || securityAccess.isCurrentSystemUser()) {
            return true;
        } else {
            final String currentUserId = securityAccess.currentUserId();
            Practice practice = practiceDao.findOne(id);
            return practice == null || currentUserId.equals(practice.getOwner().getId());
        }
    }
}
