package com.cl.mdd.server.core.manager.questionnaire.processor;

import com.cl.mdd.server.core.data.model.questionnaire.DentistQuestionnaireModel;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor.ItemKey.*;

@Component
public class DentistQuestionnaireProcessor extends BaseQuestionnaireProcessor<DentistQuestionnaireModel> {

    public DentistQuestionnaireProcessor(@Value("${specialties.dds.id}") String relativeCategoryId) {
        super(relativeCategoryId, DentistQuestionnaireModel.class);
    }

    @Override
    protected void updateCustomItems(DentistQuestionnaireModel model, ProfessionalQuestionnaire entity) {
        updateSpecialtyComfort(model.getSpecialtiesComfort(), entity);

        setBoolean(entity, DDS_TEMPORARY_AS_RDH, model.getTemporaryAsRdh());
        setBoolean(entity, CAD_CAM_FAMILIARITY, model.getCadCam());
        setBoolean(entity, INTRA_ORAL_CAM_FAMILIARITY, model.getIntraOralCam());
        setBoolean(entity, PANO_CAM_FAMILIARITY, model.getPano());
        setBoolean(entity, DDS_SURGERY_COMFORT, model.getSurgery());
        setBoolean(entity, DDS_8_HOURS_ON_FEET, model.getHoursOnFeet());
        setBoolean(entity, DDS_8_10_PATIENTS_PER_DAY, model.getPatientsPerDay());
    }

    @Override
    protected DentistQuestionnaireModel create() {
        return new DentistQuestionnaireModel();
    }

    @Override
    protected void convertCustomModel(ProfessionalQuestionnaire entity, DentistQuestionnaireModel model) {
        convertSpecialtyComfort(entity, model.getSpecialtiesComfort());
        
        model.setCadCam(getBoolean(entity, CAD_CAM_FAMILIARITY))
                .setIntraOralCam(getBoolean(entity, INTRA_ORAL_CAM_FAMILIARITY))
                .setTemporaryAsRdh(getBoolean(entity, DDS_TEMPORARY_AS_RDH))
                .setPano(getBoolean(entity, PANO_CAM_FAMILIARITY))
                .setSurgery(getBoolean(entity, DDS_SURGERY_COMFORT))
                .setHoursOnFeet(getBoolean(entity, DDS_8_HOURS_ON_FEET))
                .setPatientsPerDay(getBoolean(entity, DDS_8_10_PATIENTS_PER_DAY));
    }
}
