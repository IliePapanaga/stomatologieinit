package com.cl.mdd.server.core.data.model.questionnaire;

import com.cl.mdd.server.core.data.model.MDDModel;

public class QuestionnaireModel extends MDDModel {

    protected String id;

    protected Integer yoeInDental;

    protected Integer yoeBySpecialty;

    protected String digitalRadiographySystems;

    protected String managementSoftware;

    public String getId() {
        return id;
    }

    public QuestionnaireModel setId(String id) {
        this.id = id;
        return this;
    }

    public Integer getYoeInDental() {
        return yoeInDental;
    }

    public QuestionnaireModel setYoeInDental(Integer yoeInDental) {
        this.yoeInDental = yoeInDental;
        return this;
    }

    public Integer getYoeBySpecialty() {
        return yoeBySpecialty;
    }

    public QuestionnaireModel setYoeBySpecialty(Integer yoeBySpecialty) {
        this.yoeBySpecialty = yoeBySpecialty;
        return this;
    }

    public String getDigitalRadiographySystems() {
        return digitalRadiographySystems;
    }

    public QuestionnaireModel setDigitalRadiographySystems(String digitalRadiographySystems) {
        this.digitalRadiographySystems = digitalRadiographySystems;
        return this;
    }

    public String getManagementSoftware() {
        return managementSoftware;
    }

    public QuestionnaireModel setManagementSoftware(String managementSoftware) {
        this.managementSoftware = managementSoftware;
        return this;
    }
}
