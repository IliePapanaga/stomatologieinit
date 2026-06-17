package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.data.persistent.model.common.embeddable.FullName;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.ProfessionalJobPreference;
import com.google.common.collect.Sets;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public class TemporaryJobPostingApplicationSummary extends MDDModel {

    private String id;

    private String professionalId;
    private String firstName;
    private String lastName;
    private String specialty;
    private String status;
    private BigDecimal rph;
    private String rating;

    private Set<ZonedJobDayModel> workingDays = Sets.newHashSet();


    public TemporaryJobPostingApplicationSummary() {
    }

    public TemporaryJobPostingApplicationSummary(TemporaryJobPostingApplication application) {
        Professional professional = application.getProfessional();
        ProfessionalJobPreference professionalJobPreference = professional.getProfessionalJobPreference();
        FullName name = professional.getContact().getName();
        this.id = application.getId();
        this.professionalId = professional.getId();
        this.firstName = name.getFirst();
        this.lastName = name.getLast();
        this.rph = professionalJobPreference != null ? professionalJobPreference.getDesiredRatePerHour() : null;
        this.rating = String.valueOf(professional.getRating());
        this.status = application.getStatus();
        this.workingDays = application.getJobDays().stream().map(jobDay -> {
            ZonedJobDayModel zonedJobDayModel = new ZonedJobDayModel();
            zonedJobDayModel.setDate(jobDay.getDate());
            zonedJobDayModel.setStartTime(jobDay.getZonedStartDateTime());
            zonedJobDayModel.setEndTime(jobDay.getZonedEndDateTime());
            zonedJobDayModel.setExcluded(jobDay.isExcluded());
            return zonedJobDayModel;
        }).collect(Collectors.toSet());
        this.specialty = professional.getSpecialties();
    }

    public String getId() {
        return id;
    }

    public TemporaryJobPostingApplicationSummary setId(String id) {
        this.id = id;
        return this;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public TemporaryJobPostingApplicationSummary setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public TemporaryJobPostingApplicationSummary setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public TemporaryJobPostingApplicationSummary setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getSpecialty() {
        return specialty;
    }

    public TemporaryJobPostingApplicationSummary setSpecialty(String specialty) {
        this.specialty = specialty;
        return this;
    }

    public BigDecimal getRph() {
        return rph;
    }

    public TemporaryJobPostingApplicationSummary setRph(BigDecimal rph) {
        this.rph = rph;
        return this;
    }

    public String getRating() {
        return rating;
    }

    public TemporaryJobPostingApplicationSummary setRating(String rating) {
        this.rating = rating;
        return this;
    }

    public Set<ZonedJobDayModel> getWorkingDays() {
        return workingDays;
    }

    public TemporaryJobPostingApplicationSummary setWorkingDays(Set<ZonedJobDayModel> workingDays) {
        this.workingDays = workingDays;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public TemporaryJobPostingApplicationSummary setStatus(String status) {
        this.status = status;
        return this;
    }

}
