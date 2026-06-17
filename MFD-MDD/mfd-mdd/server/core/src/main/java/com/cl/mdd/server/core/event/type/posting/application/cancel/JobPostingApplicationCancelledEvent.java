package com.cl.mdd.server.core.event.type.posting.application.cancel;

import com.cl.mdd.server.core.event.Event;

public class JobPostingApplicationCancelledEvent extends Event {

    private String jobPostingApplicationId;

    public String getJobPostingApplicationId() {
        return jobPostingApplicationId;
    }

    public JobPostingApplicationCancelledEvent setJobPostingApplicationId(String jobPostingApplicationId) {
        this.jobPostingApplicationId = jobPostingApplicationId;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.jobPostingApplicationId = null;
    }
}
