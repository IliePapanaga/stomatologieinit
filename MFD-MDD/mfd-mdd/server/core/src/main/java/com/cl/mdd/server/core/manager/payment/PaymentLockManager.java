package com.cl.mdd.server.core.manager.payment;

import com.cl.mdd.server.core.data.persistent.access.payment.PaymentLockDao;
import com.cl.mdd.server.core.data.persistent.model.payment.Payment;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentLock;
import com.cl.mdd.server.core.manager.annotation.Manager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

@Manager
public class PaymentLockManager {

    @PersistenceContext
    private EntityManager entityManager;

    private final PaymentLockDao lockDao;

    public PaymentLockManager(PaymentLockDao lockDao) {
        this.lockDao = lockDao;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void acquire(Payment payment) throws PaymentLockAcquisitionFailure {
        if(lockDao.findByPayment(payment) != null) {
            throw new PaymentLockAcquisitionFailure("Payment previously locked.", "PAYMENT_LOCK_EXISTS");
        }
        try {
            payment = entityManager.find(Payment.class, payment.getId(), LockModeType.PESSIMISTIC_WRITE);
        } catch (PersistenceException persistenceException) {
            throw new PaymentLockAcquisitionFailure("Payment lock failure.", "PAYMENT_LOCK_CANNOT_OBTAIN");
        }
        PaymentLock lock = new PaymentLock();
        lock.setPayment(payment);
        lockDao.save(lock);
    }

    @Transactional
    public void release(Payment payment) {
        PaymentLock lock = lockDao.findByPayment(payment);
        if(lock != null) {
            lockDao.delete(lock);
        }
    }
}
