package com.cl.mdd.server.core.data.persistent.model.user.professional;

import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "PROFESSIONAL_BLACK_LISTED_LOCATIONS")
public class BlackListedPracticeLocation extends Identifiable implements Serializable {

    @Column(name = "black_listed_date", updatable = false, nullable = false)
    private ZonedDateTime blackListedDate;

    @Column(name = "unblack_listed_date")
    private ZonedDateTime unblackListedDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_professional_id", nullable = false, updatable = false)
    private Professional professional;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_practice_location_id", nullable = false, updatable = false)
    private PracticeLocation location;

    public BlackListedPracticeLocation() {
    }

    public BlackListedPracticeLocation(Professional professional, PracticeLocation practiceLocation) {
        this.professional = professional;
        this.location = practiceLocation;
    }

    public ZonedDateTime getBlackListedDate() {
        return blackListedDate;
    }

    public BlackListedPracticeLocation setBlackListedDate(ZonedDateTime blacklistedDate) {
        this.blackListedDate = blacklistedDate;
        return this;
    }

    public ZonedDateTime getUnblackListedDate() {
        return unblackListedDate;
    }

    public BlackListedPracticeLocation setUnblackListedDate(ZonedDateTime unblacklistedDate) {
        this.unblackListedDate = unblacklistedDate;
        return this;
    }

    public Professional getProfessional() {
        return professional;
    }

    public BlackListedPracticeLocation setProfessional(Professional professional) {
        this.professional = professional;
        return this;
    }

    public PracticeLocation getLocation() {
        return location;
    }

    public BlackListedPracticeLocation setLocation(PracticeLocation location) {
        this.location = location;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlackListedPracticeLocation that = (BlackListedPracticeLocation) o;
        return Objects.equals(professional, that.professional) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(professional, location);
    }

}
