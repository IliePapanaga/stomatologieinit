package com.cl.mdd.server.core.event.impl.bus.posting.application;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.application.reject.NotifyConcurrentApplicantsAboutRejectedJobPostingApplicationHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.reject.NotifyPracticeOwnerAboutRejectedJobPostingApplicationHandler;
import com.cl.mdd.server.core.event.type.posting.application.JobPostingApplicationRejectedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class JobPostingApplicationRejectedEventBus extends DisruptorEventBus<JobPostingApplicationRejectedEvent> {

    private static final int CONSUMER_STACK_SIZE = 3;

    @Autowired
    private NotifyPracticeOwnerAboutRejectedJobPostingApplicationHandler notifyPracticeOwnerAboutRejectedJobPostingApplicationHandler;

    @Autowired
    private NotifyConcurrentApplicantsAboutRejectedJobPostingApplicationHandler notifyConcurrentApplicantsAboutRejectedJobPostingApplicationHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<JobPostingApplicationRejectedEvent> initialize() {
        Disruptor<JobPostingApplicationRejectedEvent> disruptor = new Disruptor<>(JobPostingApplicationRejectedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyPracticeOwnerAboutRejectedJobPostingApplicationHandler)
                .then(notifyConcurrentApplicantsAboutRejectedJobPostingApplicationHandler).then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(JobPostingApplicationRejectedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getJobPostingApplicationId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
