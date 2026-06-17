package com.cl.mdd.server.core.data.persistent.model.user.professional.profile;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.common.Language;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

@Entity
@Table(name = "PROFESSIONAL_PROFILES")
public class ProfessionalProfile extends AuditedEntity {

    @Column(name = "skill_summary", nullable = false)
    private String skillSummary;

    @ManyToMany
    @JoinTable(name = "PROFESSIONAL_PROFILE_LANGUAGES",
            joinColumns = @JoinColumn(name = "fk_professional_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_language_id"))
    private Set<Language> languages = newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fk_professional_profile_id")
    @OrderColumn(name = "recency")
    private List<ProfessionalWorkExperience> workExperiences = newArrayList();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fk_professional_profile_id")
    @OrderColumn(name = "recency")
    private List<ProfessionalWorkReference> workReferences = newArrayList();

    @ManyToOne
    @JoinColumn(name = "fk_education_id", nullable = false)
    private Education education;

    @ManyToOne
    @JoinColumn(name = "fk_academic_degree_id", nullable = false)
    private AcademicDegree highestAcademicDegree;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    private Professional professional;

    public String getSkillSummary() {
        return skillSummary;
    }

    public void setSkillSummary(String skillSummary) {
        this.skillSummary = skillSummary;
    }

    public Set<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
    }

    public void setEducation(Education education) {
        this.education = education;
    }

    public Education getEducation() {
        return education;
    }

    public void setHighestAcademicDegree(AcademicDegree highestAcademicDegree) {
        this.highestAcademicDegree = highestAcademicDegree;
    }

    public AcademicDegree getHighestAcademicDegree() {
        return highestAcademicDegree;
    }

    public List<ProfessionalWorkExperience> getWorkExperiences() {
        return workExperiences;
    }

    public void setWorkExperiences(List<ProfessionalWorkExperience> workExperiences) {
        this.workExperiences = workExperiences;
    }

    public List<ProfessionalWorkReference> getWorkReferences() {
        return workReferences;
    }

    public void setWorkReferences(List<ProfessionalWorkReference> workReferences) {
        this.workReferences = workReferences;
    }
}
