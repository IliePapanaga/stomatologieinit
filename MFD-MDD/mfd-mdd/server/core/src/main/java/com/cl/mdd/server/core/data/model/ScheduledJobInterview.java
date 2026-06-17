package com.cl.mdd.server.core.data.model;

import java.util.List;

public class ScheduledJobInterview extends MDDModel {

    private String id;

    private boolean working;

    private String comments;

    private List<JobInterviewScheduledOption> options;

    private JobInterviewScheduledOption acceptedOption;

    public String getId() {
        return id;
    }

    public ScheduledJobInterview setId(String id) {
        this.id = id;
        return this;
    }

    public boolean isWorking() {
        return working;
    }

    public ScheduledJobInterview setWorking(boolean working) {
        this.working = working;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public ScheduledJobInterview setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public List<JobInterviewScheduledOption> getOptions() {
        return options;
    }

    public ScheduledJobInterview setOptions(List<JobInterviewScheduledOption> options) {
        this.options = options;
        return this;
    }

    public ScheduledJobInterview setAcceptedOption(JobInterviewScheduledOption acceptedOpption) {
        this.acceptedOption = acceptedOpption;
        return this;
    }

    public JobInterviewScheduledOption getAcceptedOption() {
        return acceptedOption;
    }
}
