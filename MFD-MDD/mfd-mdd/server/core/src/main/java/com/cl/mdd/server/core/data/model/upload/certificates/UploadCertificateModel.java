package com.cl.mdd.server.core.data.model.upload.certificates;

import com.cl.mdd.server.core.data.model.MDDModel;
import com.cl.mdd.server.core.validation.constraint.certificates.Certificate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class UploadCertificateModel extends MDDModel {
    @Valid
    @NotNull(message = "{certificate.upload.data.not.null}")
    @Certificate
    private List<CertificateModel> certificates;

    public List<CertificateModel> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<CertificateModel> certificates) {
        this.certificates = certificates;
    }
}
