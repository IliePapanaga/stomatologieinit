package com.cl.mdd.server.core.data.model.questionnaire;

public class FrontOfficeQuestionnaireModel extends QuestionnaireModel implements Questionnaire {

    private SpecialtyFamiliarityModel specialtiesFamiliarity = new SpecialtyFamiliarityModel();

    private DutiesModel duties = new DutiesModel();

    private Boolean xRaysAndCameraImagesToInsurance;

    private Boolean crossTrained;

    public SpecialtyFamiliarityModel getSpecialtiesFamiliarity() {
        return specialtiesFamiliarity;
    }

    public FrontOfficeQuestionnaireModel setSpecialtiesFamiliarity(SpecialtyFamiliarityModel specialtiesFamiliarity) {
        this.specialtiesFamiliarity = specialtiesFamiliarity;
        return this;
    }

    public DutiesModel getDuties() {
        return duties;
    }

    public FrontOfficeQuestionnaireModel setDuties(DutiesModel duties) {
        this.duties = duties;
        return this;
    }

    public Boolean getxRaysAndCameraImagesToInsurance() {
        return xRaysAndCameraImagesToInsurance;
    }

    public FrontOfficeQuestionnaireModel setxRaysAndCameraImagesToInsurance(Boolean xRaysAndCameraImagesToInsurance) {
        this.xRaysAndCameraImagesToInsurance = xRaysAndCameraImagesToInsurance;
        return this;
    }

    public Boolean getCrossTrained() {
        return crossTrained;
    }

    public FrontOfficeQuestionnaireModel setCrossTrained(Boolean crossTrained) {
        this.crossTrained = crossTrained;
        return this;
    }
}
