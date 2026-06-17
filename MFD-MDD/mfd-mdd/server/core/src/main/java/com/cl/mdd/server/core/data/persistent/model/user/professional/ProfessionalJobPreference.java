package com.cl.mdd.server.core.data.persistent.model.user.professional;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.common.BayArea;
import com.cl.mdd.server.core.data.persistent.model.common.WeekDay;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Entity
@Table(name = "PROFESSIONAL_JOB_PREFERENCES")
public class ProfessionalJobPreference extends AuditedEntity {

    @Column(name = "salary_from")
    private BigDecimal salaryFrom;

    @Column(name = "salary_to")
    private BigDecimal salaryTo;

    @Column(name = "hour_rate", nullable = false)
    private BigDecimal desiredRatePerHour;

    @Column(name = "commuting_radius", nullable = false)
    private BigDecimal commutingRadius;

    @Column(name = "looking_for_permanent_job", nullable = false)
    private boolean lookingForPermanentJob = true;

    @Column(name = "looking_for_temporary_job", nullable = false)
    private boolean lookingForTemporaryJob = true;

    @Column(name = "looking_for_full_time_job", nullable = false)
    private boolean lookingForFullTimeJob = true;

    @Column(name = "looking_for_part_time_job", nullable = false)
    private boolean lookingForPartTimeJob = true;

    @Column(name = "evening_working_hours_ok", nullable = false)
    private boolean eveningWorkingHoursOk;

    @Column(name = "willing_to_relocate", nullable = false)
    private boolean willingToRelocate;

    @ManyToMany
    @JoinTable(name = "PROFESSIONAL_JOB_PREFERENCE_AVAILABILITY_DAYS",
            joinColumns = @JoinColumn(name = "fk_professional_job_preference_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_week_day_id"))
    private Set<WeekDay> availabilityDays = newHashSet();

    @ManyToMany
    @JoinTable(name = "PROFESSIONAL_JOB_PREFERENCE_BAY_AREAS",
            joinColumns = @JoinColumn(name = "fk_professional_job_preference_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_bay_area_id"))
    private Set<BayArea> bayAreas = newHashSet();

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    private Professional professional;


    public BigDecimal getSalaryFrom() {
        return salaryFrom;
    }

    public void setSalaryFrom(BigDecimal salaryFrom) {
        this.salaryFrom = salaryFrom;
    }

    public BigDecimal getSalaryTo() {
        return salaryTo;
    }

    public void setSalaryTo(BigDecimal salaryTo) {
        this.salaryTo = salaryTo;
    }

    public BigDecimal getCommutingRadius() {
        return commutingRadius;
    }

    public void setCommutingRadius(BigDecimal commutingRadius) {
        this.commutingRadius = commutingRadius;
    }

    public boolean isLookingForPermanentJob() {
        return lookingForPermanentJob;
    }

    public void setLookingForPermanentJob(boolean lookingForPermanentJob) {
        this.lookingForPermanentJob = lookingForPermanentJob;
    }

    public boolean isLookingForTemporaryJob() {
        return lookingForTemporaryJob;
    }

    public void setLookingForTemporaryJob(boolean lookingForTemporaryJob) {
        this.lookingForTemporaryJob = lookingForTemporaryJob;
    }

    public boolean isLookingForFullTimeJob() {
        return lookingForFullTimeJob;
    }

    public void setLookingForFullTimeJob(boolean lookingForFullTimeJob) {
        this.lookingForFullTimeJob = lookingForFullTimeJob;
    }

    public boolean isLookingForPartTimeJob() {
        return lookingForPartTimeJob;
    }

    public void setLookingForPartTimeJob(boolean lookingForPartTimeJob) {
        this.lookingForPartTimeJob = lookingForPartTimeJob;
    }

    public boolean isEveningWorkingHoursOk() {
        return eveningWorkingHoursOk;
    }

    public void setEveningWorkingHoursOk(boolean eveningWorkingHoursOk) {
        this.eveningWorkingHoursOk = eveningWorkingHoursOk;
    }

    public Set<WeekDay> getAvailabilityDays() {
        return availabilityDays;
    }

    public void setAvailabilityDays(Set<WeekDay> availabilityDays) {
        this.availabilityDays = availabilityDays;
    }

    public Set<BayArea> getBayAreas() {
        return bayAreas;
    }

    public void setBayAreas(Set<BayArea> bayAreas) {
        this.bayAreas = bayAreas;
    }

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
    }

    public boolean isWillingToRelocate() {
        return willingToRelocate;
    }

    public void setWillingToRelocate(boolean willingToRelocate) {
        this.willingToRelocate = willingToRelocate;
    }

    public BigDecimal getDesiredRatePerHour() {
        return desiredRatePerHour;
    }

    public void setDesiredRatePerHour(BigDecimal desiredRatePerHour) {
        this.desiredRatePerHour = desiredRatePerHour;
    }
}
