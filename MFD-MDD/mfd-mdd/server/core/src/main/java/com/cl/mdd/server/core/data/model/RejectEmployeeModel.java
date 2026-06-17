package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.ProfessionalRejectionAllowed;

import javax.validation.constraints.NotNull;

public class RejectEmployeeModel {

    @NotNull
    @ProfessionalRejectionAllowed
    private String jobDayId;

    private String reason;

    public RejectEmployeeModel() {
    }

    public RejectEmployeeModel(String jobDayId) {
        this.jobDayId = jobDayId;
    }

    public String getJobDayId() {
        return jobDayId;
    }

    public void setJobDayId(String jobDayId) {
        this.jobDayId = jobDayId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
