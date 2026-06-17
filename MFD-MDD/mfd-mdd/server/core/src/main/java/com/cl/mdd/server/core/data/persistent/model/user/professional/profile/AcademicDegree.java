package com.cl.mdd.server.core.data.persistent.model.user.professional.profile;

import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ACADEMIC_DEGREES")
public class AcademicDegree extends Identifiable {

    @Column(name = "name", unique = true, updatable = false, nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
