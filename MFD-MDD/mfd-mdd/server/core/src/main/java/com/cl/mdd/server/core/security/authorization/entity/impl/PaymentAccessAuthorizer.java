package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.payment.PaymentDao;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentMethodDao;
import com.cl.mdd.server.core.data.persistent.model.payment.Payment;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethod;
import com.cl.mdd.server.core.security.authorization.annotation.AccessAuthorizer;

import java.util.Objects;

/**
 * Authorize access to payment.
 */
@AccessAuthorizer
public class PaymentAccessAuthorizer extends AbstractEntityAccessAuthorizer<Payment> {

    private final PaymentDao paymentDao;

    private final PaymentMethodDao paymentMethodDao;

    public PaymentAccessAuthorizer(PaymentDao paymentDao, PaymentMethodDao paymentMethodDao) {
        this.paymentDao = paymentDao;
        this.paymentMethodDao = paymentMethodDao;
    }

    @Override
    public boolean readAllowed(String id) {
        Payment p;
        return securityAccess.isCurrentSystemUser()
                ||
                Objects.nonNull(id)
                        && (p = paymentDao.findOne(id)) != null
                        && p.getPractice().getId().equals(securityAccess.currentUserId());
    }

    public boolean paymentMethodAccessible(String id) {
        PaymentMethod m;
        return id == null
                || (m = paymentMethodDao.findOne(id)) != null && m.getPractice().getId().equals(securityAccess.currentUserId());

    }

    public boolean currentPractice(String id) {
        return securityAccess.isCurrentSystemUser() || securityAccess.currentUserId().equals(id);
    }
}
