package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.model.payment.PaymentModel;
import com.cl.mdd.server.core.data.persistent.model.payment.Payment;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentAttempt;
import com.cl.mdd.server.core.service.notification.definition.NotificationContextSupplier;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.cl.mdd.server.core.service.notification.definition.impl.BasePredefinedVariables;
import com.cl.mdd.server.core.service.payment.impl.PaymentConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class PaymentAttemptVariables extends BasePredefinedVariables implements NotificationContextSupplier<PaymentAttempt> {

    public static final DateTimeFormatter PAYMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("LLLL/dd/yyyy");

    public static final String VAR_TYPE_KEY_HOLDER = "{payment.origin.type}";
    public static final String VAR_POSTING_KEY_HOLDER = "{payment.posting.name}";
    public static final String VAR_DATE_KEY_HOLDER = "{payment.date}";
    public static final String VAR_AMOUNT_KEY_HOLDER = "{payment.amount}";
    public static final String VAR_PRACTICE_KEY_HOLDER = "{payment.practice}";
    public static final String VAR_STATUS_KEY_HOLDER = "{payment.status}";
    public static final String VAR_METHOD_KEY_HOLDER = "{payment.method}";

    @Autowired
    private PaymentConverter paymentConverter;

    private static final List<Variable> VARIABLES = Arrays.asList(
            variable("notification.var.payment.origin.type", VAR_TYPE_KEY_HOLDER),
            variable("notification.var.payment.posting.name", VAR_POSTING_KEY_HOLDER),
            variable("notification.var.payment.date", VAR_DATE_KEY_HOLDER),
            variable("notification.var.payment.amount", VAR_AMOUNT_KEY_HOLDER),
            variable("notification.var.payment.practice", VAR_PRACTICE_KEY_HOLDER),
            variable("notification.var.payment.status", VAR_STATUS_KEY_HOLDER),
            variable("notification.var.payment.method", VAR_METHOD_KEY_HOLDER)
    );

    @Override
    public List<Variable> expand() {
        return VARIABLES;
    }

    @Override
    public void supply(PaymentAttempt paymentAttempt, Map<String, String> context) {
        PaymentModel paymentModel = paymentConverter.toPaymentModel(paymentAttempt.getPayment());
        String type = getType(paymentAttempt.getPayment());
        String status = getStatus(paymentAttempt);

        context.put(VAR_TYPE_KEY_HOLDER, type);
        context.put(VAR_POSTING_KEY_HOLDER, paymentModel.getLabel());
        context.put(VAR_DATE_KEY_HOLDER, paymentAttempt.getCreated().format(PAYMENT_DATE_FORMATTER));
        context.put(VAR_AMOUNT_KEY_HOLDER, paymentAttempt.getAmount().toString());
        context.put(VAR_PRACTICE_KEY_HOLDER, paymentAttempt.getPayment().getPractice().getName());
        context.put(VAR_STATUS_KEY_HOLDER, status);
        context.put(VAR_METHOD_KEY_HOLDER, paymentAttempt.getPaymentMethodLabel());
    }

    private String getType(Payment payment) {
        if (payment.getJobDay() != null) {
            return "Job Day";
        } else if (payment.getJobInterview() != null) {
            return "Interview for Permanent position";
        } else if (payment.getPermanentJobApplication() != null) {
            return "Permanent position";
        }
        return "Unknown";
    }

    private String getStatus(PaymentAttempt paymentAttempt) {
        switch (paymentAttempt.getStatus()) {
            case PaymentAttempt.STATUS_PAID:
                return "successful";
            case PaymentAttempt.STATUS_FAILED:
                return "failed";
            default:
                return paymentAttempt.getStatus();
        }
    }
}
