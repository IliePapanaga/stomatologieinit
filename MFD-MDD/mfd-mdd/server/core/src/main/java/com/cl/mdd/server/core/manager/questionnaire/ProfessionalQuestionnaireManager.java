package com.cl.mdd.server.core.manager.questionnaire;

import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import com.cl.mdd.server.core.manager.GenericEntityManager;

public interface ProfessionalQuestionnaireManager extends GenericEntityManager<ProfessionalQuestionnaire> {

    ProfessionalQuestionnaire getById(String id);

    ProfessionalQuestionnaire getByProfessionalAndCategory(String professionalId, String categoryId);

    void save(ProfessionalQuestionnaire questionnaire);
}
