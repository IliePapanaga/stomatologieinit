package com.cl.mdd.server.core.validation.validator.payment;

import com.cl.mdd.server.core.data.model.payment.PaymentOptionsModel;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentDao;
import com.cl.mdd.server.core.data.persistent.model.payment.Payment;
import com.cl.mdd.server.core.validation.constraint.payment.PaymentStatusOptions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PaymentStatusOptionsValidator implements ConstraintValidator<PaymentStatusOptions, PaymentOptionsModel> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${payment.status.options.NEW:MODIFY,COMPLETE,RUN,CANCEL,INSTANT_PAY,PARTIAL}")
    private String operationsForNew;

    @Value("${payment.status.options.PAID:}")
    private String operationsForPaid;

    @Value("${payment.status.options.PENDING:COMPLETE,FAIL}")
    private String operationsForPending;

    @Value("${payment.status.options.FAILED:MODIFY,COMPLETE,RUN,CANCEL,INSTANT_PAY,PARTIAL}")
    private String operationsForFailed;

    @Value("${payment.status.options.FAILED_FINAL:MODIFY,COMPLETE,RUN,CANCEL,INSTANT_PAY,PARTIAL}")
    private String operationsForFailedFinal;

    @Value("${payment.status.options.CANCELED:}")
    private String operationsForCanceled;

    @Value("${payment.status.options.PARTIAL:MODIFY,COMPLETE}")
    private String operationsForPartial;

    private Map<String, Collection<String>> allowed;

    @Autowired
    private PaymentDao paymentDao;

    @Override
    public void initialize(PaymentStatusOptions constraintAnnotation) {
        allowed = new HashMap<>(7);
        allowed.put(Payment.STATUS_NEW, Arrays.asList(StringUtils.split(operationsForNew, ',')));
        allowed.put(Payment.STATUS_PAID, Arrays.asList(StringUtils.split(operationsForPaid, ',')));
        allowed.put(Payment.STATUS_PENDING, Arrays.asList(StringUtils.split(operationsForPending, ',')));
        allowed.put(Payment.STATUS_FAILED, Arrays.asList(StringUtils.split(operationsForFailed, ',')));
        allowed.put(Payment.STATUS_FAILED_FINAL, Arrays.asList(StringUtils.split(operationsForFailedFinal, ',')));
        allowed.put(Payment.STATUS_CANCELED, Arrays.asList(StringUtils.split(operationsForCanceled, ',')));
        allowed.put(Payment.STATUS_PARTIAL, Arrays.asList(StringUtils.split(operationsForPartial, ',')));
    }

    @Override
    public boolean isValid(PaymentOptionsModel value, ConstraintValidatorContext context) {
        Payment payment = paymentDao.findOne(value.getPaymentId());
        if(payment != null) {
            Collection<String> allowedOptions = allowed.get(payment.getStatus());
            logger.debug("payment [{}] is {}, operation {}, allowed {}",
                    payment.getId(), payment.getStatus(), value.getOption(), allowedOptions);
            return allowedOptions.contains(value.getOption());
        }
        return true;
    }
}
