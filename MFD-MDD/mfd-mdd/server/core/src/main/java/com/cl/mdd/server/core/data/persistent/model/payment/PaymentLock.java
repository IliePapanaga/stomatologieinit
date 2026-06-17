package com.cl.mdd.server.core.data.persistent.model.payment;

import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT_LOCKS")
public class PaymentLock extends Identifiable {

    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created = LocalDateTime.now();

    @OneToOne(optional = false)
    //@JoinColumn(name = "fk_payment_id", nullable = false, unique = true)
    @MapsId
    private Payment payment;

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
