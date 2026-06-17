package com.cl.mdd.server.core.data.model.payment;

public class BankAccount extends PaymentInstrumentBase implements PaymentInstrument {

    private String name;

    private String account;

    private String routing;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BankAccount withName(String name) {
        setName(name);
        return this;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public BankAccount withAccount(String account) {
        setAccount(account);
        return this;
    }

    public String getRouting() {
        return routing;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public BankAccount withRouting(String routing) {
        setRouting(routing);
        return this;
    }
}
