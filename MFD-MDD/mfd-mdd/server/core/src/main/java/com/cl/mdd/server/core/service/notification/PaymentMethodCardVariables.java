package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethodCard;
import com.cl.mdd.server.core.service.notification.definition.NotificationContextSupplier;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.cl.mdd.server.core.service.notification.definition.impl.BasePredefinedVariables;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class PaymentMethodCardVariables extends BasePredefinedVariables implements NotificationContextSupplier<PaymentMethodCard> {

    public static final String CARD_NUMBER_HOLDER = "{card.number}";
    public static final String CARD_EXPIRATION_DATE_HOLDER = "{card.expiry}";

    private static final String EXPIRATION_DATE_FORMATTER = "%s/%s";

    private static final List<Variable> USER_VARS = Arrays.asList(
            variable("notification.var.card.number", CARD_NUMBER_HOLDER),
            variable("notification.var.card.expiry", CARD_EXPIRATION_DATE_HOLDER)
    );

    @Override
    public List<Variable> expand() {
        return USER_VARS;
    }

    @Override
    public void supply(PaymentMethodCard paymentMethodCard, Map<String, String> context) {
        String expiration = getExpiration(paymentMethodCard);

        context.put(CARD_NUMBER_HOLDER, paymentMethodCard.getNumber());
        context.put(CARD_EXPIRATION_DATE_HOLDER, expiration);
    }

    private String getExpiration(PaymentMethodCard paymentMethodCard) {
        String cardExpiration = paymentMethodCard.getExpiration();
        String month = cardExpiration.substring(0, 2);
        String year = cardExpiration.substring(2);
        return String.format(EXPIRATION_DATE_FORMATTER, month, year);
    }
}
