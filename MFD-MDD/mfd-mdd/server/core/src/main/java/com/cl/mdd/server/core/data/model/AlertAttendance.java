package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.AttendanceAlertAllowed;

import javax.validation.constraints.NotNull;

public class AlertAttendance {

    @NotNull
    @AttendanceAlertAllowed
    private String jobDayId;

    public AlertAttendance() {
    }

    public AlertAttendance(String jobDayId) {
        this.jobDayId = jobDayId;
    }

    public String getJobDayId() {
        return jobDayId;
    }

    public AlertAttendance setJobDayId(String jobDayId) {
        this.jobDayId = jobDayId;
        return this;
    }
}
