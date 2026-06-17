package com.cl.mdd.server.core.data.model.notification;

import java.util.Objects;

public class NotificationTypeVariableModel {

    private String name;

    private String variable;

    public NotificationTypeVariableModel() {
    }

    public NotificationTypeVariableModel(String variable, String name) {
        this.variable = variable;
        this.name = name;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTypeVariableModel that = (NotificationTypeVariableModel) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(variable, that.variable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, variable);
    }
}