package com.cl.mdd.server.core.data.model.upload.certificates;

import com.cl.mdd.server.core.validation.constraint.certificates.CertificateType;
import org.springframework.web.multipart.MultipartFile;

public class CertificateModel {

    @CertificateType(message = "{certificate.unsupported.type}")
    private String type;
    private MultipartFile file;
    private String licenseNumber;
    private String expirationDate;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

}
