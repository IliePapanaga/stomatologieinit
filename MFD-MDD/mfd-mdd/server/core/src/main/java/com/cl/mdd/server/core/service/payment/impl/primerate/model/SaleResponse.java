package com.cl.mdd.server.core.service.payment.impl.primerate.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class SaleResponse extends EndResponse {

    @XmlElement(name = "transaction-id")
    private String transactionId;

    @XmlElement(name = "billing")
    private Billing billing;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Billing getBilling() {
        return billing;
    }

    public void setBilling(Billing billing) {
        this.billing = billing;
    }
}
