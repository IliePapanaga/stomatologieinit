package com.cl.mdd.server.core.data.model.payment;

import com.cl.mdd.server.core.data.model.MDDModel;

public class PaymentOptionsResponse extends MDDModel {

    public static final String DONE = "DONE";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String ACTION = "ACTION_REQUIRED";
    public static final String FAIL = "FAILED";

    private String status;

    private String message;

    private String url;

    static PaymentOptionsResponse status(String status) {
        PaymentOptionsResponse response = new PaymentOptionsResponse();
        response.setStatus(status);
        return response;
    }

    public static PaymentOptionsResponse done() {
        return status(DONE);
    }

    public static PaymentOptionsResponse accepted() {
        return status(ACCEPTED);
    }

    public static PaymentOptionsResponse url(String url) {
        PaymentOptionsResponse response = status(ACTION);
        response.setUrl(url);
        return response;
    }

    public static PaymentOptionsResponse fail(String message) {
        PaymentOptionsResponse response = status(FAIL);
        response.setMessage(message);
        return response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
