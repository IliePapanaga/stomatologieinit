package com.cl.mdd.server.core.validation.validator.payment;

import com.cl.mdd.server.core.data.model.payment.PaymentInstrumentBase;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentMethodDao;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethod;
import com.cl.mdd.server.core.validation.constraint.payment.PreferredMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PreferredMethodValidator implements ConstraintValidator<PreferredMethod, PaymentInstrumentBase> {

    @Autowired
    private PaymentMethodDao paymentMethodDao;

    @Override
    public void initialize(PreferredMethod constraintAnnotation) {
    }

    @Override
    public boolean isValid(PaymentInstrumentBase instrument, ConstraintValidatorContext context) {
        if(instrument != null && !instrument.isPreferred() && StringUtils.isNotBlank(instrument.getId())) {
            PaymentMethod method = paymentMethodDao.findOne(instrument.getId());
            if(method != null) {
                return paymentMethodDao.findByPracticeAndLabelNotNullOrderByPreferredDesc(method.getPractice()).stream()
                        .anyMatch(m -> !instrument.getId().equals(m.getId()) && m.isPreferred());
            }
        }
        return true;
    }
}
