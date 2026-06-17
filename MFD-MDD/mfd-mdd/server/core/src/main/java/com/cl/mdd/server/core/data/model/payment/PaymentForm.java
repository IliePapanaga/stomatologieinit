package com.cl.mdd.server.core.data.model.payment;

import com.cl.mdd.server.core.data.model.MDDModel;

public class PaymentForm extends MDDModel {

    private String submitUrl;

    public PaymentForm() {
    }

    public PaymentForm(String submitUrl) {
        this.submitUrl = submitUrl;
    }

    public String getSubmitUrl() {
        return submitUrl;
    }

    public void setSubmitUrl(String submitUrl) {
        this.submitUrl = submitUrl;
    }
}
