package com.cl.mdd.server.core.data.persistent.access.payment;

import com.cl.mdd.server.core.data.persistent.access.common.AbstractDao;
import com.cl.mdd.server.core.data.persistent.model.payment.Payment;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentLock;

public interface PaymentLockDao extends AbstractDao<PaymentLock> {

    PaymentLock findByPayment(Payment payment);

}
