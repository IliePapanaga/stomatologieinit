package com.cl.mdd.server.core.manager.questionnaire.processor;

import com.cl.mdd.server.core.data.model.questionnaire.HygienistQuestionnaireModel;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor.ItemKey.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.Assert.*;

public class HygienistQuestionnaireProcessorTest extends AbstractQuestionnaireProcessorTest {

    private HygienistQuestionnaireProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new HygienistQuestionnaireProcessor("categoryId");
    }

    @Test
    public void updateEntity() {
        HygienistQuestionnaireModel model = buildRandomModel();
        ProfessionalQuestionnaire questionnaire = new ProfessionalQuestionnaire();

        processor.updateEntity(model, questionnaire);

        assertBaseModel(model, questionnaire);
        assertFieldAndItemEqual(questionnaire, RDH_NO_COMFORT, model.getNitrousOxide());
        assertFieldAndItemEqual(questionnaire, RDH_ANESTHETIZE, model.getAnesthetize());
        assertFieldAndItemEqual(questionnaire, RDH_ANTI_MICROBIAL, model.getAntiMicrobial());
        assertFieldAndItemEqual(questionnaire, INTRA_ORAL_CAM_FAMILIARITY, model.getIntraOralCam());
        assertFieldAndItemEqual(questionnaire, PANO_CAM_FAMILIARITY, model.getPano());
        assertFieldAndItemEqual(questionnaire, RDH_RECARE, model.getRecareAppt());
        assertSpecialtyFamiliarityEqual(model.getSpecialtiesFamiliarity(), questionnaire);
    }

    @Test
    public void convertToModel() {
        ProfessionalQuestionnaire questionnaire = buildRandomQuestionnaire();

        HygienistQuestionnaireModel result = processor.convertToModel(questionnaire);

        assertBaseModel(result, questionnaire);
        assertFieldAndItemEqual(questionnaire, RDH_NO_COMFORT, result.getNitrousOxide());
        assertFieldAndItemEqual(questionnaire, RDH_ANESTHETIZE, result.getAnesthetize());
        assertFieldAndItemEqual(questionnaire, RDH_ANTI_MICROBIAL, result.getAntiMicrobial());
        assertFieldAndItemEqual(questionnaire, INTRA_ORAL_CAM_FAMILIARITY, result.getIntraOralCam());
        assertFieldAndItemEqual(questionnaire, PANO_CAM_FAMILIARITY, result.getPano());
        assertFieldAndItemEqual(questionnaire, RDH_RECARE, result.getRecareAppt());

        assertSpecialtyFamiliarityEqual(result.getSpecialtiesFamiliarity(), questionnaire);
    }

    @Test
    public void relativeCategoryId() {
        assertEquals("categoryId", processor.relativeCategoryId());
    }

    @Test
    public void modelClass() {
        assertEquals(HygienistQuestionnaireModel.class, processor.modelClass());
    }

    private ProfessionalQuestionnaire buildRandomQuestionnaire() {
        ProfessionalQuestionnaire questionnaire = new ProfessionalQuestionnaire();
        questionnaire.setItems(new HashSet<>());
        questionnaire.getItems().add(number(YEARS_IN_DENTAL_FIELD));
        questionnaire.getItems().add(number(YEARS_BY_SPECIALTY));
        questionnaire.getItems().add(text(DIGITAL_RADIOGRAPHY_SYSTEMS));
        questionnaire.getItems().add(text(SOFTWARE_EXPERIENCE));
        questionnaire.getItems().add(bool(RDH_NO_COMFORT));
        questionnaire.getItems().add(bool(RDH_ANESTHETIZE));
        questionnaire.getItems().add(bool(RDH_ANTI_MICROBIAL));
        questionnaire.getItems().add(bool(INTRA_ORAL_CAM_FAMILIARITY));
        questionnaire.getItems().add(bool(PANO_CAM_FAMILIARITY));
        questionnaire.getItems().add(bool(RDH_RECARE));
        questionnaire.getItems().addAll(specialtyFamiliarity());
        return questionnaire;
    }

    private HygienistQuestionnaireModel buildRandomModel() {
        HygienistQuestionnaireModel model = new HygienistQuestionnaireModel();

        model.setYoeBySpecialty(nextInt());
        model.setYoeBySpecialty(nextInt());
        model.setDigitalRadiographySystems(randomAlphanumeric(100));
        model.setManagementSoftware(randomAlphanumeric(100));
        model.setNitrousOxide(nextBoolean());
        model.setAnesthetize(nextBoolean());
        model.setAntiMicrobial(nextBoolean());
        model.setIntraOralCam(nextBoolean());
        model.setPano(nextBoolean());
        model.setRecareAppt(nextBoolean());
        model.setSpecialtiesFamiliarity(specialtyFamiliarityModel());

        return model;
    }
}