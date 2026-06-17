package com.cl.mdd.server.core.event.type.posting.application;

import com.cl.mdd.server.core.event.Event;

public class JobPostingApplicationRejectedEvent extends Event {

    private String jobPostingApplicationId;

    public String getJobPostingApplicationId() {
        return jobPostingApplicationId;
    }

    public JobPostingApplicationRejectedEvent setJobPostingApplicationId(String jobPostingApplicationId) {
        this.jobPostingApplicationId = jobPostingApplicationId;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.jobPostingApplicationId = null;
    }
}
