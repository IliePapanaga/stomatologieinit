package com.cl.mdd.server.core.event.type.payment;

import com.cl.mdd.server.core.data.persistent.model.payment.Payment;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentAttempt;
import com.cl.mdd.server.core.event.Event;
import com.cl.mdd.server.core.event.EventFiller;

public class PaymentStatusEvent extends Event {

    private String status;

    private String paymentId;

    private String attemptId;

    public String getStatus() {
        return status;
    }

    private  void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    private void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getAttemptId() {
        return attemptId;
    }

    private void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }

    public static EventFiller<PaymentStatusEvent> create(PaymentAttempt attempt, Payment payment) {
        return event -> {
            event.setStatus(attempt.getStatus());
            event.setPaymentId(payment.getId());
            event.setAttemptId(attempt.getId());
        };
    }

}
