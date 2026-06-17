package com.cl.mdd.server.core.manager.questionnaire;

import com.cl.mdd.server.core.data.persistent.access.prodessional.ProfessionalQuestionnaireDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import com.cl.mdd.server.core.manager.annotation.Manager;

@Manager
public class ProfessionalQuestionnaireManagerImpl implements ProfessionalQuestionnaireManager {

    private final ProfessionalQuestionnaireDao professionalQuestionnaireDao;

    public ProfessionalQuestionnaireManagerImpl(final ProfessionalQuestionnaireDao professionalQuestionnaireDao) {
        this.professionalQuestionnaireDao = professionalQuestionnaireDao;
    }

    @Override
    public ProfessionalQuestionnaire getById(String id) {
        return professionalQuestionnaireDao.findOne(id);
    }

    @Override
    public ProfessionalQuestionnaire getByProfessionalAndCategory(String professionalId, String categoryId) {
        return professionalQuestionnaireDao.findByProfessional_IdAndCategory_Id(professionalId, categoryId);
    }

    @Override
    public void save(ProfessionalQuestionnaire questionnaire) {
        professionalQuestionnaireDao.save(questionnaire);
    }
}
