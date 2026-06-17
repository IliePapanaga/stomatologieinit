package com.cl.mdd.server.core.service.payment.impl.primerate.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sale")
public class Sale extends StepOne {

    @XmlElement(name = "customer-vault-id")
    private String vaultId;

    @XmlElement(name = "amount")
    private String amount;

    // TODO: These two order fields are not documented !!
    @XmlElement(name = "order-id")
    private String orderId;

    @XmlElement(name = "order-description")
    private String orderDescription;

    public String getVaultId() {
        return vaultId;
    }

    public void setVaultId(String vaultId) {
        this.vaultId = vaultId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }
}
