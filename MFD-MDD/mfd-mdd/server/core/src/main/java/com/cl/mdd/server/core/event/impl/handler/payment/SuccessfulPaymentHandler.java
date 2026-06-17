package com.cl.mdd.server.core.event.impl.handler.payment;

import com.cl.mdd.server.core.data.persistent.model.payment.PaymentAttempt;
import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.payment.PaymentStatusEvent;
import com.cl.mdd.server.core.service.notification.AdminVariables;
import com.cl.mdd.server.core.service.notification.PaymentAttemptVariables;
import com.cl.mdd.server.core.service.notification.PracticeOwnerVariables;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;

import static com.cl.mdd.server.core.event.impl.handler.payment.PaymentStatusEventHandler.PAYMENT_SUCCESSFUL_NOTIFICATION;

@Consumer
public class SuccessfulPaymentHandler implements EventHandler<PaymentStatusEvent> {

    private final PaymentStatusEventHandler delegate;

    @Autowired
    public SuccessfulPaymentHandler(PaymentStatusEventHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    @NotificationDefinition(value = PAYMENT_SUCCESSFUL_NOTIFICATION,
            predefined = {
                    PracticeOwnerVariables.class,
                    PaymentAttemptVariables.class,
                    AdminVariables.class
            })
    public void onEvent(PaymentStatusEvent event, long sequence, boolean endOfBatch) {
        delegate.notifyPractice(event, PaymentAttempt.STATUS_PAID);
    }
}
