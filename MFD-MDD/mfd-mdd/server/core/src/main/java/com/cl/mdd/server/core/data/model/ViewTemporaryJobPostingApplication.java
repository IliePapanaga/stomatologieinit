package com.cl.mdd.server.core.data.model;

import com.google.common.collect.Lists;

import java.util.List;

public class ViewTemporaryJobPostingApplication implements JobPostingApplication {

    private String id;

    private List<JobDayModel> jobDays;

    private List<ZonedJobDayModel> zonedJobDays = Lists.newArrayList();

    public String getId() {
        return id;
    }

    public ViewTemporaryJobPostingApplication setId(String id) {
        this.id = id;
        return this;
    }

    public List<JobDayModel> getJobDays() {
        return jobDays;
    }

    public ViewTemporaryJobPostingApplication setJobDays(List<JobDayModel> jobDays) {
        this.jobDays = jobDays;
        return this;
    }

    public List<ZonedJobDayModel> getZonedJobDays() {
        return zonedJobDays;
    }

    public ViewTemporaryJobPostingApplication setZonedJobDays(List<ZonedJobDayModel> zonedJobDays) {
        this.zonedJobDays = zonedJobDays;
        return this;
    }
}
