package com.cl.mdd.server.core.event.impl.bus.posting.interview;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.JobInterviewStartSoonEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.MarkJobInterviewNotifiedAboutStartSoonEventHandler;
import com.cl.mdd.server.core.event.type.posting.interview.JobInterviewStartSoonEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class JobInterviewStartSoonEventBus extends DisruptorEventBus<JobInterviewStartSoonEvent> {

    private static final int CONSUMER_STACK_SIZE = 3;

    @Autowired
    private JobInterviewStartSoonEventHandler jobInterviewStartSoonEventHandler;

    @Autowired
    private MarkJobInterviewNotifiedAboutStartSoonEventHandler markJobInterviewNotifiedAboutStartSoonEventHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<JobInterviewStartSoonEvent> initialize() {
        Disruptor<JobInterviewStartSoonEvent> disruptor = new Disruptor<>(JobInterviewStartSoonEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(jobInterviewStartSoonEventHandler)
                .then(markJobInterviewNotifiedAboutStartSoonEventHandler)
                .then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(JobInterviewStartSoonEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getInterviewId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
