package com.cl.mdd.server.core.event.impl.bus.posting;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.cl.mdd.server.core.event.impl.handler.posting.update.NotifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.update.NotifyApplicantsAboutPrematurelyCompletedApplicationsDuringJobPostingUpdateHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.update.NotifySystemUsersAboutJobPostingUpdateHandler;
import com.cl.mdd.server.core.event.type.posting.JobPostingUpdatedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class JobPostingUpdatedEventBus extends DisruptorEventBus<JobPostingUpdatedEvent> {

    private static final int CONSUMER_STACK_SIZE = 4;

    @Autowired
    private NotifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler notifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler;

    @Autowired
    private NotifyApplicantsAboutPrematurelyCompletedApplicationsDuringJobPostingUpdateHandler notifyApplicantsAboutPrematurelyCompletedApplicationsDuringJobPostingUpdateHandler;

    @Autowired
    private NotifySystemUsersAboutJobPostingUpdateHandler notifySystemUsersAboutJobPostingUpdateHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<JobPostingUpdatedEvent> initialize() {
        Disruptor<JobPostingUpdatedEvent> disruptor = new Disruptor<>(JobPostingUpdatedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor
                .handleEventsWith(notifyApplicantsAboutCancelledApplicationsDuringJobPostingUpdateHandler)
                .handleEventsWith(notifyApplicantsAboutPrematurelyCompletedApplicationsDuringJobPostingUpdateHandler)
                .handleEventsWith(notifySystemUsersAboutJobPostingUpdateHandler)
                .then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(JobPostingUpdatedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getJobPostingId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
