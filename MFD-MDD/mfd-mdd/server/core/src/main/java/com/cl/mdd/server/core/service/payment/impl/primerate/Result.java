package com.cl.mdd.server.core.service.payment.impl.primerate;

import com.cl.mdd.server.core.data.persistent.model.payment.PaymentAttempt;

public class Result {

    private String status;
    private String message;
    private String transactionId;

    private Result(String status, String message, String transactionId) {
        this.status = status;
        this.message = message;
        this.transactionId = transactionId;
    }

    public static Result successful(String message, String transactionId) {
        return new Result(PaymentAttempt.STATUS_PAID, message, transactionId);
    }

    public static Result pending(String message, String transactionId) {
        return new Result(PaymentAttempt.STATUS_PENDING, message, transactionId);
    }

    public static Result failed(String message, String transactionId) {
        return new Result(PaymentAttempt.STATUS_FAILED, message, transactionId);
    }

    public String getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
