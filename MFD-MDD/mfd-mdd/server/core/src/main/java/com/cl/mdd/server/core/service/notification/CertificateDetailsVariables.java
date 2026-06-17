package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import com.cl.mdd.server.core.service.notification.definition.NotificationContextSupplier;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.cl.mdd.server.core.service.notification.definition.impl.BasePredefinedVariables;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class CertificateDetailsVariables extends BasePredefinedVariables implements NotificationContextSupplier<CertificateDetails>, MessageSourceAware {

    private static final DateTimeFormatter EXPIRE_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/YYYY");

    private MessageSource messageSource;

    private static final String CERTIFICATE_TYPE_PLACEHOLDER = "{certificate.type}";
    private static final String CERTIFICATE_EXPIRE_DATE_PLACEHOLDER = "{certificate.expire.date}";
    private static final String CERTIFICATE_LICENSE_NUMBER_PLACEHOLDER = "{certificate.license.number}";
    private static final String CERTIFICATE_COMMENT_PLACEHOLDER = "{certificate.comment}";

    private static final String NO_LICENSE_NUMBER_PROVIDED_MESSAGE = "(no license number provided)";
    private static final String NO_EXPIRE_DATE_MESSAGE = "(no expiration date provided)";
    private static final String NO_COMMENT_MESSAGE = "(no comment provided)";

    private static final List<Variable> CERTIFICATE_VARIABLES = Arrays.asList(
            variable("notification.var.certificate.type", CERTIFICATE_TYPE_PLACEHOLDER),
            variable("notification.var.certificate.expire.date", CERTIFICATE_EXPIRE_DATE_PLACEHOLDER),
            variable("notification.var.certificate.license.number", CERTIFICATE_LICENSE_NUMBER_PLACEHOLDER),
            variable("notification.var.certificate.comment", CERTIFICATE_COMMENT_PLACEHOLDER)
    );

    @Override
    public List<Variable> expand() {
        return CERTIFICATE_VARIABLES;
    }

    @Override
    public void supply(CertificateDetails certificateDetails, Map<String, String> context) {
        context.put(CERTIFICATE_TYPE_PLACEHOLDER, getCertificateName(certificateDetails));
        context.put(CERTIFICATE_EXPIRE_DATE_PLACEHOLDER, getExpireDate(certificateDetails));
        context.put(CERTIFICATE_LICENSE_NUMBER_PLACEHOLDER, getLicenseNumber(certificateDetails));
        context.put(CERTIFICATE_COMMENT_PLACEHOLDER, getComment(certificateDetails));
    }

    private String getCertificateName(CertificateDetails certificateDetails) {
        String certificateId = certificateDetails.getCertificateType().getId();
        String name = messageSource.getMessage(certificateId + "_CERTIFICATE_NAME", new Object[0], certificateId, Locale.getDefault());
        return StringUtils.isBlank(name) ? certificateId : name;
    }

    private String getLicenseNumber(CertificateDetails certificateDetails) {
        String licenseNumber = certificateDetails.getLicenseNumber();
        return licenseNumber == null
                ? NO_LICENSE_NUMBER_PROVIDED_MESSAGE
                : licenseNumber;
    }

    private String getExpireDate(CertificateDetails certificateDetails) {
        LocalDate expirationDate = certificateDetails.getExpirationDate();
        return expirationDate == null
                ? NO_EXPIRE_DATE_MESSAGE
                : EXPIRE_DATE_FORMATTER.format(certificateDetails.getExpirationDate());
    }

    private String getComment(CertificateDetails certificateDetails) {
        String comment = certificateDetails.getComment();
        return comment == null
                ? NO_COMMENT_MESSAGE
                : comment;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
