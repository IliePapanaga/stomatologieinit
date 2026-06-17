package com.cl.mdd.server.core.event;

import com.cl.mdd.server.core.event.annotation.Consumer;
import com.lmax.disruptor.EventHandler;

/**
 * Cleanup handler
 */
@Consumer
public class EventCleanupHandler<T extends Event> implements EventHandler<T> {

    @Override
    public void onEvent(T event, long sequence, boolean endOfBatch) {
        event.clear();
    }
}
