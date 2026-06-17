package com.cl.mdd.server.core.manager.payment;

import com.cl.mdd.server.core.data.persistent.model.payment.Payment;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class PaymentAccess {
    private final PaymentLockManager paymentLockManager;

    public PaymentAccess(PaymentLockManager paymentLockManager) {
        this.paymentLockManager = paymentLockManager;
    }

    public <T> T perform(Payment payment, Supplier<T> paymentOperation) {
        // 1. acquire payment lock
        paymentLockManager.acquire(payment);
        // 2. perform processing
        T result = null;
        try {
            result = paymentOperation.get();
        }
        finally {
        // 3. release payment lock (should release error fall silently?)
            if(! (result instanceof KeepPaymentLocked)) {
                paymentLockManager.release(payment);
            }
        }
        // 4. return
        return result;
    }

    public void free(Payment payment) {
        this.paymentLockManager.release(payment);
    }
}
