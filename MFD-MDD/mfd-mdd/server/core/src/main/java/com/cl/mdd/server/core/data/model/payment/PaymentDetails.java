package com.cl.mdd.server.core.data.model.payment;

import com.cl.mdd.server.core.data.model.MDDModel;

import java.util.List;

public class PaymentDetails extends MDDModel {

    private String paymentId;

    private String status;

    private List<PaymentStep> steps;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PaymentStep> getSteps() {
        return steps;
    }

    public void setSteps(List<PaymentStep> steps) {
        this.steps = steps;
    }
}
