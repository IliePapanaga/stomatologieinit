package com.cl.mdd.server.core.event.type.posting.attendance;

import com.cl.mdd.server.core.event.Event;

public class AttendanceAlertRepliedEvent extends Event {

    private String attendanceAlertId;

    public String getAttendanceAlertId() {
        return attendanceAlertId;
    }

    public AttendanceAlertRepliedEvent setAttendanceAlertId(String attendanceAlertId) {
        this.attendanceAlertId = attendanceAlertId;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.attendanceAlertId = null;
    }
}
