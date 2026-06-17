package com.cl.mdd.server.core.event.type.posting.interview;

import com.cl.mdd.server.core.event.Event;

public class JobInterviewFinishedEvent extends Event {

    private String interviewId;

    public String getInterviewId() {
        return interviewId;
    }

    public JobInterviewFinishedEvent setInterviewId(String interviewId) {
        this.interviewId = interviewId;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.interviewId = null;
    }
}
