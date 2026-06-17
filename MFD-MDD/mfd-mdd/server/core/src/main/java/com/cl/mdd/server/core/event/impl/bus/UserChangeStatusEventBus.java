package com.cl.mdd.server.core.event.impl.bus;

import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.cl.mdd.server.core.event.impl.handler.UserStatusChangedEventHandler;
import com.cl.mdd.server.core.event.type.UserStatusChangedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class UserChangeStatusEventBus extends DisruptorEventBus<UserStatusChangedEvent> {

    private static final int CONSUMER_STACK_SIZE = 2;

    @Autowired
    private UserStatusChangedEventHandler userStatusChangedEventHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<UserStatusChangedEvent> initialize() {
        Disruptor<UserStatusChangedEvent> disruptor = new Disruptor<>(UserStatusChangedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(userStatusChangedEventHandler).then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(UserStatusChangedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getUserId());
        Objects.requireNonNull(event.getStatus());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
