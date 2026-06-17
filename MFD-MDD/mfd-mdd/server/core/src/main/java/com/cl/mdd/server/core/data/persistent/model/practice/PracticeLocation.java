package com.cl.mdd.server.core.data.persistent.model.practice;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.contact.Contact;

import javax.persistence.*;
import java.time.LocalTime;
import java.time.ZoneId;

@Entity
@Table(name = "PRACTICE_LOCATIONS")
public class PracticeLocation extends AuditedEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "working_hours_from")
    private LocalTime workingHoursFrom;

    @Column(name = "working_hours_to")
    private LocalTime workingHoursTo;

    @ManyToOne
    @JoinColumn(name = "fk_practice_id", nullable = false)
    private Practice practice;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "fk_contact_id", updatable = false)
    private Contact contact;

    @Column(name = "time_zone", nullable = false)
    private ZoneId timeZone;

    @Column(name = "rating")
    private double rating;

    public LocalTime getWorkingHoursFrom() {
        return workingHoursFrom;
    }

    public void setWorkingHoursFrom(LocalTime workingHoursFrom) {
        this.workingHoursFrom = workingHoursFrom;
    }

    public LocalTime getWorkingHoursTo() {
        return workingHoursTo;
    }

    public void setWorkingHoursTo(LocalTime workingHoursTo) {
        this.workingHoursTo = workingHoursTo;
    }

    public Practice getPractice() {
        return practice;
    }

    public void setPractice(Practice practice) {
        this.practice = practice;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    public PracticeLocation setRating(double rating) {
        this.rating = rating;
        return this;
    }

    public double getRating() {
        return rating;
    }
}
