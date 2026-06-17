package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.posting.JobInterviewDao;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import com.cl.mdd.server.core.security.authorization.annotation.AccessAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.function.Supplier;

@AccessAuthorizer
public class JobInterviewAccessAuthorizer extends AbstractEntityAccessAuthorizer<JobPostingApplication> {

    @Autowired
    private JobInterviewDao jobInterviewDao;

    @Override
    public boolean readAllowed(String id) {
        return checkAgainstProfessional(id, () -> jobInterviewDao.findOne(id)) || checkAgainstPracticeOwner(id);
    }

    @Override
    public boolean updateAllowed(String id) {
        return false;
    }

    @Override
    public boolean deleteAllowed(String id) {
        return checkAgainstProfessional(id, () -> jobInterviewDao.findOne(id));
    }

    public boolean acceptAllowed(String optionId) {
        return checkAgainstProfessional(optionId, () -> jobInterviewDao.findOneByOptionId(optionId));
    }

    public boolean cancelAllowed(String id) {
        return checkAgainstPracticeOwner(id);
    }

    public boolean rejectAllowed(String id) {
        return checkAgainstProfessional(id, () -> jobInterviewDao.findOne(id));
    }

    @Override
    public boolean usageAllowed(String id) {
        return false;
    }

    private boolean checkAgainstProfessional(String id, Supplier<JobInterview> jobInterviewSupplier) {
        if (Objects.isNull(id) || securityAccess.isCurrentSystemUser()) {
            return true;
        } else {
            final String currentUserId = securityAccess.currentUserId();
            JobInterview jobInterview = jobInterviewSupplier.get();
            return jobInterview == null || currentUserId.equals(jobInterview.getApplication().getProfessional().getId());
        }
    }

    private boolean checkAgainstPracticeOwner(String id) {
        if (Objects.isNull(id) || securityAccess.isCurrentSystemUser()) {
            return true;
        } else {
            final String currentUserId = securityAccess.currentUserId();
            JobInterview jobInterview = jobInterviewDao.findOne(id);
            return jobInterview == null || currentUserId.equals(jobInterview.getApplication().getJobPosting().getLocation().getPractice().getOwner().getId());
        }
    }

}
