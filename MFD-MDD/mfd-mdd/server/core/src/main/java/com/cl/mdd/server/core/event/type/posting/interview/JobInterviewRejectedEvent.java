package com.cl.mdd.server.core.event.type.posting.interview;

import com.cl.mdd.server.core.event.Event;

public class JobInterviewRejectedEvent extends Event {

    private String interviewId;

    public String getInterviewId() {
        return interviewId;
    }

    public JobInterviewRejectedEvent setInterviewId(String interviewId) {
        this.interviewId = interviewId;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.interviewId = null;
    }
}
