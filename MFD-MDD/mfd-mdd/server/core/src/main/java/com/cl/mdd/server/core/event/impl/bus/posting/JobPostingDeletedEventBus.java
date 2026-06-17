package com.cl.mdd.server.core.event.impl.bus.posting;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.JobPostingDeletedEventHandler;
import com.cl.mdd.server.core.event.type.posting.JobPostingDeletedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class JobPostingDeletedEventBus extends DisruptorEventBus<JobPostingDeletedEvent> {

    private static final int CONSUMER_STACK_SIZE = 2;

    @Autowired
    private JobPostingDeletedEventHandler handler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<JobPostingDeletedEvent> initialize() {
        Disruptor<JobPostingDeletedEvent> disruptor = new Disruptor<>(JobPostingDeletedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(handler).then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(JobPostingDeletedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getJobPostingId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
