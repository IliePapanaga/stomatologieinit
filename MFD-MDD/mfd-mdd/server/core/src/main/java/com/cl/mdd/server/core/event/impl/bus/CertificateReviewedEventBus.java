package com.cl.mdd.server.core.event.impl.bus;

import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.cl.mdd.server.core.event.impl.handler.CertificateReviewedEventHandler;
import com.cl.mdd.server.core.event.type.CertificateReviewedEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class CertificateReviewedEventBus extends DisruptorEventBus<CertificateReviewedEvent> {

    private static final int CONSUMER_STACK_SIZE = 2;

    @Autowired
    private CertificateReviewedEventHandler certificateReviewedEventHandler;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    @Override
    protected Disruptor<CertificateReviewedEvent> initialize() {
        Disruptor<CertificateReviewedEvent> disruptor = new Disruptor<>(CertificateReviewedEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(certificateReviewedEventHandler).then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(CertificateReviewedEvent event) {
        Objects.requireNonNull(event.getFireDate());
        Objects.requireNonNull(event.getCertificateDetailsId());
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
