package com.cl.mdd.server.core.data.persistent.model.user.professional;

import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import com.google.api.client.util.Sets;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Entity
@DiscriminatorValue("PROFESSIONAL")
public class Professional extends User {

    @Column(name = "notifications_enabled")
    private boolean notificationsEnabled = true;

    @OneToMany(mappedBy = "professional", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BlackListedPracticeLocation> blackListedLocations = newHashSet();

    @Column(name = "comments", length = 1000)
    private String comments;

    @Column(name = "rating")
    private double rating;

    @Column(name = "noShow")
    private int noShow;

    @Column(name = "denials")
    private int denials;

    @OneToOne(mappedBy ="professional")
    private ProfessionalJobPreference professionalJobPreference;

    @OneToMany(mappedBy = "professional", fetch = FetchType.LAZY, targetEntity = NoWork.class)
    private Set<NoWork> noWorks = newHashSet();

    @Column(name = "specialties", length = 512)
    private String specialties;

    @OneToMany(mappedBy = "professional", fetch = FetchType.LAZY)
    private Set<CertificateDetails> certificateDetails = Sets.newHashSet();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "PROFESSIONAL_SUBCATEGORIES",
            joinColumns = {@JoinColumn(name = "fk_professional_id")},
            inverseJoinColumns = {@JoinColumn(name = "fk_subcategory_id")})
    private Set<SubCategory> subCategories = Sets.newHashSet();

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public Professional setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
        return this;
    }

    public Set<BlackListedPracticeLocation> getBlackListedLocations() {
        return blackListedLocations;
    }

    public Professional setBlackListedLocations(Set<BlackListedPracticeLocation> blackListedLocations) {
        this.blackListedLocations = blackListedLocations;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public Professional setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public double getRating() {
        return rating;
    }

    public Professional setRating(double rating) {
        this.rating = rating;
        return this;
    }

    public int getNoShow() {
        return noShow;
    }

    public Professional setNoShow(int noShow) {
        this.noShow = noShow;
        return this;
    }

    public int getDenials() {
        return denials;
    }

    public Professional setDenials(int denials) {
        this.denials = denials;
        return this;
    }

    public ProfessionalJobPreference getProfessionalJobPreference() {
        return professionalJobPreference;
    }

    public Professional setProfessionalJobPreference(ProfessionalJobPreference professionalJobPreference) {
        this.professionalJobPreference = professionalJobPreference;
        return this;
    }

    public Set<NoWork> getNoWorks() {
        return noWorks;
    }

    public Professional setNoWorks(Set<NoWork> noWorks) {
        this.noWorks = noWorks;
        return this;
    }

    public String getSpecialties() {
        return specialties;
    }

    public Professional setSpecialties(String specialties) {
        this.specialties = specialties;
        return this;
    }

    public Set<CertificateDetails> getCertificateDetails() {
        return certificateDetails;
    }

    public Professional setCertificateDetails(Set<CertificateDetails> certificateDetails) {
        this.certificateDetails = certificateDetails;
        return this;
    }

    public Set<SubCategory> getSubCategories() {
        return subCategories;
    }

    public Professional setSubCategories(Set<SubCategory> subCategories) {
        this.subCategories = subCategories;
        return this;
    }
}
