package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;

public class BlackListProfessional extends MDDModel {

    @ExpressionConstraint(expression = "@practiceDao.existsById(#this)", message = "{practice.id.not.exist}")
    private String practiceId;

    @ExpressionConstraint(expression = "@professionalDao.existsById(#this)", message = "{professional.id.not.exist}")
    private String professionalId;

    public String getPracticeId() {
        return practiceId;
    }

    public BlackListProfessional setPracticeId(String practiceId) {
        this.practiceId = practiceId;
        return this;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public BlackListProfessional setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
        return this;
    }
}
