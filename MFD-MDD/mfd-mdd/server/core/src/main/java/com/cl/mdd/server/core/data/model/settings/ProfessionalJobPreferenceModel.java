package com.cl.mdd.server.core.data.model.settings;

import com.cl.mdd.server.core.data.model.MDDModel;
import com.cl.mdd.server.core.validation.constraint.BayArea;
import com.cl.mdd.server.core.validation.constraint.WeekDay;
import com.cl.mdd.server.core.validation.group.Save;
import com.cl.mdd.server.core.validation.group.Update;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

public class ProfessionalJobPreferenceModel extends MDDModel {

    //    @NotNull(groups = Save.class, message = "{jobPreference.salaryFrom.not.null}")
    private BigDecimal salaryFrom;

    //    @NotNull(groups = Save.class, message = "{jobPreference.salaryTo.not.null}")
    private BigDecimal salaryTo;

    @NotNull(groups = {Save.class, Update.class}, message = "{jobPreference.commutingRadius.not.null}")
    @Min(value = 1, message = "{jobPreference.commutingRadius.min}")
    @Max(value = 100, message = "{jobPreference.commutingRadius.max}")
    private BigDecimal commutingRadius;

    @NotNull(groups = {Save.class, Update.class}, message = "{jobPreference.desiredRatePerHour.not.null}")
    private BigDecimal desiredRatePerHour;

    private boolean lookingForPermanentJob;

    private boolean lookingForTemporaryJob;

    private boolean lookingForFullTimeJob;

    private boolean lookingForPartTimeJob;

    private boolean eveningWorkingHoursOk;

    @NotEmpty(groups = {Save.class, Update.class}, message = "{jobPreference.availabilityDays.not.empty}")
    @WeekDay(groups = {Save.class, Update.class})
    private Set<String> availabilityDays;

    @BayArea(groups = {Save.class, Update.class})
    private Set<String> bayAreas;

    private boolean willingToRelocate;

    public BigDecimal getSalaryFrom() {
        return salaryFrom;
    }

    public ProfessionalJobPreferenceModel setSalaryFrom(BigDecimal salaryFrom) {
        this.salaryFrom = salaryFrom;
        return this;
    }

    public BigDecimal getSalaryTo() {
        return salaryTo;
    }

    public ProfessionalJobPreferenceModel setSalaryTo(BigDecimal salaryTo) {
        this.salaryTo = salaryTo;
        return this;
    }

    public BigDecimal getCommutingRadius() {
        return commutingRadius;
    }

    public ProfessionalJobPreferenceModel setCommutingRadius(BigDecimal commutingRadius) {
        this.commutingRadius = commutingRadius;
        return this;
    }

    public boolean getLookingForPermanentJob() {
        return lookingForPermanentJob;
    }

    public ProfessionalJobPreferenceModel setLookingForPermanentJob(boolean lookingForPermanentJob) {
        this.lookingForPermanentJob = lookingForPermanentJob;
        return this;
    }

    public boolean getLookingForTemporaryJob() {
        return lookingForTemporaryJob;
    }

    public ProfessionalJobPreferenceModel setLookingForTemporaryJob(boolean lookingForTemporaryJob) {
        this.lookingForTemporaryJob = lookingForTemporaryJob;
        return this;
    }

    public boolean getLookingForFullTimeJob() {
        return lookingForFullTimeJob;
    }

    public ProfessionalJobPreferenceModel setLookingForFullTimeJob(boolean lookingForFullTimeJob) {
        this.lookingForFullTimeJob = lookingForFullTimeJob;
        return this;
    }

    public boolean getLookingForPartTimeJob() {
        return lookingForPartTimeJob;
    }

    public void setLookingForPartTimeJob(boolean lookingForPartTimeJob) {
        this.lookingForPartTimeJob = lookingForPartTimeJob;
    }

    public boolean getEveningWorkingHoursOk() {
        return eveningWorkingHoursOk;
    }

    public ProfessionalJobPreferenceModel setEveningWorkingHoursOk(boolean eveningWorkingHoursOk) {
        this.eveningWorkingHoursOk = eveningWorkingHoursOk;
        return this;
    }

    public Set<String> getAvailabilityDays() {
        return availabilityDays;
    }

    public ProfessionalJobPreferenceModel setAvailabilityDays(Set<String> availabilityDays) {
        this.availabilityDays = availabilityDays;
        return this;
    }

    public Set<String> getBayAreas() {
        return bayAreas;
    }

    public ProfessionalJobPreferenceModel setBayAreas(Set<String> bayAreas) {
        this.bayAreas = bayAreas;
        return this;
    }

    public boolean getWillingToRelocate() {
        return willingToRelocate;
    }

    public void setWillingToRelocate(boolean willingToRelocate) {
        this.willingToRelocate = willingToRelocate;
    }

    public BigDecimal getDesiredRatePerHour() {
        return desiredRatePerHour;
    }

    public ProfessionalJobPreferenceModel setDesiredRatePerHour(BigDecimal desiredRatePerHour) {
        this.desiredRatePerHour = desiredRatePerHour;
        return this;
    }
}
