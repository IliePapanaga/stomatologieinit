package com.cl.mdd.server.core.event.impl.bus.posting.review;


import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.posting.application.review.NotifyPracticeOwnerAboutProfessionalWouldWorkPermanentlyEventHandler;
import com.cl.mdd.server.core.event.impl.handler.posting.application.review.NotifySystemAboutProfessionalWouldWorkPermanentlyEventHandler;
import com.cl.mdd.server.core.event.type.posting.application.review.ProfessionalHasReviewedLocationEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class ProfessionalHasReviewedLocationEventBus extends DisruptorEventBus<ProfessionalHasReviewedLocationEvent> {

    private static final int CONSUMER_STACK_SIZE = 3;

    @Autowired
    private NotifySystemAboutProfessionalWouldWorkPermanentlyEventHandler notifySystemAboutProfessionalWouldWorkPermanentlyEventHandler;

    @Autowired
    private NotifyPracticeOwnerAboutProfessionalWouldWorkPermanentlyEventHandler notifyPracticeOwnerAboutProfessionalWouldWorkPermanentlyEventHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<ProfessionalHasReviewedLocationEvent> initialize() {
        Disruptor<ProfessionalHasReviewedLocationEvent> disruptor = new Disruptor<>(ProfessionalHasReviewedLocationEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notifySystemAboutProfessionalWouldWorkPermanentlyEventHandler).
                then(notifyPracticeOwnerAboutProfessionalWouldWorkPermanentlyEventHandler).
                then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(ProfessionalHasReviewedLocationEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
