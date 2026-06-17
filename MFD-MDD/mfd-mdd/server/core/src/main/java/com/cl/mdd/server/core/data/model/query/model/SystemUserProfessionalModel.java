package com.cl.mdd.server.core.data.model.query.model;

import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * System user pro model.
 * <p/>
 * This is a read only model.
 */
public class SystemUserProfessionalModel {

    private String id;

    private String firstName;

    private String lastName;

    private String speciality;

    private String status;

    private String documentStatus;

    private String phone;

    private BigDecimal rph;

    private Double rating;

    private Long totalFeedback;

    private ZonedDateTime lastEmploymentStartDate;

    private ZonedDateTime lastActivity;

    private int noShow;

    private int cancellations;

    private String approvedByFirstName;

    private String approvedByLastName;

    private ZonedDateTime modifiedDate;

    public SystemUserProfessionalModel() {
    }

    public SystemUserProfessionalModel(String id,
                                       String firstName,
                                       String lastName,
                                       String speciality,
                                       String status,
                                       String documentStatus,
                                       String phone,
                                       BigDecimal rph,
                                       Double rating,
                                       Long totalFeedback,
                                       ZonedDateTime lastEmploymentStartDate,
                                       ZonedDateTime lastActivity,
                                       int noShow,
                                       int cancellations,
                                       String approvedByFirstName,
                                       String approvedByLastName,
                                       ZonedDateTime modifiedDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.speciality = speciality;
        this.status = status;
        this.documentStatus = documentStatus;
        this.phone = phone;
        this.rph = rph;
        this.rating = rating;
        this.totalFeedback = totalFeedback;
        this.lastEmploymentStartDate = lastEmploymentStartDate;
        this.lastActivity = lastActivity;
        this.noShow = noShow;
        this.cancellations = cancellations;
        this.approvedByFirstName = approvedByFirstName;
        this.approvedByLastName = approvedByLastName;
        this.modifiedDate = modifiedDate;
    }

    public String getId() {
        return id;
    }

    public SystemUserProfessionalModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public SystemUserProfessionalModel setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public SystemUserProfessionalModel setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getSpeciality() {
        return speciality;
    }

    public SystemUserProfessionalModel setSpeciality(String speciality) {
        this.speciality = speciality;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public SystemUserProfessionalModel setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public SystemUserProfessionalModel setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public SystemUserProfessionalModel setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public BigDecimal getRph() {
        return rph;
    }

    public SystemUserProfessionalModel setRph(BigDecimal rph) {
        this.rph = rph;
        return this;
    }

    public Double getRating() {
        return rating;
    }

    public SystemUserProfessionalModel setRating(Double rating) {
        this.rating = rating;
        return this;
    }

    public Long getTotalFeedback() {
        return totalFeedback;
    }

    public SystemUserProfessionalModel setTotalFeedback(Long totalFeedback) {
        this.totalFeedback = totalFeedback;
        return this;
    }

    public ZonedDateTime getLastEmploymentStartDate() {
        return lastEmploymentStartDate;
    }

    public SystemUserProfessionalModel setLastEmploymentStartDate(ZonedDateTime lastEmploymentStartDate) {
        this.lastEmploymentStartDate = lastEmploymentStartDate;
        return this;
    }

    public ZonedDateTime getLastActivity() {
        return lastActivity;
    }

    public SystemUserProfessionalModel setLastActivity(ZonedDateTime lastActivity) {
        this.lastActivity = lastActivity;
        return this;
    }

    public int getNoShow() {
        return noShow;
    }

    public SystemUserProfessionalModel setNoShow(int noShow) {
        this.noShow = noShow;
        return this;
    }

    public int getCancellations() {
        return cancellations;
    }

    public SystemUserProfessionalModel setCancellations(int cancellations) {
        this.cancellations = cancellations;
        return this;
    }

    public String getApprovedByFirstName() {
        return approvedByFirstName;
    }

    public SystemUserProfessionalModel setApprovedByFirstName(String approvedByFirstName) {
        this.approvedByFirstName = approvedByFirstName;
        return this;
    }

    public String getApprovedByLastName() {
        return approvedByLastName;
    }

    public SystemUserProfessionalModel setApprovedByLastName(String approvedByLastName) {
        this.approvedByLastName = approvedByLastName;
        return this;
    }

    public ZonedDateTime getModifiedDate() {
        return modifiedDate;
    }

    public SystemUserProfessionalModel setModifiedDate(ZonedDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
        return this;
    }

    protected void setSpecialties(Professional pro) {
        if (Objects.nonNull(pro)) {
            this.speciality = CollectionUtils.emptyIfNull(pro.getSubCategories()).stream()
                    .map(SubCategory::getName)
                    .sorted()
                    .collect(Collectors.joining(","));
        }
    }
}
