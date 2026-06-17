package com.cl.sns.server.core.model.db.common;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Identity {

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

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this,
                ToStringStyle.DEFAULT_STYLE, true, true);
    }
}
