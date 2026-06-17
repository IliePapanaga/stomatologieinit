package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.AcademicDegree;
import com.cl.mdd.server.core.validation.constraint.Education;
import com.cl.mdd.server.core.validation.constraint.Language;
import com.cl.mdd.server.core.validation.group.Save;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

public class ProfessionalProfileModel extends MDDModel {

    @NotNull(groups = Save.class, message = "{professional.profile.skillSummary.not.null}")
    @Length(max = 255, groups = Save.class, message = "{professional.profile.skillSummary.length}")
    private String skillSummary;

    @NotNull(groups = Save.class, message = "{professional.profile.education.not.null}")
    @Education(groups = Save.class)
    private String education;

    @NotNull(groups = Save.class, message = "{professional.profile.highestDegree.not.null}")
    @AcademicDegree(groups = Save.class)
    private String highestDegree;

    @NotEmpty(groups = Save.class, message = "{professional.profile.languages.not.empty}")
    @Language(groups = Save.class)
    private Set<String> languages = newHashSet();

    @Valid
    @Size(max = 3, message = "{workExperience.size}")
    private List<WorkExperienceModel> workExperiences = newArrayList();

    @Valid
    @Size(max = 3, message = "{workReference.size}")
    private List<WorkReferenceModel> workReferences = newArrayList();

    public String getSkillSummary() {
        return skillSummary;
    }

    public void setSkillSummary(String skillSummary) {
        this.skillSummary = skillSummary;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getHighestDegree() {
        return highestDegree;
    }

    public void setHighestDegree(String highestDegree) {
        this.highestDegree = highestDegree;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public List<WorkExperienceModel> getWorkExperiences() {
        return workExperiences;
    }

    public void setWorkExperiences(List<WorkExperienceModel> workExperiences) {
        this.workExperiences = workExperiences;
    }

    public List<WorkReferenceModel> getWorkReferences() {
        return workReferences;
    }

    public void setWorkReferences(List<WorkReferenceModel> workReferences) {
        this.workReferences = workReferences;
    }
}
