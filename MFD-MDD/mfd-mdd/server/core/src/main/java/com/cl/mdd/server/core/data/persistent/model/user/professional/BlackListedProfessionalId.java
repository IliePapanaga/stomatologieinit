package com.cl.mdd.server.core.data.persistent.model.user.professional;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BlackListedProfessionalId implements Serializable {

    @Column(name = "practice_owner_id", nullable = false)
    private String practiceId;

    @Column(name = "professional_id", nullable = false)
    private String professionalId;

    private BlackListedProfessionalId() {

    }

    public BlackListedProfessionalId(String practiceId, String professionalId) {
        this.practiceId = practiceId;
        this.professionalId = professionalId;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public BlackListedProfessionalId setPracticeId(String practiceId) {
        this.practiceId = practiceId;
        return this;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public BlackListedProfessionalId setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlackListedProfessionalId that = (BlackListedProfessionalId) o;
        return Objects.equals(practiceId, that.practiceId) &&
                Objects.equals(professionalId, that.professionalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(practiceId, professionalId);
    }
}
