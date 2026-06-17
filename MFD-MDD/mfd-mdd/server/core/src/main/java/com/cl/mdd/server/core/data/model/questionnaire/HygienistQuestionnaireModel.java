package com.cl.mdd.server.core.data.model.questionnaire;

public class HygienistQuestionnaireModel extends QuestionnaireModel implements Questionnaire {

    private SpecialtyFamiliarityModel specialtiesFamiliarity = new SpecialtyFamiliarityModel();

    private Boolean nitrousOxide;

    private Boolean anesthetize;

    private Boolean antiMicrobial;

    private Boolean intraOralCam;

    private Boolean pano;

    private Boolean recareAppt;

    public SpecialtyFamiliarityModel getSpecialtiesFamiliarity() {
        return specialtiesFamiliarity;
    }

    public HygienistQuestionnaireModel setSpecialtiesFamiliarity(SpecialtyFamiliarityModel specialtiesFamiliarity) {
        this.specialtiesFamiliarity = specialtiesFamiliarity;
        return this;
    }

    public Boolean getNitrousOxide() {
        return nitrousOxide;
    }

    public HygienistQuestionnaireModel setNitrousOxide(Boolean nitrousOxide) {
        this.nitrousOxide = nitrousOxide;
        return this;
    }

    public Boolean getAnesthetize() {
        return anesthetize;
    }

    public HygienistQuestionnaireModel setAnesthetize(Boolean anesthetize) {
        this.anesthetize = anesthetize;
        return this;
    }

    public Boolean getAntiMicrobial() {
        return antiMicrobial;
    }

    public HygienistQuestionnaireModel setAntiMicrobial(Boolean antiMicrobial) {
        this.antiMicrobial = antiMicrobial;
        return this;
    }

    public Boolean getIntraOralCam() {
        return intraOralCam;
    }

    public HygienistQuestionnaireModel setIntraOralCam(Boolean intraOralCam) {
        this.intraOralCam = intraOralCam;
        return this;
    }

    public Boolean getPano() {
        return pano;
    }

    public HygienistQuestionnaireModel setPano(Boolean pano) {
        this.pano = pano;
        return this;
    }

    public Boolean getRecareAppt() {
        return recareAppt;
    }

    public HygienistQuestionnaireModel setRecareAppt(Boolean recareAppt) {
        this.recareAppt = recareAppt;
        return this;
    }
}
