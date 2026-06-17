package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;

import javax.validation.constraints.NotNull;

public class ReplyAlertAttendance {

    @NotNull
    private String temporaryJobPostingApplicationId;

    @NotNull
    @ExpressionConstraint(expression = "{'ARRIVE_IN_A_MINUTE','COUPLE_OF_MINUTES_LATE','ARRIVE_IN_AN_HOUR','CANNOT_COME_TODAY'}.contains(#this)")
    private String template;

    public String getTemporaryJobPostingApplicationId() {
        return temporaryJobPostingApplicationId;
    }

    public void setTemporaryJobPostingApplicationId(String temporaryJobPostingApplicationId) {
        this.temporaryJobPostingApplicationId = temporaryJobPostingApplicationId;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
