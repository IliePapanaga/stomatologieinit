package com.cl.mdd.server.core.data.persistent.access.payment;

import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.payment.Payment;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentAttempt;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentAttemptDao extends AbstractDao<PaymentAttempt> {

    List<PaymentAttempt> findByPaymentAndRoundOrderByCreated(Payment payment, int round);

    List<PaymentAttempt> findByPaymentOrderByCreatedDesc(Payment payment);

    PaymentAttempt findByOrderId(String orderId);

    PaymentAttempt findTopByPaymentOrderByCreatedDesc(Payment payment);

    @Modifying
    @Query("update PaymentAttempt a set a.method = null where a.method.id=?1")
    int deleteMethodReferences(String methodId);

}
