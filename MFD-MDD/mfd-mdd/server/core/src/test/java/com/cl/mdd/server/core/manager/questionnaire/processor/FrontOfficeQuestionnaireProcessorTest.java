package com.cl.mdd.server.core.manager.questionnaire.processor;

import com.cl.mdd.server.core.data.model.questionnaire.DentistQuestionnaireModel;
import com.cl.mdd.server.core.data.model.questionnaire.FrontOfficeQuestionnaireModel;
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

public class FrontOfficeQuestionnaireProcessorTest extends AbstractQuestionnaireProcessorTest {

    private FrontOfficeQuestionnaireProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new FrontOfficeQuestionnaireProcessor("categoryId");
    }

    @Test
    public void updateEntity() {
        FrontOfficeQuestionnaireModel model = buildRandomModel();
        ProfessionalQuestionnaire questionnaire = new ProfessionalQuestionnaire();

        processor.updateEntity(model, questionnaire);

        assertBaseModel(model, questionnaire);
        assertFieldAndItemEqual(questionnaire, FO_XRAYS_TO_INSURANCE, model.getxRaysAndCameraImagesToInsurance());
        assertFieldAndItemEqual(questionnaire, CROSS_TRAINED, model.getCrossTrained());

        assertSpecialtyFamiliarityEqual(model.getSpecialtiesFamiliarity(), questionnaire);
        assertDutiesEqual(model.getDuties(), questionnaire);
    }

    @Test
    public void convertToModel() {
        ProfessionalQuestionnaire questionnaire = buildRandomQuestionnaire();

        FrontOfficeQuestionnaireModel result = processor.convertToModel(questionnaire);

        assertBaseModel(result, questionnaire);
        assertFieldAndItemEqual(questionnaire, FO_XRAYS_TO_INSURANCE, result.getxRaysAndCameraImagesToInsurance());
        assertFieldAndItemEqual(questionnaire, CROSS_TRAINED, result.getCrossTrained());

        assertSpecialtyFamiliarityEqual(result.getSpecialtiesFamiliarity(), questionnaire);
        assertDutiesEqual(result.getDuties(), questionnaire);
    }

    @Test
    public void relativeCategoryId() {
        assertEquals("categoryId", processor.relativeCategoryId());
    }

    @Test
    public void modelClass() {
        assertEquals(FrontOfficeQuestionnaireModel.class, processor.modelClass());
    }

    private ProfessionalQuestionnaire buildRandomQuestionnaire() {
        ProfessionalQuestionnaire questionnaire = new ProfessionalQuestionnaire();
        questionnaire.setItems(new HashSet<>());
        questionnaire.getItems().add(number(YEARS_IN_DENTAL_FIELD));
        questionnaire.getItems().add(number(YEARS_BY_SPECIALTY));
        questionnaire.getItems().add(text(DIGITAL_RADIOGRAPHY_SYSTEMS));
        questionnaire.getItems().add(text(SOFTWARE_EXPERIENCE));
        questionnaire.getItems().add(bool(FO_XRAYS_TO_INSURANCE));
        questionnaire.getItems().add(bool(CROSS_TRAINED));
        questionnaire.getItems().addAll(specialtyFamiliarity());
        questionnaire.getItems().addAll(duties());
        return questionnaire;
    }

    private FrontOfficeQuestionnaireModel buildRandomModel() {
        FrontOfficeQuestionnaireModel model = new FrontOfficeQuestionnaireModel();

        model.setYoeBySpecialty(nextInt());
        model.setYoeBySpecialty(nextInt());
        model.setDigitalRadiographySystems(randomAlphanumeric(100));
        model.setManagementSoftware(randomAlphanumeric(100));
        model.setxRaysAndCameraImagesToInsurance(nextBoolean());
        model.setCrossTrained(nextBoolean());
        model.setSpecialtiesFamiliarity(specialtyFamiliarityModel());
        model.setDuties(dutiesModel());

        return model;
    }
}