package com.cl.mdd.server.core.event.impl.bus;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.SignUpCompletedEventHandler;
import com.cl.mdd.server.core.event.type.SignUpCompletedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class SignUpCompletedEventBus extends DisruptorEventBus<SignUpCompletedEvent> {

    private static final int CONSUMER_STACK_SIZE = 2;

    @Autowired
    private SignUpCompletedEventHandler signUpCompletedEventHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<SignUpCompletedEvent> initialize() {
        Disruptor<SignUpCompletedEvent> disruptor = new Disruptor<>(SignUpCompletedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(signUpCompletedEventHandler).then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(SignUpCompletedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getUserId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
