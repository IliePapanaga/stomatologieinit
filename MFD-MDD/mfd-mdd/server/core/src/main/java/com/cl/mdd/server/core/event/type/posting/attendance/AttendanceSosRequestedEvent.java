package com.cl.mdd.server.core.event.type.posting.attendance;

import com.cl.mdd.server.core.event.Event;

public class AttendanceSosRequestedEvent extends Event {

    private String sosRequestId;

    public String getSosRequestId() {
        return sosRequestId;
    }

    public AttendanceSosRequestedEvent setSosRequestId(String sosRequestId) {
        this.sosRequestId = sosRequestId;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.sosRequestId = null;
    }
}
