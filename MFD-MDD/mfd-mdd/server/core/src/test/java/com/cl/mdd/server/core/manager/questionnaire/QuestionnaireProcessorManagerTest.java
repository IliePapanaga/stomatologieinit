package com.cl.mdd.server.core.manager.questionnaire;

import com.cl.mdd.server.core.data.model.questionnaire.AssistantQuestionnaireModel;
import com.cl.mdd.server.core.data.model.questionnaire.DentistQuestionnaireModel;
import com.cl.mdd.server.core.data.model.questionnaire.HygienistQuestionnaireModel;
import com.cl.mdd.server.core.data.persistent.access.specialty.CategoryDao;
import com.cl.mdd.server.core.data.persistent.model.specialty.Category;
import com.cl.mdd.server.core.exception.MDDException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireProcessorManagerTest {

    private QuestionnaireProcessorManager manager;

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private QuestionnaireProcessor dentistProcessor;

    @Mock
    private QuestionnaireProcessor assistantProcessor;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        manager = new QuestionnaireProcessorManager(categoryDao, Arrays.asList(dentistProcessor, assistantProcessor));

        doReturn(DentistQuestionnaireModel.class).when(dentistProcessor).modelClass();
        doReturn("DENTIST").when(dentistProcessor).relativeCategoryId();

        doReturn(AssistantQuestionnaireModel.class).when(assistantProcessor).modelClass();
        doReturn("ASSISTANT").when(assistantProcessor).relativeCategoryId();

        doReturn(new Category()).when(categoryDao).getOne(anyString());
    }

    @Test
    public void getConverter_byModel_whenProcessorAvailable_returnProcessor() {
        QuestionnaireProcessor<DentistQuestionnaireModel> result = manager.getConverter(new DentistQuestionnaireModel());

        assertSame(dentistProcessor, result);
    }

    @Test
    public void getConverter_byModel_whenProcessorNotAvailable_throwException() {
        expectedException.expect(MDDException.class);

        manager.getConverter(new HygienistQuestionnaireModel());
    }

    @Test
    public void getConverterByCategory_whenProcessorAvailable_returnProcessor() {
        QuestionnaireProcessor<DentistQuestionnaireModel> result = manager.getConverter("DENTIST");

        assertSame(dentistProcessor, result);
    }

    @Test
    public void getConverterByCategory_whenProcessorNotAvailable_throwException() {
        expectedException.expect(MDDException.class);

        manager.getConverter(new HygienistQuestionnaireModel());
    }

    @Test
    public void getCategoryByQuestionnaireModel_whenProcessorAvailable_returnCategoryFromDao() {
        Category category = manager.getCategoryByQuestionnaireModel(new DentistQuestionnaireModel());

        assertNotNull(category);

        verify(categoryDao).getOne(eq("DENTIST"));
    }

    @Test
    public void getCategoryByQuestionnaireModel_whenProcessorNotAvailable_throwException() {
        expectedException.expect(MDDException.class);

        manager.getCategoryByQuestionnaireModel(new HygienistQuestionnaireModel());
    }
}