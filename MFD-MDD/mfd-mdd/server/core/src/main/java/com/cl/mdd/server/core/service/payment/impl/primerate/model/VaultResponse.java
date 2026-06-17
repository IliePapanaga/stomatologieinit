package com.cl.mdd.server.core.service.payment.impl.primerate.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class VaultResponse extends EndResponse {

    @XmlElement(name = "customer-vault-id")
    private String vaultId;

    @XmlElement(name = "billing")
    private Billing billing;

    public String getVaultId() {
        return this.vaultId;
    }

    public void setVaultId(String vaultId) {
        this.vaultId = vaultId;
    }

    public Billing getBilling() {
        return this.billing;
    }

    public void setBilling(Billing billing) {
        this.billing = billing;
    }

}
