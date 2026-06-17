package com.cl.mdd.server.core.data.model.certificates;

public class CertificateTypeModel {

    private String id;
    private boolean optional;

    public String getId() {
        return id;
    }

    public CertificateTypeModel setId(String id) {
        this.id = id;
        return this;
    }

    public boolean isOptional() {
        return optional;
    }

    public CertificateTypeModel setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }
}
