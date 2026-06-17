package com.cl.mdd.server.core.event.impl.bus.posting.application;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.application.cancel.NotifyPracticeOwnerAboutCancelledApplicationEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.cancel.NotifyProfessionalAboutCancelledEventHandler;
import com.cl.mdd.server.core.event.type.posting.application.cancel.JobPostingApplicationCancelledEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class JobPostingApplicationCancelledEventBus extends DisruptorEventBus<JobPostingApplicationCancelledEvent> {

    private static final int CONSUMER_STACK_SIZE = 3;

    @Autowired
    private NotifyPracticeOwnerAboutCancelledApplicationEventHandler notifyPracticeOwnerAboutCancelledApplicationEventHandler;

    @Autowired
    private NotifyProfessionalAboutCancelledEventHandler notifyProfessionalAboutCancelledEventHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<JobPostingApplicationCancelledEvent> initialize() {
        Disruptor<JobPostingApplicationCancelledEvent> disruptor = new Disruptor<>(JobPostingApplicationCancelledEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyPracticeOwnerAboutCancelledApplicationEventHandler).
                then(notifyProfessionalAboutCancelledEventHandler).
                then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(JobPostingApplicationCancelledEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getJobPostingApplicationId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
