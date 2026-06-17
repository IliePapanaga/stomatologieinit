package com.cl.mdd.server.core.validation.validator.multivalue;

import com.cl.mdd.server.core.data.model.upload.certificates.CertificateModel;
import com.cl.mdd.server.core.validation.constraint.certificates.Certificate;
import com.cl.mdd.server.core.validation.validator.CertificateValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

import static java.util.Objects.isNull;

public class CertificateCollectionValidator implements ConstraintValidator<Certificate, Collection<CertificateModel>> {

    @Autowired
    private CertificateValidator certificateValidator;

    @Override
    public void initialize(Certificate constraintAnnotation) {

    }

    @Override
    public boolean isValid(Collection<CertificateModel> certificates, ConstraintValidatorContext context) {
        return isNull(certificates) || CollectionUtils.emptyIfNull(certificates).stream()
                .allMatch(certificate -> certificateValidator.isValid(certificate, context));
    }
}
