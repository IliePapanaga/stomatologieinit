package com.cl.mdd.server.core.event.type.posting.attendance;

import com.cl.mdd.server.core.event.Event;

public class WorkStartSoonEvent extends Event {

    private String jobDayId;

    public String getJobDayId() {
        return jobDayId;
    }

    public WorkStartSoonEvent setJobDayId(String jobDayId) {
        this.jobDayId = jobDayId;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.jobDayId = null;
    }
}
