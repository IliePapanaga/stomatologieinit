package com.cl.mdd.server.core.validation.validator.payment;

import com.cl.mdd.server.core.data.persistent.access.payment.PaymentMethodDao;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethodCard;
import com.cl.mdd.server.core.validation.constraint.payment.PaymentMethodForOptions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PaymentMethodForOptionsValidator implements ConstraintValidator<PaymentMethodForOptions, String> {

    @Autowired
    private PaymentMethodDao paymentMethodDao;

    @Override
    public void initialize(PaymentMethodForOptions constraintAnnotation) {
    }

    @Override
    public boolean isValid(String id, ConstraintValidatorContext context) {
        return StringUtils.isBlank(id) || paymentMethodDao.findOne(id) instanceof PaymentMethodCard;
    }
}
