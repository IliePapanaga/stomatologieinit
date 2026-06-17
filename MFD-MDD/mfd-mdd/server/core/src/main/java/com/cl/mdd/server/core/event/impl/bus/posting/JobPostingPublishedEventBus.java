package com.cl.mdd.server.core.event.impl.bus.posting;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.cl.mdd.server.core.event.impl.handler.posting.NotifyProfessionalAboutJobPostingPublishedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.NotifySystemUserAboutJobPostingPublishedEventHandler;
import com.cl.mdd.server.core.event.type.posting.JobPostingPublishedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class JobPostingPublishedEventBus extends DisruptorEventBus<JobPostingPublishedEvent> {

    private static final int CONSUMER_STACK_SIZE = 3;

    @Autowired
    private NotifyProfessionalAboutJobPostingPublishedEventHandler notifyProfessionalAboutJobPostingPublishedEventHandler;

    @Autowired
    private NotifySystemUserAboutJobPostingPublishedEventHandler notifySystemUserAboutJobPostingPublishedEventHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<JobPostingPublishedEvent> initialize() {
        Disruptor<JobPostingPublishedEvent> disruptor = new Disruptor<>(JobPostingPublishedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyProfessionalAboutJobPostingPublishedEventHandler).
                then(notifySystemUserAboutJobPostingPublishedEventHandler).
                then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(JobPostingPublishedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getJobPostingId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
