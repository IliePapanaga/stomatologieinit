package com.cl.mdd.server.core.manager.questionnaire;

import com.cl.mdd.server.core.data.persistent.access.prodessional.ProfessionalQuestionnaireDao;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProfessionalQuestionnaireManagerImplTest {

    private ProfessionalQuestionnaireManagerImpl manager;

    @Mock
    private ProfessionalQuestionnaireDao dao;

    @Before
    public void setUp() throws Exception {
        manager = new ProfessionalQuestionnaireManagerImpl(dao);
    }

    @Test
    public void getById() {
        manager.getById("id");

        verify(dao).findOne("id");
    }

    @Test
    public void getByProfessionalAndCategory() {
        manager.getByProfessionalAndCategory("pro-id", "cat-id");

        verify(dao).findByProfessional_IdAndCategory_Id("pro-id", "cat-id");
    }

    @Test
    public void save() {
        ProfessionalQuestionnaire entity = new ProfessionalQuestionnaire();

        manager.save(entity);

        verify(dao).save(entity);
    }
}