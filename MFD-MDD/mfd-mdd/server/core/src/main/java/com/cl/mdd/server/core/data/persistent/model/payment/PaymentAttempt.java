package com.cl.mdd.server.core.data.persistent.model.payment;

import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "PAYMENT_ATTEMPTS")
public class PaymentAttempt extends Identifiable {

    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_DONE = "DONE";

    @ManyToOne
    @JoinColumn(name = "fk_payment_id", nullable = false, updatable = false)
    private Payment payment;

    @Column(name = "order_id", nullable = false, updatable = false, unique = true)
    private String orderId = UUID.randomUUID().toString();

    @Column(name = "attempt_date", nullable = false, updatable = false)
    private LocalDateTime created = LocalDateTime.now();

    @Column(name = "attempt_status", nullable = false)
    private String status = STATUS_NEW;

    @Column(name = "gateway_status")
    private String gatewayStatus;

    @Column(name = "gateway_id")
    private String gatewayId;

    @Lob
    @Column(name = "attempt_log", length = 65535)
    private String log;

    @ManyToOne
    @JoinColumn(name = "fk_method_id")
    private PaymentMethod method;

    @Column(name = "payment_method_label")
    private String paymentMethodLabel;

    @Column(name = "amount", nullable = false, updatable = false)
    private BigDecimal amount;

    @Column(name = "attempts_round", nullable = false)
    private int round;

    public Payment getPayment() {
        return this.payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getCreated() {
        return this.created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGatewayStatus() {
        return this.gatewayStatus;
    }

    public void setGatewayStatus(String gatewayStatus) {
        this.gatewayStatus = gatewayStatus;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getLog() {
        return this.log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public PaymentMethod getMethod() {
        return this.method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public String getPaymentMethodLabel() {
        return paymentMethodLabel;
    }

    public void setPaymentMethodLabel(String paymentMethodLabel) {
        this.paymentMethodLabel = paymentMethodLabel;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getRound() {
        return this.round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
