package com.cl.mdd.server.core.data.model;

import javax.validation.constraints.NotNull;

public class DismissSos {

    @NotNull
    private String jobPostingId;

    public DismissSos() {
    }

    public DismissSos(String jobPostingId) {
        this.jobPostingId = jobPostingId;
    }

    public String getJobPostingId() {
        return jobPostingId;
    }

    public DismissSos setJobPostingId(String jobPostingId) {
        this.jobPostingId = jobPostingId;
        return this;
    }
}
