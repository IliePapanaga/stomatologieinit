package com.cl.mdd.server.core.data.model;

import javax.validation.constraints.NotNull;

public class ApplicationForPermanentJob extends MDDModel {

    @NotNull(message = "{job.posting.id.not.null}")
    private String jobPostingId;

    public String getJobPostingId() {
        return jobPostingId;
    }

    public void setJobPostingId(String jobPostingId) {
        this.jobPostingId = jobPostingId;
    }
}
