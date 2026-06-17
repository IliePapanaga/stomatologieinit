package com.cl.mdd.server.core.data.persistent.model.payment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PAYMENT_METHODS_CARD")
public class PaymentMethodCard extends PaymentMethod {

    @Column(name = "cc_number", nullable = false, updatable = false)
    private String number;

    @Column(name = "cc_exp", nullable = false, updatable = false)
    private String expiration;

    public String type() {
        return TYPE_CC;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExpiration() {
        return this.expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }
}
