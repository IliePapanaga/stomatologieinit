package com.cl.mdd.server.core.data.persistent.model.user.professional;

import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "PRACTICE_BLACK_LISTED_PROFESSIONALS")
public class BlackListedProfessional implements Serializable {

    @EmbeddedId
    private BlackListedProfessionalId embeddedId;

    @Column(name = "date", updatable = false, nullable = false)
    @CreatedDate
    private ZonedDateTime date;

    @ManyToOne(optional = false)
    @MapsId("practice_owner_id")
    private Practice practice;

    @ManyToOne(optional = false)
    @MapsId("professional_id")
    private Professional professional;

    private BlackListedProfessional() {
    }

    public BlackListedProfessional(Practice practice, Professional professional) {
        this.practice = practice;
        this.professional = professional;
        this.embeddedId = new BlackListedProfessionalId(practice.getId(), professional.getId());
    }

    public BlackListedProfessionalId getEmbeddedId() {
        return embeddedId;
    }

    public BlackListedProfessional setEmbeddedId(BlackListedProfessionalId embeddedId) {
        this.embeddedId = embeddedId;
        return this;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public BlackListedProfessional setDate(ZonedDateTime date) {
        this.date = date;
        return this;
    }

    public Practice getPractice() {
        return practice;
    }

    public BlackListedProfessional setPractice(Practice practice) {
        this.practice = practice;
        return this;
    }

    public Professional getProfessional() {
        return professional;
    }

    public BlackListedProfessional setProfessional(Professional professional) {
        this.professional = professional;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlackListedProfessional that = (BlackListedProfessional) o;
        return Objects.equals(practice, that.practice) &&
                Objects.equals(professional, that.professional);
    }

    @Override
    public int hashCode() {
        return Objects.hash(practice, professional);
    }

}
