package com.cl.mdd.server.core.data.model.payment;

import java.math.BigDecimal;

public class PaymentPermanentInfo extends PaymentInstrumentBase implements PaymentInstrument {

    private String professionalFirstName;
    private String professionalLastName;
    private String specialities;
    private String workingDaysPerWeek;
    private int hoursPerDay;
    private int weeksPerYear;
    private BigDecimal hourlyRate;
    private BigDecimal totalFee;

    public String getProfessionalFirstName() {
        return professionalFirstName;
    }

    public void setProfessionalFirstName(String professionalFirstName) {
        this.professionalFirstName = professionalFirstName;
    }

    public String getProfessionalLastName() {
        return professionalLastName;
    }

    public void setProfessionalLastName(String professionalLastName) {
        this.professionalLastName = professionalLastName;
    }

    public String getSpecialities() {
        return specialities;
    }

    public void setSpecialities(String specialities) {
        this.specialities = specialities;
    }

    public String getWorkingDaysPerWeek() {
        return workingDaysPerWeek;
    }

    public void setWorkingDaysPerWeek(String workingDaysPerWeek) {
        this.workingDaysPerWeek = workingDaysPerWeek;
    }

    public int getHoursPerDay() {
        return hoursPerDay;
    }

    public void setHoursPerDay(int hoursPerDay) {
        this.hoursPerDay = hoursPerDay;
    }

    public int getWeeksPerYear() {
        return weeksPerYear;
    }

    public void setWeeksPerYear(int weeksPerYear) {
        this.weeksPerYear = weeksPerYear;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }
}
