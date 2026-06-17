package com.cl.mdd.server.core.data.model;

import javax.validation.constraints.NotNull;

public class RejectCertificateDetailsModel extends MDDModel {

    @NotNull(message = "{reject.certificate.id.not.null}")
    private String id;
    @NotNull(message = "{reject.certificate.comment.not.null}")
    private String comment;

    public String getId() {
        return id;
    }

    public RejectCertificateDetailsModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public RejectCertificateDetailsModel setComment(String comment) {
        this.comment = comment;
        return this;
    }

}
