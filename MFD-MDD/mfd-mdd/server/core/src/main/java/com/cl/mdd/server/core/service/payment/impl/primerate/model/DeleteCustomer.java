package com.cl.mdd.server.core.service.payment.impl.primerate.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "delete-customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeleteCustomer {

    @XmlElement(name = "api-key")
    private String apiKey;

    @XmlElement(name = "customer-vault-id")
    private String vaultId;

    public DeleteCustomer() {
    }

    public DeleteCustomer(String apiKey, String vaultId) {
        this.apiKey = apiKey;
        this.vaultId = vaultId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getVaultId() {
        return vaultId;
    }

    public void setVaultId(String vaultId) {
        this.vaultId = vaultId;
    }
}
