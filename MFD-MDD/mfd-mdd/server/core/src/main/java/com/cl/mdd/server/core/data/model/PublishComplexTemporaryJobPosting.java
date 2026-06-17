package com.cl.mdd.server.core.data.model;

import javax.validation.Valid;
import java.util.List;

public class PublishComplexTemporaryJobPosting extends PublishTemporaryJobPosting {

    @Valid
    private List<JobDayModel> jobDays;

    public List<JobDayModel> getJobDays() {
        return jobDays;
    }

    public void setJobDays(List<JobDayModel> jobDays) {
        this.jobDays = jobDays;
    }
}
