package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.NoShowAllowed;

import javax.validation.constraints.NotNull;

public class AddNoShowModel {

    @NotNull
    @NoShowAllowed
    private String jobDayId;

    public AddNoShowModel() {
    }

    public AddNoShowModel(String jobDayId) {
        this.jobDayId = jobDayId;
    }

    public String getJobDayId() {
        return jobDayId;
    }

    public AddNoShowModel setJobDayId(String jobDayId) {
        this.jobDayId = jobDayId;
        return this;
    }
}
