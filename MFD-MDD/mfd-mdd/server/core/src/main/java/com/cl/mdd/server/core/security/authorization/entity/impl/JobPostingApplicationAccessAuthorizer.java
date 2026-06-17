package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.posting.JobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.security.authorization.annotation.AccessAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@AccessAuthorizer
public class JobPostingApplicationAccessAuthorizer extends AbstractEntityAccessAuthorizer<JobPostingApplication> {

    @Autowired
    private JobPostingApplicationDao jobPostingApplicationDao;

    @Override
    public boolean readAllowed(String id) {
        return checkAgainstProfessional(id);
    }

    @Override
    public boolean updateAllowed(String id) {
        return false;
    }

    @Override
    public boolean deleteAllowed(String id) {
        return checkAgainstProfessional(id);
    }

    public boolean acceptAllowed(String id) {
        return checkAgainstProfessional(id);
    }

    public boolean reviewProfessionalAllowed(String id) {
        return checkAgainstPracticeOwner(id);
    }

    public boolean reviewLocationAllowed(String id) {
        return checkAgainstProfessional(id);
    }

    public boolean bookAllowed(String id) {
        return checkAgainstPracticeOwner(id);
    }

    public boolean scheduleInterviewAllowed(String id) {
        return checkAgainstPracticeOwner(id);
    }

    public boolean cancelAllowed(String id) {
        return checkAgainstPracticeOwner(id);
    }

    public boolean rejectAllowed(String id) {
        return checkAgainstProfessional(id);
    }

    @Override
    public boolean usageAllowed(String id) {
        return false;
    }

    private boolean checkAgainstProfessional(String id) {
        if (Objects.isNull(id) || securityAccess.isCurrentSystemUser()) {
            return true;
        } else {
            final String currentUserId = securityAccess.currentUserId();
            JobPostingApplication application = jobPostingApplicationDao.findOne(id);
            return application == null || currentUserId.equals(application.getProfessional().getId());
        }
    }

    private boolean checkAgainstPracticeOwner(String id) {
        if (Objects.isNull(id) || securityAccess.isCurrentSystemUser()) {
            return true;
        } else {
            final String currentUserId = securityAccess.currentUserId();
            JobPostingApplication application = jobPostingApplicationDao.findOne(id);
            return application == null || currentUserId.equals(application.getJobPosting().getLocation().getPractice().getOwner().getId());
        }
    }

}
