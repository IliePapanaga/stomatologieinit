package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.practice.PracticeLocationDao;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.security.authorization.annotation.AccessAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@AccessAuthorizer
public class PracticeLocationAccessAuthorizer extends AbstractEntityAccessAuthorizer<PracticeLocation> {

    @Autowired
    private PracticeLocationDao practiceLocationDao;

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

    public boolean publishJobPostingAllowed(String id) {
        return check(id);
    }

    private boolean check(String id) {
        if (Objects.isNull(id) || securityAccess.isCurrentSystemUser()) {
            return true;
        } else {
            final String currentUserId = securityAccess.currentUserId();
            PracticeLocation practiceLocation = practiceLocationDao.findOne(id);
            return practiceLocation == null || currentUserId.equals(practiceLocation.getPractice().getOwner().getId());
        }
    }
}
