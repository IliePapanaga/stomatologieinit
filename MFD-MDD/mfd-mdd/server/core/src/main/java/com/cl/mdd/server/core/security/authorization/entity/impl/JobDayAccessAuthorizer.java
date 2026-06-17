package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.posting.JobDayDao;
import com.cl.mdd.server.core.data.persistent.access.posting.TemporaryJobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.security.authorization.annotation.AccessAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@AccessAuthorizer
public class JobDayAccessAuthorizer extends AbstractEntityAccessAuthorizer<JobDay> {

    @Autowired
    private JobDayDao jobDayDao;

    @Autowired
    private TemporaryJobPostingApplicationDao temporaryJobPostingApplicationDao;

    @Override
    public boolean readAllowed(String id) {
        return checkPracticeOwner(id);
    }

    @Override
    public boolean updateAllowed(String id) {
        return checkPracticeOwner(id);
    }

    public boolean checkInAllowed(String id) {
        return checkPracticeOwner(id) || checkProfessional(id);
    }

    @Override
    public boolean deleteAllowed(String id) {
        return securityAccess.isCurrentSystemUser();
    }

    private boolean checkPracticeOwner(String id) {
        if (Objects.isNull(id) || securityAccess.isCurrentSystemUser()) {
            return true;
        } else {
            final String currentUserId = securityAccess.currentUserId();
            JobDay jobDay = jobDayDao.findOne(id);
            return jobDay == null || currentUserId.equals(jobDay.getJobPosting().getLocation().getPractice().getOwner().getId());
        }
    }

    private boolean checkProfessional(String id) {
        if (Objects.isNull(id) || securityAccess.isCurrentSystemUser()) {
            return true;
        } else {
            final String currentUserId = securityAccess.currentUserId();
            TemporaryJobPostingApplication application = temporaryJobPostingApplicationDao.findOneByAttendanceId(id);
            return application == null || currentUserId.equals(application.getProfessional().getId());
        }
    }
}
