package com.cl.mdd.server.core.data.model.payment;

import com.cl.mdd.server.core.data.model.MDDModel;
import com.cl.mdd.server.core.validation.constraint.payment.PaymentExists;
import com.cl.mdd.server.core.validation.constraint.payment.PaymentMethodForOptions;
import com.cl.mdd.server.core.validation.constraint.payment.PaymentStatusOptions;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@PaymentStatusOptions(message = "{payment.options.payment.option.invalid}")
public class PaymentOptionsModel extends MDDModel {

    public static final String MODIFY = "MODIFY";
    public static final String COMPLETE = "COMPLETE";
    public static final String RUN = "RUN";
    public static final String CANCEL = "CANCEL";
    public static final String INSTANT_PAY = "INSTANT_PAY";
    public static final String PARTIAL = "PARTIAL";
    public static final String FAIL = "FAIL";

    @PaymentExists(message = "{payment.options.payment.not.exists}")
    private String paymentId;

    @NotEmpty(message = "{payment.options.option.required}")
    private String option;

    @Min(value = 0, message = "{payment.options.amount.negative}")
    private BigDecimal amount;

    @Size(max = 255, message = "{payment.options.label.too.long}")
    private String comment;

    @PaymentMethodForOptions(message = "{payment.options.payment.method.not.exists}")
    private String paymentMethodId;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentOptionsModel withPaymentId(String paymentId) {
        setPaymentId(paymentId);
        return this;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public PaymentOptionsModel withOption(String option) {
        setOption(option);
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentOptionsModel withAmount(BigDecimal amount) {
        setAmount(amount);
        return this;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PaymentOptionsModel withComment(String comment) {
        setComment(comment);
        return this;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public PaymentOptionsModel withPaymentMethodId(String paymentMethodId) {
        setPaymentMethodId(paymentMethodId);
        return this;
    }
}
