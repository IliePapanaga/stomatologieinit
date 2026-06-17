package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.access.user.CertificateTypeDao;
import com.cl.mdd.server.core.validation.annotation.Validator;
import com.cl.mdd.server.core.validation.constraint.certificates.CertificateType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

@Validator
public class CertificateTypeValidator implements ConstraintValidator<CertificateType, String> {

    @Autowired
    private CertificateTypeDao certificateTypeDao;

    @Override
    public void initialize(CertificateType constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return isNull(value) || certificateTypeDao.exists(value);
    }
}
