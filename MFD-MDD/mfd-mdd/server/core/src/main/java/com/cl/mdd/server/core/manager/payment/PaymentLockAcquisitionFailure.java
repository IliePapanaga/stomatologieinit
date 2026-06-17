package com.cl.mdd.server.core.manager.payment;

import com.cl.mdd.server.core.exception.MDDException;

public class PaymentLockAcquisitionFailure extends MDDException {

    public PaymentLockAcquisitionFailure(String message, String code) {
        super(message, code);
    }

    public PaymentLockAcquisitionFailure(String message, Throwable cause, String code) {
        super(message, cause, code);
    }
}
