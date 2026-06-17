package com.cl.mdd.server.core.data.model.certificates;

import com.cl.mdd.server.core.data.model.common.CertificateModel;

import java.time.LocalDate;

public class BaseCertificateDetailsModel implements CertificateDetailsModel {
    private String id;
    private String status;
    private String comment;
    private String licenseNumber;
    private CertificateModel certificate;
    private LocalDate expirationDate;
    private CertificateTypeModel certificateType;

    public BaseCertificateDetailsModel() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public CertificateDetailsModel setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public CertificateDetailsModel setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public CertificateDetailsModel setComment(String comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public String getLicenseNumber() {
        return licenseNumber;
    }

    @Override
    public CertificateDetailsModel setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
        return this;
    }

    @Override
    public CertificateModel getCertificate() {
        return certificate;
    }

    @Override
    public CertificateDetailsModel setCertificate(CertificateModel certificate) {
        this.certificate = certificate;
        return this;
    }

    @Override
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    @Override
    public CertificateDetailsModel setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    @Override
    public CertificateTypeModel getCertificateType() {
        return certificateType;
    }

    @Override
    public CertificateDetailsModel setCertificateType(CertificateTypeModel certificateType) {
        this.certificateType = certificateType;
        return this;
    }
}
