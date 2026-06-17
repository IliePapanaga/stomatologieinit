package com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.review;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@MappedSuperclass
public abstract class TemporaryJobApplicationReview extends AuditedEntity {

    @OneToOne
    @MapsId
    private TemporaryJobPostingApplication application;

    @Column(name = "comment")
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public TemporaryJobPostingApplication getApplication() {
        return application;
    }

    public void setApplication(TemporaryJobPostingApplication application) {
        this.application = application;
    }
}
