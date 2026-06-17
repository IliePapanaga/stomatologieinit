package com.cl.mdd.server.core.data.persistent.model.user.professional.profile;

import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "PROFESSIONAL_WORK_EXPERIENCES")
public class ProfessionalWorkExperience extends Identifiable {

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "leave_date")
    private ZonedDateTime leaveDate;

    @Column(name = "hire_date", nullable = false)
    private ZonedDateTime hireDate;

    @Column(name = "responsibilities", length = 680)
    private String responsibilities;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public ZonedDateTime getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(ZonedDateTime leaveDate) {
        this.leaveDate = leaveDate;
    }

    public ZonedDateTime getHireDate() {
        return hireDate;
    }

    public void setHireDate(ZonedDateTime hireDate) {
        this.hireDate = hireDate;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }
}
