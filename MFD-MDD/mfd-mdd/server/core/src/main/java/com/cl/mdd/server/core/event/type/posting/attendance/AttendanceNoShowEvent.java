package com.cl.mdd.server.core.event.type.posting.attendance;

import com.cl.mdd.server.core.event.Event;

public class AttendanceNoShowEvent extends Event {

    private String noShowId;

    public String getNoShowId() {
        return noShowId;
    }

    public AttendanceNoShowEvent setNoShowId(String noShowId) {
        this.noShowId = noShowId;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.noShowId = null;
    }
}
