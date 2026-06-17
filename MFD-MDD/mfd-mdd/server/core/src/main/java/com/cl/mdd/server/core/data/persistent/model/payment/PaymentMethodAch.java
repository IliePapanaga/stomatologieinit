package com.cl.mdd.server.core.data.persistent.model.payment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PAYMENT_METHODS_ACH")
public class PaymentMethodAch extends PaymentMethod {

    @Column(name = "ach_name", nullable = false, updatable = false)
    private String name;

    @Column(name = "ach_account_no", nullable = false, updatable = false)
    private String account;

    @Column(name = "ach_routing_no", nullable = false, updatable = false)
    private String routing;

    public String type() {
        return TYPE_ACH;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRouting() {
        return this.routing;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }
}
