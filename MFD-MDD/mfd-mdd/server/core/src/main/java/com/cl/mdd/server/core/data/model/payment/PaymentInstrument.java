package com.cl.mdd.server.core.data.model.payment;

public interface PaymentInstrument {

    String getId();

    String getLabel();

    boolean isPreferred();

}
