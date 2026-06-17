package com.cl.mdd.server.core.event.type;

import com.cl.mdd.server.core.event.Event;

public class SignUpCompletedEvent extends Event {

    private String userId;

    public String getUserId() {
        return userId;
    }

    public SignUpCompletedEvent setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.userId = null;
    }
}
