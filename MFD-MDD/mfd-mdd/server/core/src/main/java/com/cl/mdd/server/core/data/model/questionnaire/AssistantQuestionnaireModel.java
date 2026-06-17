package com.cl.mdd.server.core.data.model.questionnaire;

public class AssistantQuestionnaireModel extends QuestionnaireModel implements Questionnaire {

    private SpecialtyFamiliarityModel specialtiesFamiliarity = new SpecialtyFamiliarityModel();

    private DutiesModel duties = new DutiesModel();

    private Boolean cadCam;

    private Boolean imaging3D;

    private Boolean xray;

    private Boolean intraOralCam;

    private Boolean pano;

    private Boolean nomad;

    private Boolean crossTrained;

    public SpecialtyFamiliarityModel getSpecialtiesFamiliarity() {
        return specialtiesFamiliarity;
    }

    public AssistantQuestionnaireModel setSpecialtiesFamiliarity(SpecialtyFamiliarityModel specialtiesFamiliarity) {
        this.specialtiesFamiliarity = specialtiesFamiliarity;
        return this;
    }

    public DutiesModel getDuties() {
        return duties;
    }

    public AssistantQuestionnaireModel setDuties(DutiesModel duties) {
        this.duties = duties;
        return this;
    }

    public Boolean getCadCam() {
        return cadCam;
    }

    public AssistantQuestionnaireModel setCadCam(Boolean cadCam) {
        this.cadCam = cadCam;
        return this;
    }

    public Boolean getImaging3D() {
        return imaging3D;
    }

    public AssistantQuestionnaireModel setImaging3D(Boolean imaging3D) {
        this.imaging3D = imaging3D;
        return this;
    }

    public Boolean getXray() {
        return xray;
    }

    public AssistantQuestionnaireModel setXray(Boolean xray) {
        this.xray = xray;
        return this;
    }

    public Boolean getIntraOralCam() {
        return intraOralCam;
    }

    public AssistantQuestionnaireModel setIntraOralCam(Boolean intraOralCam) {
        this.intraOralCam = intraOralCam;
        return this;
    }

    public Boolean getPano() {
        return pano;
    }

    public AssistantQuestionnaireModel setPano(Boolean pano) {
        this.pano = pano;
        return this;
    }

    public Boolean getNomad() {
        return nomad;
    }

    public AssistantQuestionnaireModel setNomad(Boolean nomad) {
        this.nomad = nomad;
        return this;
    }

    public Boolean getCrossTrained() {
        return crossTrained;
    }

    public AssistantQuestionnaireModel setCrossTrained(Boolean crossTrained) {
        this.crossTrained = crossTrained;
        return this;
    }
}
