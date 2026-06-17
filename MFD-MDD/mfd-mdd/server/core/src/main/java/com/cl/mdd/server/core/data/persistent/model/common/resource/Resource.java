package com.cl.mdd.server.core.data.persistent.model.common.resource;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;

import javax.persistence.*;

import static javax.persistence.InheritanceType.SINGLE_TABLE;

@Entity
@Table(name = "RESOURCES")
@DiscriminatorColumn(name = "RESOURCE_TYPE")
@Inheritance(strategy = SINGLE_TABLE)
public abstract class Resource extends AuditedEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Basic(fetch = FetchType.LAZY) @Lob
    @Column(name = "content", nullable = false)
    private byte[] content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
