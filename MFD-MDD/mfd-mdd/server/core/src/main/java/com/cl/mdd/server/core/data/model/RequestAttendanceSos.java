package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.SosRequestAllowed;

import javax.validation.constraints.NotNull;

public class RequestAttendanceSos {

    @NotNull
    @SosRequestAllowed
    private String jobDayId;

    private boolean noShow;

    public RequestAttendanceSos() {
    }

    public RequestAttendanceSos(String jobDayId) {
        this.jobDayId = jobDayId;
    }

    public String getJobDayId() {
        return jobDayId;
    }

    public RequestAttendanceSos setJobDayId(String jobDayId) {
        this.jobDayId = jobDayId;
        return this;
    }

    public RequestAttendanceSos setNoShow(boolean noShow) {
        this.noShow = noShow;
        return this;
    }

    public boolean isNoShow() {
        return noShow;
    }
}
