package com.cl.mdd.server.core.data.persistent.model.common;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Identifiable {

    private static final String UUID_GENERATOR = "system-uuid";

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = UUID_GENERATOR)
    @GenericGenerator(name = UUID_GENERATOR, strategy = "uuid2")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
