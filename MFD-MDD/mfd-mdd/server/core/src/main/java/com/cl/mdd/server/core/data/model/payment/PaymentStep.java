package com.cl.mdd.server.core.data.model.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentStep {

    private LocalDateTime date;

    private String status;

    private BigDecimal amount;

    private String method;

    private String log;

    private String gatewayId;

    private String gatewayMessage;

    public PaymentStep() {
    }

    public PaymentStep(LocalDateTime date, String status, BigDecimal amount, String method,  String log, String gatewayId, String gatewayMessage) {
        this.date = date;
        this.status = status;
        this.amount = amount;
        this.method = method;
        this.log = log;
        this.gatewayId = gatewayId;
        this.gatewayMessage = gatewayMessage;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getGatewayMessage() {
        return gatewayMessage;
    }

    public void setGatewayMessage(String gatewayMessage) {
        this.gatewayMessage = gatewayMessage;
    }
}
