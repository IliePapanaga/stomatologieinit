package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.model.upload.certificates.CertificateModel;
import com.cl.mdd.server.core.data.persistent.access.user.CertificateDetailsDao;
import com.cl.mdd.server.core.data.persistent.model.specialty.CertificateType;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import com.cl.mdd.server.core.security.SecurityAccess;
import com.cl.mdd.server.core.validation.annotation.Validator;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

import static com.cl.mdd.server.core.data.persistent.model.specialty.CertificateType.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Validator
public class CertificateValidator implements ConstraintValidator<com.cl.mdd.server.core.validation.constraint.certificates.Certificate, CertificateModel> {
    private static final Map<String, String> OPTIONAL_ASSISTANT_CERTIFICATE_LICENSE_NUMBER_TO_ERROR_MESSAGES = ImmutableMap.of(
            ENDODONTIC_ASSISTANT, "{endodontic.assistant.certificate.number.not.null}",
            ORAL_SURGERY_ASSISTANT, "{oral.surgery.assistant.certificate.number.not.null}",
            ORTHODONTIC_ASSISTANT, "{orthodontic.assistant.certificate.number.not.null}",
            PEDODONTIC_ASSISTANT, "{pedodontic.assistant.certificate.number.not.null}",
            PERIODONTAL_ASSISTANT, "{periodontal.assistant.certificate.number.not.null}"
    );

    private static final Map<String, String> OPTIONAL_ASSISTANT_CERTIFICATE_EXPIRATION_TO_ERROR_MESSAGES = ImmutableMap.of(
            ENDODONTIC_ASSISTANT, "{endodontic.assistant.certificate.expiration.not.null}",
            ORAL_SURGERY_ASSISTANT, "{oral.surgery.assistant.certificate.expiration.not.null}",
            ORTHODONTIC_ASSISTANT, "{orthodontic.assistant.certificate.expiration.not.null}",
            PEDODONTIC_ASSISTANT, "{pedodontic.assistant.certificate.expiration.not.null}",
            PERIODONTAL_ASSISTANT, "{periodontal.assistant.certificate.expiration.not.null}"
    );

    @Autowired
    private CertificateDetailsDao certificateDetailsDao;

    @Autowired
    private SecurityAccess securityAccess;

    @Override
    public void initialize(com.cl.mdd.server.core.validation.constraint.certificates.Certificate constraintAnnotation) {
    }

    @Override
    public boolean isValid(CertificateModel model, ConstraintValidatorContext context) {
        if (nonNull(model) && StringUtils.isNotBlank(model.getType())) {
            switch (model.getType()) {
                case RDA:
                case RDAEF:
                case RDAEF_1:
                case RDAEF_2:
                    return validateRDA(model, context);
                case RDH:
                case RDHAP:
                    return validateRDH(model, context);
                case DDS_OR_DMD:
                    return validateDDS(model, context);
                case DEA:
                    return validateDEA(model, context);
                case XRAY:
                    return constrainMessage(isNull(model.getFile()), "{xray.certificate.file.not.null}", context);
                case CPR:
                    return validateCPR(model, context);
                case LIABILITY:
                    return validateLiability(model, context);
                case NPI:
                    return constrainMessage(isNull(model.getLicenseNumber()), "{npi.certificate.number.not.null}", context);
                case ENDODONTIC_ASSISTANT:
                case ORAL_SURGERY_ASSISTANT:
                case ORTHODONTIC_ASSISTANT:
                case PEDODONTIC_ASSISTANT:
                case PERIODONTAL_ASSISTANT:
                    return validateOptionalAssistant(model, context);
                default:
                    return true;
            }
        }
        return true;
    }

    protected boolean validateOptionalAssistant(CertificateModel model, ConstraintValidatorContext context) {
        boolean license = constrainMessage(isNull(model.getLicenseNumber()),
                OPTIONAL_ASSISTANT_CERTIFICATE_LICENSE_NUMBER_TO_ERROR_MESSAGES.get(model.getType()), context);
        boolean expiration = constrainMessage(isNull(model.getExpirationDate()),
                OPTIONAL_ASSISTANT_CERTIFICATE_EXPIRATION_TO_ERROR_MESSAGES.get(model.getType()), context);
        return license && expiration;
    }

    protected boolean validateDEA(CertificateModel model, ConstraintValidatorContext context) {
        boolean file = constrainMessage(isNull(model.getFile()), "{dea.certificate.file.not.null}", context);
        boolean license = constrainMessage(isNull(model.getLicenseNumber()), "{dea.certificate.number.not.null}", context);
        boolean expiration = constrainMessage(isNull(model.getExpirationDate()), "{dea.certificate.expiration.not.null}", context);
        return file && license && expiration;
    }

    protected boolean validateDDS(CertificateModel model, ConstraintValidatorContext context) {
        boolean file = constrainMessage(isNull(model.getFile()), "{dds.certificate.file.not.null}", context);
        boolean license = constrainMessage(isNull(model.getLicenseNumber()), "{dds.certificate.number.not.null}", context);
        return file && license;
    }

    protected boolean validateRDH(CertificateModel model, ConstraintValidatorContext context) {
        boolean file = constrainMessage(isNull(model.getFile()), "{rdh.certificate.file.not.null}", context);
        boolean license = constrainMessage(isNull(model.getLicenseNumber()), "{rdh.certificate.number.not.null}", context);
        boolean expiration = constrainMessage(isNull(model.getExpirationDate()), "{rdh.certificate.expiration.not.null}", context);
        return file && license && expiration;
    }

    protected boolean validateRDA(CertificateModel model, ConstraintValidatorContext context) {
        boolean file = constrainMessage(isNull(model.getFile()), "{rda.certificate.file.not.null}", context);
        boolean license = constrainMessage(isNull(model.getLicenseNumber()), "{rda.certificate.number.not.null}", context);
        boolean expiration = constrainMessage(isNull(model.getExpirationDate()), "{rda.certificate.expiration.not.null}", context);
        return file && license && expiration;
    }

    protected boolean validateLiability(CertificateModel model, ConstraintValidatorContext context) {
        CertificateDetails liabilityCertificate = findCertificateByType(CertificateType.LIABILITY);
        if (isNull(liabilityCertificate)) {
            boolean file = constrainMessage(isNull(model.getFile()), "{liability.certificate.file.not.null}", context);
            boolean expiration = constrainMessage(isNull(model.getExpirationDate()), "{liability.certificate.expiration.not.null}", context);
            return file && expiration;
        }
        return true;
    }

    protected boolean validateCPR(CertificateModel model, ConstraintValidatorContext context) {
        CertificateDetails cprCertificate = findCertificateByType(CertificateType.CPR);
        if (isNull(cprCertificate)) {
            boolean file = constrainMessage(isNull(model.getFile()), "{crp.certificate.certificate.not.null}", context);
            boolean expiration = constrainMessage(isNull(model.getExpirationDate()), "{crp.certificate.date.not.null}", context);
            return file && expiration;
        }
        return true;
    }

    protected boolean constrainMessage(boolean isValid, String message, ConstraintValidatorContext context) {
        if (isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
        return !isValid;
    }

    protected CertificateDetails findCertificateByType(String type) {
        return certificateDetailsDao.findByProfessionalIdAndCertificateType(securityAccess.currentUserId(), type);
    }

}
