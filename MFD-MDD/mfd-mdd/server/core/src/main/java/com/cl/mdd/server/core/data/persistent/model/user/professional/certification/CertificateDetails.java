package com.cl.mdd.server.core.data.persistent.model.user.professional.certification;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.specialty.CertificateType;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;

import javax.persistence.*;
import java.time.LocalDate;

import static com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails.FK_CERTIFICATE_TYPE_ID;
import static com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails.FK_PROFESSIONAL_ID;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

@Entity
@Table(name = "CERTIFICATE_DETAILS", uniqueConstraints = @UniqueConstraint(columnNames = {FK_PROFESSIONAL_ID, FK_CERTIFICATE_TYPE_ID}))
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "CERTIFICATE_TYPE", length = 100)
public class CertificateDetails extends AuditedEntity {

    /**
     * Transient status, means that such certificate details does not exist yet for specific {@link #certificateType}
     */
    public static final String PENDING = "PENDING";

    // certificate statuses
    public static final String REQUIRES_REVIEW = "REQUIRES_REVIEW";

    public static final String REJECTED = "REJECTED";

    public static final String APPROVED = "APPROVED";

    public static final String EXPIRED = "EXPIRED";

    static final String FK_PROFESSIONAL_ID = "fk_professional_id";

    static final String FK_CERTIFICATE_TYPE_ID = "fk_certificate_type_id";

    @ManyToOne
    @JoinColumn(name = FK_CERTIFICATE_TYPE_ID, nullable = false, updatable = false)
    private CertificateType certificateType;

    @ManyToOne
    @JoinColumn(name = FK_PROFESSIONAL_ID, nullable = false, updatable = false)
    private Professional professional;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "fk_certificate_id")
    private Certificate certificate;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "status", nullable = false)
    private String status = REQUIRES_REVIEW;

    @Column(name = "comment", length = 512)
    private String comment;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public Professional getProfessional() {
        return professional;
    }

    public CertificateDetails setProfessional(Professional professional) {
        this.professional = professional;
        return this;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
