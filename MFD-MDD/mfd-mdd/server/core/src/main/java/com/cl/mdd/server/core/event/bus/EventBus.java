package com.cl.mdd.server.core.event.bus;

import com.cl.mdd.server.core.event.Event;
import com.cl.mdd.server.core.event.EventFiller;

public interface EventBus<T extends Event> {

    /**
     * @param eventFiller
     */
    void publishEvent(EventFiller<T> eventFiller);

}
