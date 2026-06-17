package com.cl.mdd.server.core.manager.questionnaire.processor;

import com.cl.mdd.server.core.data.model.questionnaire.HygienistQuestionnaireModel;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor.ItemKey.*;

@Component
public class HygienistQuestionnaireProcessor extends BaseQuestionnaireProcessor<HygienistQuestionnaireModel> {

    public HygienistQuestionnaireProcessor(@Value("${specialties.rdh.id}") String relativeCategoryId) {
        super(relativeCategoryId, HygienistQuestionnaireModel.class);
    }

    @Override
    protected void updateCustomItems(HygienistQuestionnaireModel model, ProfessionalQuestionnaire entity) {
        updateSpecialtyFamiliarity(model.getSpecialtiesFamiliarity(), entity);
        setBoolean(entity, RDH_NO_COMFORT, model.getNitrousOxide());
        setBoolean(entity, RDH_ANESTHETIZE, model.getAnesthetize());
        setBoolean(entity, RDH_ANTI_MICROBIAL, model.getAntiMicrobial());
        setBoolean(entity, INTRA_ORAL_CAM_FAMILIARITY, model.getIntraOralCam());
        setBoolean(entity, PANO_CAM_FAMILIARITY, model.getPano());
        setBoolean(entity, RDH_RECARE, model.getRecareAppt());
    }

    @Override
    protected HygienistQuestionnaireModel create() {
        return new HygienistQuestionnaireModel();
    }

    @Override
    protected void convertCustomModel(ProfessionalQuestionnaire entity, HygienistQuestionnaireModel model) {
        convertSpecialtyFamiliarity(entity, model.getSpecialtiesFamiliarity());
        
        model.setNitrousOxide(getBoolean(entity, RDH_NO_COMFORT))
                .setAnesthetize(getBoolean(entity, RDH_ANESTHETIZE))
                .setAntiMicrobial(getBoolean(entity, RDH_ANTI_MICROBIAL))
                .setIntraOralCam(getBoolean(entity, INTRA_ORAL_CAM_FAMILIARITY))
                .setPano(getBoolean(entity, PANO_CAM_FAMILIARITY))
                .setRecareAppt(getBoolean(entity, RDH_RECARE));
    }
}
