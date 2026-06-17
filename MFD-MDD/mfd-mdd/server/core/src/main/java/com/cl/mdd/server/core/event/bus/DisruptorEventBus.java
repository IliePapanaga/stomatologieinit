package com.cl.mdd.server.core.event.bus;

import com.cl.mdd.server.core.event.Event;
import com.cl.mdd.server.core.event.EventCleanupHandler;
import com.cl.mdd.server.core.event.EventFiller;
import com.cl.mdd.server.core.event.impl.Disruptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Disruptor based {@link EventBus}.
 * <p>
 * General contract is to extend this class and provide implementation for {@link #initialize()} and {@link #valid(Event)} methods.
 */
public abstract class DisruptorEventBus<T extends Event> implements EventBus<T> {

    /**
     * Event clean up handler.
     * <p>
     * Sets the event fields to null in order to make their values GC eligible.
     * <p>
     * It may be optionally assigned as the <b>last</b> handler of the returned disruptor from {@link #initialize()}
     * <p>
     * https://github.com/LMAX-Exchange/disruptor/wiki/Getting-Started#clearing-objects-from-the-ring-buffer
     */
    @Autowired
    protected EventCleanupHandler eventCleanupHandler;

    private Disruptor<T> disruptor;

    @Override
    public void publishEvent(EventFiller<T> eventFiller) {
        disruptor.publishEvent((T event, long sequence) -> {
            eventFiller.fillEvent(event);
            event.setFireDate(ZonedDateTime.now());
            valid(event);
        });
    }

    /**
     * Initializes, configures and returns an implementation specific {@link Disruptor}
     * TODO: provide a default implementation
     */
    protected abstract Disruptor<T> initialize();

    /**
     * Validates that {@link Event} is valid to be published
     *
     * @throws RuntimeException with implementation specific details
     */
    protected abstract void valid(T event);

    @Order
    @PostConstruct
    private void start() {
        disruptor = initialize();
        if (Objects.isNull(disruptor)) {
            throw new NullPointerException("Disruptor cannot be null!");
        }
        disruptor.start();
    }

    @PreDestroy
    private void preDestroy() {
        disruptor.shutdown();
        this.shutDown();
    }

    protected abstract void shutDown();


}
