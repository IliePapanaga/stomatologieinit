package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;

public class BlackListPracticeLocation extends MDDModel {

    @ExpressionConstraint(expression = " @professionalDao.existsById(#this)", message = "{professional.id.not.exist}")
    private String professionalId;

    @ExpressionConstraint(expression = "@practiceLocationDao.existsById(#this)", message = "{practice.location.id.not.exist}")
    private String practiceLocationId;

    public String getProfessionalId() {
        return professionalId;
    }

    public BlackListPracticeLocation setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
        return this;
    }

    public String getPracticeLocationId() {
        return practiceLocationId;
    }

    public BlackListPracticeLocation setPracticeLocationId(String practiceLocationId) {
        this.practiceLocationId = practiceLocationId;
        return this;
    }
}
