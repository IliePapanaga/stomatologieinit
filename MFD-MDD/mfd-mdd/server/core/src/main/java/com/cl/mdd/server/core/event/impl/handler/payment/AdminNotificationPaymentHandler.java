package com.cl.mdd.server.core.event.impl.handler.payment;

import com.cl.mdd.server.core.event.annotation.Consumer;
import com.cl.mdd.server.core.event.type.payment.PaymentStatusEvent;
import com.cl.mdd.server.core.service.notification.PaymentAttemptVariables;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;

import static com.cl.mdd.server.core.event.impl.handler.payment.PaymentStatusEventHandler.PAYMENT_RESULT_NOTIFICATION_FOR_SYSTEM;

@Consumer
public class AdminNotificationPaymentHandler implements EventHandler<PaymentStatusEvent> {

    private final PaymentStatusEventHandler delegate;

    @Autowired
    public AdminNotificationPaymentHandler(PaymentStatusEventHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    @NotificationDefinition(value = PAYMENT_RESULT_NOTIFICATION_FOR_SYSTEM,
            predefined = {
                    PaymentAttemptVariables.class
            })
    public void onEvent(PaymentStatusEvent event, long sequence, boolean endOfBatch) {
        delegate.notifySystem(event);
    }
}
