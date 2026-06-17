package com.cl.mdd.server.core.data.model.questionnaire;

public class DentistQuestionnaireModel extends QuestionnaireModel implements Questionnaire {

    private SpecialtyComfortLevelModel specialtiesComfort = new SpecialtyComfortLevelModel();

    private Boolean temporaryAsRdh;

    private Boolean cadCam;

    private Boolean intraOralCam;

    private Boolean pano;

    private Boolean surgery;

    private Boolean hoursOnFeet;

    private Boolean patientsPerDay;

    public SpecialtyComfortLevelModel getSpecialtiesComfort() {
        return specialtiesComfort;
    }

    public DentistQuestionnaireModel setSpecialtiesComfort(SpecialtyComfortLevelModel specialtiesComfort) {
        this.specialtiesComfort = specialtiesComfort;
        return this;
    }

    public Boolean getTemporaryAsRdh() {
        return temporaryAsRdh;
    }

    public DentistQuestionnaireModel setTemporaryAsRdh(Boolean temporaryAsRdh) {
        this.temporaryAsRdh = temporaryAsRdh;
        return this;
    }

    public Boolean getCadCam() {
        return cadCam;
    }

    public DentistQuestionnaireModel setCadCam(Boolean cadCam) {
        this.cadCam = cadCam;
        return this;
    }

    public Boolean getIntraOralCam() {
        return intraOralCam;
    }

    public DentistQuestionnaireModel setIntraOralCam(Boolean intraOralCam) {
        this.intraOralCam = intraOralCam;
        return this;
    }

    public Boolean getPano() {
        return pano;
    }

    public DentistQuestionnaireModel setPano(Boolean pano) {
        this.pano = pano;
        return this;
    }

    public Boolean getSurgery() {
        return surgery;
    }

    public DentistQuestionnaireModel setSurgery(Boolean surgery) {
        this.surgery = surgery;
        return this;
    }

    public Boolean getHoursOnFeet() {
        return hoursOnFeet;
    }

    public DentistQuestionnaireModel setHoursOnFeet(Boolean hoursOnFeet) {
        this.hoursOnFeet = hoursOnFeet;
        return this;
    }

    public Boolean getPatientsPerDay() {
        return patientsPerDay;
    }

    public DentistQuestionnaireModel setPatientsPerDay(Boolean patientsPerDay) {
        this.patientsPerDay = patientsPerDay;
        return this;
    }
}

