package com.cl.mdd.server.core.data.model.certificates;

import com.cl.mdd.server.core.data.model.common.CertificateModel;

import java.time.LocalDate;

public interface CertificateDetailsModel {
    String getId();
    CertificateDetailsModel setId(String id);
    String getStatus();
    CertificateDetailsModel setStatus(String status);
    String getComment();
    CertificateDetailsModel setComment(String comment);
    String getLicenseNumber();
    CertificateDetailsModel setLicenseNumber(String licenseNumber);
    CertificateModel getCertificate();
    CertificateDetailsModel setCertificate(CertificateModel certificate);
    LocalDate getExpirationDate();
    CertificateDetailsModel setExpirationDate(LocalDate expirationDate);
    CertificateTypeModel getCertificateType();
    CertificateDetailsModel setCertificateType(CertificateTypeModel certificateType);
}
