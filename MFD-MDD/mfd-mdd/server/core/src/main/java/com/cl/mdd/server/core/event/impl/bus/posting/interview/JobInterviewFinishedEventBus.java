package com.cl.mdd.server.core.event.impl.bus.posting.interview;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.JobInterviewFinishedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.MarkJobInterviewAboutFinishedEventHandler;
import com.cl.mdd.server.core.event.type.posting.interview.JobInterviewFinishedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class JobInterviewFinishedEventBus extends DisruptorEventBus<JobInterviewFinishedEvent> {

    private static final int CONSUMER_STACK_SIZE = 3;

    @Autowired
    private JobInterviewFinishedEventHandler jobInterviewFinishedEventHandler;

    @Autowired
    private MarkJobInterviewAboutFinishedEventHandler markJobInterviewAboutFinishedEventHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<JobInterviewFinishedEvent> initialize() {
        Disruptor<JobInterviewFinishedEvent> disruptor = new Disruptor<>(JobInterviewFinishedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(jobInterviewFinishedEventHandler).then(markJobInterviewAboutFinishedEventHandler).then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(JobInterviewFinishedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getInterviewId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
