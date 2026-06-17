package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.data.model.questionnaire.DentistQuestionnaireModel;
import com.cl.mdd.server.core.data.model.questionnaire.Questionnaire;
import com.cl.mdd.server.core.data.model.questionnaire.QuestionnaireModel;
import com.cl.mdd.server.core.data.persistent.model.specialty.Category;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.manager.questionnaire.ProfessionalQuestionnaireManager;
import com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor;
import com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessorManager;
import com.cl.mdd.server.core.manager.user.ProfessionalManager;
import com.cl.mdd.server.core.security.SecurityAccess;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProfessionalQuestionnaireServiceImplTest {

    private ProfessionalQuestionnaireServiceImpl service;

    @Mock
    private ProfessionalQuestionnaireManager questionnaireManager;

    @Mock
    private ProfessionalManager professionalManager;

    @Mock
    private QuestionnaireProcessorManager questionnaireProcessorManager;

    @Mock
    private SecurityAccess securityAccess;

    @Mock
    private QuestionnaireProcessor processor;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        service = new ProfessionalQuestionnaireServiceImpl(questionnaireManager, professionalManager, questionnaireProcessorManager);
        ReflectionTestUtils.setField(service, "securityAccess", securityAccess);
        doReturn(processor).when(questionnaireProcessorManager).getConverter(anyString());
        doReturn(processor).when(questionnaireProcessorManager).getConverter((QuestionnaireModel & Questionnaire) any());

        Category category = new Category();
        category.setId("Existing_Category");
        doReturn(category).when(questionnaireProcessorManager).getCategoryByQuestionnaireModel(any());
        Professional professional = new Professional();
        doReturn(professional).when(professionalManager).getOne(anyString());

        doReturn("professional-id").when(securityAccess).currentUserId();
    }

    @Test
    public void editQuestionnaire_whenProfessionalHasNoCategory_throwException() {
        doReturn(false).when(professionalManager).professionalHasCategory(anyString(), anyString());

        expectedException.expect(MDDException.class);

        service.editQuestionnaire(new DentistQuestionnaireModel());
    }

    @Test
    public void editQuestionnaire_whenEditExistingAndQuestionnaireNotFound_throwException() {
        doReturn(true).when(professionalManager).professionalHasCategory(anyString(), anyString());
        doReturn(null).when(questionnaireManager).getById(anyString());

        expectedException.expect(MDDException.class);

        DentistQuestionnaireModel model = new DentistQuestionnaireModel();
        model.setId("non_existing_id");

        service.editQuestionnaire(model);
    }

    @Test
    public void editQuestionnaire_whenTryUpdateCategory_throwException() {
        doReturn(true).when(professionalManager).professionalHasCategory(anyString(), anyString());
        ProfessionalQuestionnaire anotherCategory = new ProfessionalQuestionnaire();
        anotherCategory.setCategory(new Category());
        anotherCategory.getCategory().setId("Other_Category");
        doReturn(anotherCategory).when(questionnaireManager).getById(anyString());

        expectedException.expect(MDDException.class);

        DentistQuestionnaireModel model = new DentistQuestionnaireModel();
        model.setId("existing_id");

        service.editQuestionnaire(model);
    }

    @Test
    public void editQuestionnaire_whenEditExisting_updateAndSave() {
        doReturn(true).when(professionalManager).professionalHasCategory(anyString(), anyString());

        ProfessionalQuestionnaire sameCategory = new ProfessionalQuestionnaire();
        sameCategory.setCategory(new Category());
        sameCategory.getCategory().setId("Existing_Category");
        doReturn(sameCategory).when(questionnaireManager).getById(anyString());

        DentistQuestionnaireModel model = new DentistQuestionnaireModel();
        model.setId("existing_id");

        service.editQuestionnaire(model);

        verify(processor).updateEntity(eq(model), eq(sameCategory));
        verify(questionnaireManager).save(eq(sameCategory));
    }

    @Test
    public void editQuestionnaire_whenNew_setAndSave() {
        doReturn(true).when(professionalManager).professionalHasCategory(anyString(), anyString());

        DentistQuestionnaireModel model = new DentistQuestionnaireModel();

        service.editQuestionnaire(model);

        verify(processor).updateEntity(eq(model), any(ProfessionalQuestionnaire.class));
        ArgumentCaptor<ProfessionalQuestionnaire> questionnaireCaptor = ArgumentCaptor.forClass(ProfessionalQuestionnaire.class);
        verify(questionnaireManager).save(questionnaireCaptor.capture());

        ProfessionalQuestionnaire questionnaire = questionnaireCaptor.getValue();

        assertEquals("Existing_Category", questionnaire.getCategory().getId());
        assertNotNull(questionnaire.getProfessional());
    }

    @Test
    public void get_whenProfessionalIdIsEmpty_getFromSecurityAccess() {
        doReturn(new ProfessionalQuestionnaire()).when(questionnaireManager).getByProfessionalAndCategory(anyString(), anyString());

        service.get(null, "category-id");

        verify(questionnaireManager).getByProfessionalAndCategory(eq("professional-id"), eq("category-id"));
        verify(processor).convertToModel(any(ProfessionalQuestionnaire.class));
    }

    @Test
    public void get_whenProfessionalIsNotFoundById_throwException() {
        doReturn(null).when(professionalManager).findOne(anyString());

        expectedException.expect(MDDException.class);

        service.get("professional-id", "category-id");
    }

    @Test
    public void get_whenProfessionalHasId_findQuestionnaireAndConvert() {
        doReturn(new Professional()).when(professionalManager).findOne(anyString());
        doReturn(new ProfessionalQuestionnaire()).when(questionnaireManager).getByProfessionalAndCategory(anyString(), anyString());

        service.get("professional-id", "category-id");

        verify(questionnaireManager).getByProfessionalAndCategory(eq("professional-id"), eq("category-id"));
        verify(processor).convertToModel(any(ProfessionalQuestionnaire.class));
    }
}