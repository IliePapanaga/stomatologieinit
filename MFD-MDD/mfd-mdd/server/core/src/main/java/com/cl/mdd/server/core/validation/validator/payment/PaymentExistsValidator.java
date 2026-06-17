package com.cl.mdd.server.core.validation.validator.payment;

import com.cl.mdd.server.core.data.persistent.access.payment.PaymentDao;
import com.cl.mdd.server.core.validation.constraint.payment.PaymentExists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PaymentExistsValidator implements ConstraintValidator<PaymentExists, String> {

    @Autowired
    private PaymentDao paymentDao;

    @Override
    public void initialize(PaymentExists constraintAnnotation) {
    }

    @Override
    public boolean isValid(String id, ConstraintValidatorContext context) {
        return StringUtils.isBlank(id) || paymentDao.exists(id);
    }
}
