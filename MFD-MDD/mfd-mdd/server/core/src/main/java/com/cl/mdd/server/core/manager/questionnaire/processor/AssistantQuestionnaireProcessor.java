package com.cl.mdd.server.core.manager.questionnaire.processor;

import com.cl.mdd.server.core.data.model.questionnaire.AssistantQuestionnaireModel;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor.ItemKey.*;

@Component
public class AssistantQuestionnaireProcessor extends BaseQuestionnaireProcessor<AssistantQuestionnaireModel> {

    public AssistantQuestionnaireProcessor(@Value("${specialties.rda.id}") String relativeCategoryId) {
        super(relativeCategoryId, AssistantQuestionnaireModel.class);
    }

    @Override
    protected void updateCustomItems(AssistantQuestionnaireModel model, ProfessionalQuestionnaire entity) {
        updateSpecialtyFamiliarity(model.getSpecialtiesFamiliarity(), entity);
        updateDuties(model.getDuties(), entity);

        setBoolean(entity, CAD_CAM_FAMILIARITY, model.getCadCam());
        setBoolean(entity, IMAGING_3D, model.getImaging3D());
        setBoolean(entity, XRAY_MACHINES, model.getXray());
        setBoolean(entity, INTRA_ORAL_CAM_FAMILIARITY, model.getIntraOralCam());
        setBoolean(entity, PANO_CAM_FAMILIARITY, model.getPano());
        setBoolean(entity, RDA_NOMAD_FAMILIARITY, model.getNomad());
        setBoolean(entity, CROSS_TRAINED, model.getCrossTrained());
    }

    @Override
    protected AssistantQuestionnaireModel create() {
        return new AssistantQuestionnaireModel();
    }

    @Override
    protected void convertCustomModel(ProfessionalQuestionnaire entity, AssistantQuestionnaireModel model) {
        convertSpecialtyFamiliarity(entity, model.getSpecialtiesFamiliarity());
        convertDuties(entity, model.getDuties());

        model.setCadCam(getBoolean(entity, CAD_CAM_FAMILIARITY))
                .setImaging3D(getBoolean(entity, IMAGING_3D))
                .setXray(getBoolean(entity, XRAY_MACHINES))
                .setIntraOralCam(getBoolean(entity, INTRA_ORAL_CAM_FAMILIARITY))
                .setPano(getBoolean(entity, PANO_CAM_FAMILIARITY))
                .setNomad(getBoolean(entity, RDA_NOMAD_FAMILIARITY))
                .setCrossTrained(getBoolean(entity, CROSS_TRAINED));
    }
}
