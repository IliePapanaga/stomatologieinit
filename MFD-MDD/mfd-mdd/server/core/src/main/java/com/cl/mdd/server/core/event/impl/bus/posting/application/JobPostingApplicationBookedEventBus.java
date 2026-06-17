package com.cl.mdd.server.core.event.impl.bus.posting.application;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.cl.mdd.server.core.event.impl.handler.posting.application.NotifyProfessionalAboutJobPostingApplicationBookedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.NotifySystemUserAboutJobPostingApplicationBookedEventHandler;
import com.cl.mdd.server.core.event.type.posting.application.JobPostingApplicationBookedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class JobPostingApplicationBookedEventBus extends DisruptorEventBus<JobPostingApplicationBookedEvent> {

    private static final int CONSUMER_STACK_SIZE = 3;

    @Autowired
    private NotifyProfessionalAboutJobPostingApplicationBookedEventHandler notifyProfessionalAboutJobPostingApplicationBookedEventHandler;

    @Autowired
    private NotifySystemUserAboutJobPostingApplicationBookedEventHandler notifySystemUserAboutJobPostingApplicationBookedEventHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<JobPostingApplicationBookedEvent> initialize() {
        Disruptor<JobPostingApplicationBookedEvent> disruptor = new Disruptor<>(JobPostingApplicationBookedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyProfessionalAboutJobPostingApplicationBookedEventHandler, notifySystemUserAboutJobPostingApplicationBookedEventHandler).
                then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(JobPostingApplicationBookedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getJobPostingApplicationId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
