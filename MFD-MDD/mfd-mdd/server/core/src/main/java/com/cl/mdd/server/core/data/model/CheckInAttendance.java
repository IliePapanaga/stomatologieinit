package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.CheckInAllowed;

import javax.validation.constraints.NotNull;

public class CheckInAttendance {

    @NotNull
    @CheckInAllowed
    private String jobDayId;

    public CheckInAttendance() {
    }

    public CheckInAttendance(String jobDayId) {
        this.jobDayId = jobDayId;
    }

    public String getJobDayId() {
        return jobDayId;
    }

    public CheckInAttendance setJobDayId(String jobDayId) {
        this.jobDayId = jobDayId;
        return this;
    }
}
