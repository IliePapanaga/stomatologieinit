package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;
import com.cl.mdd.server.core.validation.constraint.Language;
import com.cl.mdd.server.core.validation.constraint.SubCategory;
import com.cl.mdd.server.core.validation.constraint.posting.DirectBookingBlacklisted;
import com.cl.mdd.server.core.validation.group.Complexity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@ExpressionConstraint(expression = "@paymentMethodDao.existsForPracticeLocation(#this.practiceLocationId) or @environment.getProperty('job.posting.publish.payment.method.optional', T(java.lang.Boolean), false)",
        message = "{job.posting.practice.location.payment.method}")
@DirectBookingBlacklisted(groups = Complexity.High.class)
public class PublishAbstractJobPosting extends MDDModel {

    @NotNull(message = "{job.posting.name.not.null}")
    private String name;

    @NotNull(message = "{job.posting.start.date.not.null}")
    private LocalDate startDate;

    @NotNull(message = "{job.posting.location.not.null}")
    private String practiceLocationId;

    @SubCategory
    @Size(min = 1, message = "{job.posting.subcategories.size}")
    private Set<String> requiredSubcategories;

    @Language
    @NotEmpty(message = "{job.posting.languages.not.empty}")
    private Set<String> requiredLanguages;

    @Length(max = 255, message = "{job.posting.comment.length}")
    private String comment;

    private String preferredCandidateId;

    private String practiceLocationAddressCity;

    public String getPracticeLocationAddressCity() {
        return practiceLocationAddressCity;
    }

    public PublishAbstractJobPosting setPracticeLocationAddressCity(String practiceLocationAddressCity) {
        this.practiceLocationAddressCity = practiceLocationAddressCity;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getPracticeLocationId() {
        return practiceLocationId;
    }

    public void setPracticeLocationId(String practiceLocationId) {
        this.practiceLocationId = practiceLocationId;
    }

    public Set<String> getRequiredSubcategories() {
        return requiredSubcategories;
    }


    public void setRequiredSubcategories(Set<String> requiredSubcategories) {
        this.requiredSubcategories = requiredSubcategories;
    }

    public Set<String> getRequiredLanguages() {
        return requiredLanguages;
    }

    public void setRequiredLanguages(Set<String> requiredLanguages) {
        this.requiredLanguages = requiredLanguages;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPreferredCandidateId() {
        return preferredCandidateId;
    }

    public void setPreferredCandidateId(String preferredCandidateId) {
        this.preferredCandidateId = preferredCandidateId;
    }
}
