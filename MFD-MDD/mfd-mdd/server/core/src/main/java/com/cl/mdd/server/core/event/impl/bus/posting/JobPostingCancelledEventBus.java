package com.cl.mdd.server.core.event.impl.bus.posting;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.cancel.NotifyApplicantsAboutCancelledJobPostingHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.cancel.NotifySystemUsersAboutCancelledJobPostingHandler;
import com.cl.mdd.server.core.event.type.posting.JobPostingCancelledEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class JobPostingCancelledEventBus extends DisruptorEventBus<JobPostingCancelledEvent> {

    private static final int CONSUMER_STACK_SIZE = 3;

    @Autowired
    private NotifyApplicantsAboutCancelledJobPostingHandler notifyApplicantsAboutCancelledJobPostingHandler;

    @Autowired
    private NotifySystemUsersAboutCancelledJobPostingHandler notifySystemUsersAboutCancelledJobPostingHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<JobPostingCancelledEvent> initialize() {
        Disruptor<JobPostingCancelledEvent> disruptor = new Disruptor<>(JobPostingCancelledEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyApplicantsAboutCancelledJobPostingHandler).then(notifySystemUsersAboutCancelledJobPostingHandler).then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(JobPostingCancelledEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getJobPostingId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
