package com.cl.mdd.server.core.event.impl.bus.posting.interview;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.JobInterviewCancelledEventHandler;
import com.cl.mdd.server.core.event.type.posting.interview.JobInterviewCancelledEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class JobInterviewCancelledEventBus extends DisruptorEventBus<JobInterviewCancelledEvent> {

    private static final int CONSUMER_STACK_SIZE = 2;

    @Autowired
    private JobInterviewCancelledEventHandler jobInterviewCancelledEventHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<JobInterviewCancelledEvent> initialize() {
        Disruptor<JobInterviewCancelledEvent> disruptor = new Disruptor<>(JobInterviewCancelledEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(jobInterviewCancelledEventHandler).then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(JobInterviewCancelledEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getInterviewId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
