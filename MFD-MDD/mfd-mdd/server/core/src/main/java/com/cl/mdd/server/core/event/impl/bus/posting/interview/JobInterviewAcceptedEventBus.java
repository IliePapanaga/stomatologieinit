package com.cl.mdd.server.core.event.impl.bus.posting.interview;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.accept.NotifyPracticeOwnerAboutJobInterviewAcceptedEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.interview.accept.NotifyProfessionalAboutJobInterviewAcceptedEventHandler;
import com.cl.mdd.server.core.event.type.posting.interview.JobInterviewAcceptedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class JobInterviewAcceptedEventBus extends DisruptorEventBus<JobInterviewAcceptedEvent> {

    private static final int CONSUMER_STACK_SIZE = 3;

    @Autowired
    private NotifyPracticeOwnerAboutJobInterviewAcceptedEventHandler notifyPracticeOwnerAboutJobInterviewAcceptedEventHandler;

    @Autowired
    private NotifyProfessionalAboutJobInterviewAcceptedEventHandler notifyProfessionalAboutJobInterviewAcceptedEventHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<JobInterviewAcceptedEvent> initialize() {
        Disruptor<JobInterviewAcceptedEvent> disruptor = new Disruptor<>(JobInterviewAcceptedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyPracticeOwnerAboutJobInterviewAcceptedEventHandler)
                .then(notifyProfessionalAboutJobInterviewAcceptedEventHandler)
                .then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(JobInterviewAcceptedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getInterviewId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
