package com.cl.mdd.server.core.service.payment.impl.primerate.model;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Billing {

    @XmlElement(name = "billing-id")
    private String billingId;

    @XmlElement(name = "cc-number")
    private String ccNumber;

    @XmlElement(name = "cc-exp")
    private String ccExp;

    @XmlElement(name = "account-name")
    private String accountName;

    @XmlElement(name = "account-number")
    private String accountNumber;

    @XmlElement(name = "routing-number")
    private String routingNumber;

    /**
     * Check for type of the billing.
     * @return <code>true</code> for credit card, <code>false</code> for bank account
     */
    public boolean creditCard() {
        return StringUtils.isNotBlank(ccNumber);
    }

    public String getBillingId() {
        return this.billingId;
    }

    public void setBillingId(String billingId) {
        this.billingId = billingId;
    }

    public String getCcNumber() {
        return this.ccNumber;
    }

    public void setCcNumber(String ccNumber) {
        this.ccNumber = ccNumber;
    }

    public String getCcExp() {
        return this.ccExp;
    }

    public void setCcExp(String ccExp) {
        this.ccExp = ccExp;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getRoutingNumber() {
        return this.routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }
}
