package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.group.Save;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public class WorkExperienceModel extends MDDModel {

    private String companyName;

    private ZonedDateTime leaveDate;

    @NotNull(groups = Save.class, message = "{workExperience.hireDate.not.null}")
    private ZonedDateTime hireDate;

    @Length(max = 680, groups = Save.class, message = "{workExperience.responsibilities.length}")
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
