package com.cl.mdd.server.core.data.model.notification;

import java.util.List;
import java.util.Objects;

public class NotificationTypeDescriptorModel {

    private String type;

    private String name;

    private String description;

    private List<NotificationTypeVariableModel> variables;

    public NotificationTypeDescriptorModel() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<NotificationTypeVariableModel> getVariables() {
        return variables;
    }

    public void setVariables(List<NotificationTypeVariableModel> variables) {
        this.variables = variables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTypeDescriptorModel that = (NotificationTypeDescriptorModel) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(variables, that.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, description, variables);
    }
}
