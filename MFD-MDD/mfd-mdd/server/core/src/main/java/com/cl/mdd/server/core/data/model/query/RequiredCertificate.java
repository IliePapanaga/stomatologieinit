package com.cl.mdd.server.core.data.model.query;

public class RequiredCertificate {

    private String type;

    private String status;

    private String certificateId;

    private boolean optional;

    public String getType() {
        return type;
    }

    public RequiredCertificate setType(String type) {
        this.type = type;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public RequiredCertificate setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public RequiredCertificate setCertificateId(String certificateId) {
        this.certificateId = certificateId;
        return this;
    }

    public RequiredCertificate setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public boolean isOptional() {
        return optional;
    }
}
