package com.cl.mdd.server.core.data.model.query.model;

public class RequiredCertificateTuple {

    private String type;

    private String status;

    private String certificateId;

    private boolean optional;

    public RequiredCertificateTuple(String type,
                                    String status,
                                    String certificateId,
                                    boolean optional) {
        this.type = type;
        this.status = status;
        this.certificateId = certificateId;
        this.optional = optional;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public boolean isOptional() {
        return optional;
    }
}
