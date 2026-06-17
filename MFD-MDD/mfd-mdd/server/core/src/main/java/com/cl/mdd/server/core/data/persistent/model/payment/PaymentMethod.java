package com.cl.mdd.server.core.data.persistent.model.payment;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PAYMENT_METHODS")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PaymentMethod extends AuditedEntity {

    public static final String TYPE_CC = "CC";
    public static final String TYPE_ACH = "ACH";
    public static final String TYPE_MANUAL = "MANUAL";

    @ManyToOne
    @JoinColumn(name = "fk_practice_id", nullable = false)
    private Practice practice;

    @Column(name = "vault_id", unique = true, nullable = false)
    private String vaultId;

    @Column(name = "label")
    private String label;

    @Column(name = "is_preferred", nullable = false)
    private boolean preferred;

    public abstract String type();

    public Practice getPractice() {
        return this.practice;
    }

    public void setPractice(Practice practice) {
        this.practice = practice;
    }

    public String getVaultId() {
        return this.vaultId;
    }

    public void setVaultId(String vaultId) {
        this.vaultId = vaultId;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isPreferred() {
        return this.preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }
}
