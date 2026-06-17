package com.cl.mdd.server.core.event.impl.bus.posting.review;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.application.review.NotifyProfessionalAboutPracticeOwnerWouldHirePermanentlyEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.review.NotifySystemAboutPracticeOwnerWouldHirePermanentlyEventHandler;
import com.cl.mdd.server.core.event.type.posting.application.review.LocationHasReviewedProfessionalEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class LocationHasReviewedProfessionalEventBus extends DisruptorEventBus<LocationHasReviewedProfessionalEvent> {

    private static final int CONSUMER_STACK_SIZE = 3;

    @Autowired
    private NotifyProfessionalAboutPracticeOwnerWouldHirePermanentlyEventHandler notifyProfessionalAboutPracticeOwnerWouldHirePermanentlyEventHandler;

    @Autowired
    private NotifySystemAboutPracticeOwnerWouldHirePermanentlyEventHandler notifySystemAboutPracticeOwnerWouldHirePermanentlyEventHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<LocationHasReviewedProfessionalEvent> initialize() {
        Disruptor<LocationHasReviewedProfessionalEvent> disruptor = new Disruptor<>(LocationHasReviewedProfessionalEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifyProfessionalAboutPracticeOwnerWouldHirePermanentlyEventHandler).
                then(notifySystemAboutPracticeOwnerWouldHirePermanentlyEventHandler).
                then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(LocationHasReviewedProfessionalEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
