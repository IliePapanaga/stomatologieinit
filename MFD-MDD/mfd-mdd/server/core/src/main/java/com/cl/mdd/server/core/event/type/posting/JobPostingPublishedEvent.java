package com.cl.mdd.server.core.event.type.posting;

import com.cl.mdd.server.core.event.Event;

public class JobPostingPublishedEvent extends Event {

    private String jobPostingId;

    public String getJobPostingId() {
        return jobPostingId;
    }

    public JobPostingPublishedEvent setJobPostingId(String jobPostingId) {
        this.jobPostingId = jobPostingId;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.jobPostingId = null;
    }
}
