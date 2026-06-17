package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.data.model.questionnaire.Questionnaire;
import com.cl.mdd.server.core.data.model.questionnaire.QuestionnaireModel;
import com.cl.mdd.server.core.data.persistent.model.specialty.Category;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.manager.questionnaire.ProfessionalQuestionnaireManager;
import com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor;
import com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessorManager;
import com.cl.mdd.server.core.manager.user.ProfessionalManager;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.service.user.ProfessionalQuestionnaireService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessionalQuestionnaireServiceImpl extends ServiceSupport implements ProfessionalQuestionnaireService {

    private final ProfessionalQuestionnaireManager questionnaireManager;

    private final ProfessionalManager professionalManager;

    private final QuestionnaireProcessorManager questionnaireProcessorManager;

    public ProfessionalQuestionnaireServiceImpl(final ProfessionalQuestionnaireManager questionnaireManager,
                                                final ProfessionalManager professionalManager,
                                                final QuestionnaireProcessorManager questionnaireProcessorManager) {
        this.questionnaireManager = questionnaireManager;
        this.professionalManager = professionalManager;
        this.questionnaireProcessorManager = questionnaireProcessorManager;
    }

    @Override
    @Transactional
    @PreAuthorize("@professionalQuestionnaireAccessAuthorizer.updateAllowed(#model.id)")
    public <T extends QuestionnaireModel & Questionnaire> void editQuestionnaire(T model) {
        ProfessionalQuestionnaire questionnaire = getOrCreateEntity(model);

        QuestionnaireProcessor<T> converter = questionnaireProcessorManager.getConverter(model);
        converter.updateEntity(model, questionnaire);

        questionnaireManager.save(questionnaire);
    }

    private <T extends QuestionnaireModel & Questionnaire> ProfessionalQuestionnaire getOrCreateEntity(T model) {
        Category category = questionnaireProcessorManager.getCategoryByQuestionnaireModel(model);

        final String professionalId = securityAccess.currentUserId();

        if (!professionalManager.professionalHasCategory(professionalId, category.getId())) {
            throw new MDDException("Professional with id \"" + professionalId + "\" has no category \"" + category.getId() + "\" assigned", "E_PROFESSIONAL_HAS_NO_CATEGORY");
        }

        ProfessionalQuestionnaire questionnaire;

        if (StringUtils.isNotBlank(model.getId())) {
            questionnaire = questionnaireManager.getById(model.getId());

            if (questionnaire == null) {
                throw new MDDException("Questionnaire was not found by id", "E_QUESTIONNAIRE_BY_ID_NOT_FOUND");
            }

            if (!StringUtils.equals(category.getId(), questionnaire.getCategory().getId())) {
                throw new MDDException("Questionnaire category cannot be updated", "E_QUESTIONNAIRE_CATEGORY_UPDATE_NOT_ALLOWED");
            }
        } else {
            questionnaire = new ProfessionalQuestionnaire();
            questionnaire.setCategory(category);
            questionnaire.setProfessional(professionalManager.getOne(professionalId));
        }

        return questionnaire;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@professionalQuestionnaireAccessAuthorizer.readAllowed(#professionalId)")
    public <T extends QuestionnaireModel & Questionnaire> T get(String professionalId, String categoryId) {
        if (StringUtils.isBlank(professionalId)) {
            professionalId = securityAccess.currentUserId();
        } else if (professionalManager.findOne(professionalId) == null) {
            throw new MDDException("Professional with id \"" + professionalId + "\" does not exist", "E_PROFESSIONAL_NOT_FOUND");
        }

        ProfessionalQuestionnaire questionnaire = questionnaireManager.getByProfessionalAndCategory(professionalId, categoryId);

        QuestionnaireProcessor<T> converter = questionnaireProcessorManager.getConverter(categoryId);

        return converter.convertToModel(questionnaire);
    }
}
