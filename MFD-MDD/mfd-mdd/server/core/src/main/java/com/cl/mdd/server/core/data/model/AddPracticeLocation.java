package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;
import com.cl.mdd.server.core.validation.group.Save;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

public class AddPracticeLocation extends MDDModel {

    @NotNull(groups = Save.class, message = "{practice.location.name.not.null}")
    @Length(min = 3, max = 30, message = "{practice.location.name.length}")
    private String name;

    @ExpressionConstraint(expression = "T(java.time.ZoneId).availableZoneIds.contains(#this)", groups = Save.class, message = "{practice.location.zone.valid}")
    private String timeZone;

    private LocalTime workingHoursFrom;

    private LocalTime workingHoursTo;

    @Valid
    @NotNull(groups = Save.class, message = "{practice.location.contact.not.null}")
    private ContactModel contact;

    public LocalTime getWorkingHoursFrom() {
        return workingHoursFrom;
    }

    public AddPracticeLocation setWorkingHoursFrom(LocalTime workingHoursFrom) {
        this.workingHoursFrom = workingHoursFrom;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public AddPracticeLocation setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public LocalTime getWorkingHoursTo() {
        return workingHoursTo;
    }

    public AddPracticeLocation setWorkingHoursTo(LocalTime workingHoursTo) {
        this.workingHoursTo = workingHoursTo;
        return this;
    }

    public String getName() {
        return name;
    }

    public AddPracticeLocation setName(String name) {
        this.name = name;
        return this;
    }

    public ContactModel getContact() {
        return contact;
    }

    public AddPracticeLocation setContact(ContactModel contact) {
        this.contact = contact;
        return this;
    }
}
