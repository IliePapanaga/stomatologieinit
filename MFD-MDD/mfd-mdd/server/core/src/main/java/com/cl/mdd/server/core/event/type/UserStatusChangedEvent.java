package com.cl.mdd.server.core.event.type;

import com.cl.mdd.server.core.event.Event;

public class UserStatusChangedEvent extends Event {

    private String userId;

    private String status;

    public String getUserId() {
        return userId;
    }

    public UserStatusChangedEvent setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void clear() {
        super.clear();
        this.userId = null;
        this.status = null;
    }
}
