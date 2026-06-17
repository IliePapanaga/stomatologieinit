package com.cl.mdd.server.core.data.model.payment;

public class CreditCard extends PaymentInstrumentBase implements PaymentInstrument {

    private String number;

    private String expiration;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public CreditCard withNumber(String number) {
        setNumber(number);
        return this;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public CreditCard withExpiration(String expiration) {
        setExpiration(expiration);
        return this;
    }
}
