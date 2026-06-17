package com.cl.mdd.server.core.event.type.posting.interview;

import com.cl.mdd.server.core.event.Event;

public class JobInterviewScheduledRepeatedlyEvent extends Event {

    private String interviewId;

    private int index;

    public String getInterviewId() {
        return interviewId;
    }

    public JobInterviewScheduledRepeatedlyEvent setInterviewId(String interviewId) {
        this.interviewId = interviewId;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public JobInterviewScheduledRepeatedlyEvent setIndex(int index) {
        this.index = index;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.interviewId = null;
    }
}
