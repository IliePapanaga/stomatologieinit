package com.cl.mdd.server.core.service.user;

import com.cl.mdd.server.core.data.model.questionnaire.*;

public interface ProfessionalQuestionnaireService {

    <T extends QuestionnaireModel & Questionnaire> void editQuestionnaire(T questionnaire);

    <T extends QuestionnaireModel & Questionnaire> T get(String professionalId, String categoryId);
}
