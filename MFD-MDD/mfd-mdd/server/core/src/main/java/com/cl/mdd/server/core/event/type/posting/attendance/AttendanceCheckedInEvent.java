package com.cl.mdd.server.core.event.type.posting.attendance;

import com.cl.mdd.server.core.event.Event;

public class AttendanceCheckedInEvent extends Event {

    private String checkInId;

    public String getCheckInId() {
        return checkInId;
    }

    public AttendanceCheckedInEvent setCheckInId(String checkInId) {
        this.checkInId = checkInId;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.checkInId = null;
    }
}
