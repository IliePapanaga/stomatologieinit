package com.cl.mdd.server.core.event.impl.bus.posting.application;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.cl.mdd.server.core.event.impl.handler.posting.application.accept.NotifyConcurrentApplicantsAboutAcceptedJobPostingApplicationHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.accept.NotifyPracticeOwnerAboutAcceptedJobPostingApplicationHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.accept.NotifyProfessionalAboutAcceptedJobPostingApplicationHandler;
import com.cl.mdd.server.core.event.type.posting.application.JobPostingApplicationAcceptedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class JobPostingApplicationAcceptedEventBus extends DisruptorEventBus<JobPostingApplicationAcceptedEvent> {

    private static final int CONSUMER_STACK_SIZE = 4;

    @Autowired
    private NotifyPracticeOwnerAboutAcceptedJobPostingApplicationHandler notifyPracticeOwnerAboutAcceptedJobPostingApplicationHandler;

    @Autowired
    private NotifyConcurrentApplicantsAboutAcceptedJobPostingApplicationHandler notifyConcurrentApplicantsAboutAcceptedJobPostingApplicationHandler;

    @Autowired
    private NotifyProfessionalAboutAcceptedJobPostingApplicationHandler notifyProfessionalAboutAcceptedJobPostingApplicationHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<JobPostingApplicationAcceptedEvent> initialize() {
        Disruptor<JobPostingApplicationAcceptedEvent> disruptor = new Disruptor<>(JobPostingApplicationAcceptedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyPracticeOwnerAboutAcceptedJobPostingApplicationHandler).
                then(notifyProfessionalAboutAcceptedJobPostingApplicationHandler).
                then(notifyConcurrentApplicantsAboutAcceptedJobPostingApplicationHandler).
                then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(JobPostingApplicationAcceptedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getJobPostingApplicationId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
