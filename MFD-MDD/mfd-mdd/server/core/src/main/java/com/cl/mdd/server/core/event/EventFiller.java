package com.cl.mdd.server.core.event;

@FunctionalInterface
public interface EventFiller<T extends Event> {

    /**
     * Lambda which populates the {@link Event} fields.
     */
    void fillEvent(T event);

}
