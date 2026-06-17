package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.security.authorization.annotation.AccessAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Objects.isNull;

@AccessAuthorizer
public class JobPostingAccessAuthorizer extends AbstractEntityAccessAuthorizer<PracticeLocation> {

    @Autowired
    private JobPostingDao jobPostingDao;

    public boolean applyAllowed(String id) {
        if (isNull(id)) {
            return true;
        }
        if (securityAccess.isCurrentProfessional()) {
            final String currentUserId = securityAccess.currentUserId();
            JobPosting jobPosting = jobPostingDao.findOne(id);
            return jobPosting == null || jobPosting.getPreferredProfessional() == null || jobPosting.getPreferredProfessional().getId().equals(currentUserId);
        } else {
            return false;
        }
    }

    @Override
    public boolean readAllowed(String id) {
        return true;
    }

    @Override
    public boolean updateAllowed(String id) {
        return check(id);
    }

    @Override
    public boolean deleteAllowed(String id) {
        return securityAccess.isCurrentSystemUser();
    }

    private boolean check(String id) {
        if (isNull(id) || securityAccess.isCurrentSystemUser()) {
            return true;
        } else {
            final String currentUserId = securityAccess.currentUserId();
            JobPosting jobPosting = jobPostingDao.findOne(id);
            return jobPosting == null || currentUserId.equals(jobPosting.getLocation().getPractice().getOwner().getId());
        }
    }
}
