package com.cl.mdd.server.core.data.model.upload.certificates;

public class UploadOsCertificateModel extends UploadCertificateModel {

    private String speciality;
    private String education;

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }
}
