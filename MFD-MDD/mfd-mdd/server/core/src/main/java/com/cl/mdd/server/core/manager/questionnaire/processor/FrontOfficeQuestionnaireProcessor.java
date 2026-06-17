package com.cl.mdd.server.core.manager.questionnaire.processor;

import com.cl.mdd.server.core.data.model.questionnaire.FrontOfficeQuestionnaireModel;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor.ItemKey.*;

@Component
public class FrontOfficeQuestionnaireProcessor extends BaseQuestionnaireProcessor<FrontOfficeQuestionnaireModel> {

    public FrontOfficeQuestionnaireProcessor(@Value("${specialties.frontoffice.id}") String relativeCategoryId) {
        super(relativeCategoryId, FrontOfficeQuestionnaireModel.class);
    }

    @Override
    protected void updateCustomItems(FrontOfficeQuestionnaireModel model, ProfessionalQuestionnaire entity) {
        updateSpecialtyFamiliarity(model.getSpecialtiesFamiliarity(), entity);
        updateDuties(model.getDuties(), entity);

        setBoolean(entity, FO_XRAYS_TO_INSURANCE, model.getxRaysAndCameraImagesToInsurance());
        setBoolean(entity, CROSS_TRAINED, model.getCrossTrained());
    }

    @Override
    protected FrontOfficeQuestionnaireModel create() {
        return new FrontOfficeQuestionnaireModel();
    }

    @Override
    protected void convertCustomModel(ProfessionalQuestionnaire entity, FrontOfficeQuestionnaireModel model) {
        convertSpecialtyFamiliarity(entity, model.getSpecialtiesFamiliarity());
        convertDuties(entity, model.getDuties());

        model.setxRaysAndCameraImagesToInsurance(getBoolean(entity, FO_XRAYS_TO_INSURANCE))
                .setCrossTrained(getBoolean(entity, CROSS_TRAINED));
    }
}
