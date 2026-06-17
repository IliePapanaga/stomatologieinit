package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.questionnaire.*;
import com.cl.mdd.server.core.service.user.ProfessionalQuestionnaireService;
import io.leangen.graphql.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfessionalQuestionnaireProvider implements GraphQLProvider {

    @Autowired
    private ProfessionalQuestionnaireService questionnaireService;

    @GraphQLMutation(name = "editDentistQuestionnaire")
    public void editDdsQuestionnaire(@GraphQLNonNull @GraphQLArgument(name = "questionnaire") DentistQuestionnaireModel questionnaire) {
        questionnaireService.editQuestionnaire(questionnaire);
    }

    @GraphQLMutation(name = "editHygienistQuestionnaire")
    public void editRdhQuestionnaire(@GraphQLNonNull @GraphQLArgument(name = "questionnaire") HygienistQuestionnaireModel questionnaire) {
        questionnaireService.editQuestionnaire(questionnaire);
    }

    @GraphQLMutation(name = "editAssistantQuestionnaire")
    public void editRdaQuestionnaire(@GraphQLNonNull @GraphQLArgument(name = "questionnaire") AssistantQuestionnaireModel questionnaire) {
        questionnaireService.editQuestionnaire(questionnaire);
    }

    @GraphQLMutation(name = "editFrontOfficeQuestionnaire")
    public void editFrontOfficeQuestionnaire(@GraphQLNonNull @GraphQLArgument(name = "questionnaire") FrontOfficeQuestionnaireModel questionnaire) {
        questionnaireService.editQuestionnaire(questionnaire);
    }

    @GraphQLQuery(name = "getQuestionnaire")
    public Questionnaire getQuestionnaire(@GraphQLNonNull @GraphQLArgument(name = "category") String category,
                                          @GraphQLArgument(name = "professional") String professional) {
        return questionnaireService.get(professional, category);
    }
}
