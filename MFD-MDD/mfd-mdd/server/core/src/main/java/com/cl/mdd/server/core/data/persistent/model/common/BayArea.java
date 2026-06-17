package com.cl.mdd.server.core.data.persistent.model.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BAY_AREAS")
public class BayArea extends Identifiable {

    @Column(name = "name", unique = true, updatable = false, nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
