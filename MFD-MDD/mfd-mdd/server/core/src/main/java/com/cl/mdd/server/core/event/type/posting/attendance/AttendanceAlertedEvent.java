package com.cl.mdd.server.core.event.type.posting.attendance;

import com.cl.mdd.server.core.event.Event;

public class AttendanceAlertedEvent extends Event {

    private String alertId;

    public String getAlertId() {
        return alertId;
    }

    public AttendanceAlertedEvent setAlertId(String alertId) {
        this.alertId = alertId;
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.alertId = null;
    }
}
