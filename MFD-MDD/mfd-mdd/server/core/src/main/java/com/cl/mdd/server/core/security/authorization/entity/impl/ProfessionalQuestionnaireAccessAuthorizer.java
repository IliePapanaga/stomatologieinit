package com.cl.mdd.server.core.security.authorization.entity.impl;

import com.cl.mdd.server.core.data.persistent.access.prodessional.ProfessionalQuestionnaireDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import com.cl.mdd.server.core.security.authorization.annotation.AccessAuthorizer;

import java.util.Objects;

@AccessAuthorizer
public class ProfessionalQuestionnaireAccessAuthorizer extends AbstractEntityAccessAuthorizer<ProfessionalQuestionnaire> {

    private final ProfessionalQuestionnaireDao questionnaireDao;

    public ProfessionalQuestionnaireAccessAuthorizer(final ProfessionalQuestionnaireDao questionnaireDao) {
        this.questionnaireDao = questionnaireDao;
    }

    @Override
    public boolean readAllowed(String id) {
        if (Objects.isNull(id)) {
            return securityAccess.isCurrentProfessional();
        } else {
            return securityAccess.isCurrentSystemUser() || securityAccess.isCurrentPracticeOwner();
        }
    }

    @Override
    public boolean updateAllowed(String id) {
        if (!securityAccess.isCurrentProfessional()) {
            return false;
        }

        if (Objects.isNull(id)) {
            return true;
        } else {
            final String currentUserId = securityAccess.currentUserId();
            Professional professional = questionnaireDao.findProfessionalByQuestionnaireId(id);
            return professional == null || currentUserId.equals(professional.getId());
        }
    }
}
