package com.cl.mdd.server.core.data.model;

import java.time.ZonedDateTime;

public class JobInterviewScheduledOption extends MDDModel {

    private String id;

    private ZonedDateTime dateTime;

    public String getId() {
        return id;
    }

    public JobInterviewScheduledOption setId(String id) {
        this.id = id;
        return this;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public JobInterviewScheduledOption setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }
}
