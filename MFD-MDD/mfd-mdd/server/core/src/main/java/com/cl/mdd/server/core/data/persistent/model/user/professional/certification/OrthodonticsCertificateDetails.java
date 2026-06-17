package com.cl.mdd.server.core.data.persistent.model.user.professional.certification;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ORTHODONTICS_CERTIFICATE_DETAILS")
public class OrthodonticsCertificateDetails extends CertificateDetails {

    @Column(name = "speciality")
    private String speciality;

    @Column(name = "education")
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
