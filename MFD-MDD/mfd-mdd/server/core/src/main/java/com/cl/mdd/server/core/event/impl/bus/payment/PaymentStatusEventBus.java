package com.cl.mdd.server.core.event.impl.bus.payment;

import com.cl.mdd.server.core.event.annotation.EventBus;
import com.cl.mdd.server.core.event.bus.DisruptorEventBus;
import com.cl.mdd.server.core.event.impl.handler.payment.AdminNotificationPaymentHandler;
import com.cl.mdd.server.core.event.impl.handler.payment.FailedPaymentHandler;
import com.cl.mdd.server.core.event.impl.handler.payment.SuccessfulPaymentHandler;
import com.cl.mdd.server.core.event.type.payment.PaymentStatusEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.cl.mdd.server.core.event.impl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EventBus
public class PaymentStatusEventBus extends DisruptorEventBus<PaymentStatusEvent> {

    private static final int CONSUMER_STACK_SIZE = 4;

    private ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_STACK_SIZE);

    private final SuccessfulPaymentHandler successfulHandler;
    private final FailedPaymentHandler failedHandler;
    private final AdminNotificationPaymentHandler adminNotificationHandler;

    @Autowired
    public PaymentStatusEventBus(SuccessfulPaymentHandler successfulHandler, FailedPaymentHandler failedHandler,
            AdminNotificationPaymentHandler adminNotificationHandler) {
        this.successfulHandler = successfulHandler;
        this.failedHandler = failedHandler;
        this.adminNotificationHandler = adminNotificationHandler;
    }

    @Override
    protected Disruptor<PaymentStatusEvent> initialize() {
        Disruptor<PaymentStatusEvent> disruptor = new Disruptor<>(PaymentStatusEvent::new, 1024, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(successfulHandler, failedHandler, adminNotificationHandler)
                .then(eventCleanupHandler);
        return disruptor;
    }

    @Override
    protected void valid(PaymentStatusEvent event) {
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
    }
}
