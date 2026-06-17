package com.cl.mdd.server.core.event.impl.bus.posting.application;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.application.JobPostingApplicationWithdrawnEventHandler;
import com.cl.mdd.server.core.event.type.posting.application.JobPostingApplicationWithdrawnEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class JobPostingApplicationWithdrawnEventBus extends DisruptorEventBus<JobPostingApplicationWithdrawnEvent> {

    private static final int CONSUMER_STACK_SIZE = 2;

    @Autowired
    private JobPostingApplicationWithdrawnEventHandler handler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<JobPostingApplicationWithdrawnEvent> initialize() {
        Disruptor<JobPostingApplicationWithdrawnEvent> disruptor = new Disruptor<>(JobPostingApplicationWithdrawnEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(handler).then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(JobPostingApplicationWithdrawnEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getJobPostingApplication());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
