package com.cl.mdd.server.core.event.impl.bus.posting.application;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.application.JobPostingApplicationCreatedHandler;
import com.cl.mdd.server.core.event.type.posting.application.JobPostingApplicationCreatedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class JobPostingApplicationCreatedEventBus extends DisruptorEventBus<JobPostingApplicationCreatedEvent> {

    private static final int CONSUMER_STACK_SIZE = 2;

    @Autowired
    private JobPostingApplicationCreatedHandler handler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<JobPostingApplicationCreatedEvent> initialize() {
        Disruptor<JobPostingApplicationCreatedEvent> disruptor = new Disruptor<>(JobPostingApplicationCreatedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(handler).then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(JobPostingApplicationCreatedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getJobPostingApplicationId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
