package com.cl.mdd.server.core.event;

import com.cl.mdd.server.core.event.annotation.EventBus;

import java.time.ZonedDateTime;

/**
 * Root Class for Event Hierarchy
 */
public abstract class Event {

    private ZonedDateTime fireDate;

    /**
     * @return {@link ZonedDateTime} which indicates the time when the event was fired
     * The value usually is populated by {@link EventBus} right before event fire
     */
    public ZonedDateTime getFireDate() {
        return fireDate;
    }

    /**
     * This generally should be invoked internally by {@link EventBus}
     *
     * @param fireDate
     */
    public void setFireDate(ZonedDateTime fireDate) {
        this.fireDate = fireDate;
    }

    /**
     * This generally should be invoked internally by cleanup {@link com.cl.mdd.server.core.event.annotation.Consumer}
     */
    public void clear() {
        this.fireDate = null;
    }
}
