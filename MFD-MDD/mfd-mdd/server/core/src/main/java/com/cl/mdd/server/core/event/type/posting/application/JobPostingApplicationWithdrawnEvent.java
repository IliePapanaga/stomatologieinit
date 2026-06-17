package com.cl.mdd.server.core.event.type.posting.application;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.event.Event;

public class JobPostingApplicationWithdrawnEvent extends Event {

    private JobPostingApplication jobPostingApplication;

    public JobPostingApplication getJobPostingApplication() {
        return jobPostingApplication;
    }

    public JobPostingApplicationWithdrawnEvent setJobPostingApplication(JobPostingApplication jobPostingApplication) {
        this.jobPostingApplication = jobPostingApplication;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.jobPostingApplication = null;
    }
}