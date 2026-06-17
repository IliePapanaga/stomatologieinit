package com.cl.mdd.server.core.event.type.posting;

import com.cl.mdd.server.core.event.Event;

public class JobPostingCancelledEvent extends Event {

    private String jobPostingId;

    public String getJobPostingId() {
        return jobPostingId;
    }

    public JobPostingCancelledEvent setJobPostingId(String jobPostingId) {
        this.jobPostingId = jobPostingId;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.jobPostingId = null;
    }
}
