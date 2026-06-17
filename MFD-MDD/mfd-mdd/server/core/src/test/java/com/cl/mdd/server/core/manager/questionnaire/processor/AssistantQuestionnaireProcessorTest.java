package com.cl.mdd.server.core.manager.questionnaire.processor;

import com.cl.mdd.server.core.data.model.questionnaire.AssistantQuestionnaireModel;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor.ItemKey.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.Assert.*;

public class AssistantQuestionnaireProcessorTest extends AbstractQuestionnaireProcessorTest {

    private AssistantQuestionnaireProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new AssistantQuestionnaireProcessor("categoryId");
    }

    @Test
    public void updateEntity() {
        AssistantQuestionnaireModel model = buildRandomModel();
        ProfessionalQuestionnaire questionnaire = new ProfessionalQuestionnaire();

        processor.updateEntity(model, questionnaire);

        assertBaseModel(model, questionnaire);
        assertFieldAndItemEqual(questionnaire, CAD_CAM_FAMILIARITY, model.getCadCam());
        assertFieldAndItemEqual(questionnaire, IMAGING_3D, model.getImaging3D());
        assertFieldAndItemEqual(questionnaire, XRAY_MACHINES, model.getXray());
        assertFieldAndItemEqual(questionnaire, INTRA_ORAL_CAM_FAMILIARITY, model.getIntraOralCam());
        assertFieldAndItemEqual(questionnaire, PANO_CAM_FAMILIARITY, model.getPano());
        assertFieldAndItemEqual(questionnaire, RDA_NOMAD_FAMILIARITY, model.getNomad());
        assertFieldAndItemEqual(questionnaire, CROSS_TRAINED, model.getCrossTrained());

        assertSpecialtyFamiliarityEqual(model.getSpecialtiesFamiliarity(), questionnaire);
        assertDutiesEqual(model.getDuties(), questionnaire);
    }

    @Test
    public void convertToModel() {
        ProfessionalQuestionnaire questionnaire = buildRandomQuestionnaire();

        AssistantQuestionnaireModel result = processor.convertToModel(questionnaire);

        assertBaseModel(result, questionnaire);
        assertFieldAndItemEqual(questionnaire, CAD_CAM_FAMILIARITY, result.getCadCam());
        assertFieldAndItemEqual(questionnaire, IMAGING_3D, result.getImaging3D());
        assertFieldAndItemEqual(questionnaire, XRAY_MACHINES, result.getXray());
        assertFieldAndItemEqual(questionnaire, INTRA_ORAL_CAM_FAMILIARITY, result.getIntraOralCam());
        assertFieldAndItemEqual(questionnaire, PANO_CAM_FAMILIARITY, result.getPano());
        assertFieldAndItemEqual(questionnaire, RDA_NOMAD_FAMILIARITY, result.getNomad());
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
        assertEquals(AssistantQuestionnaireModel.class, processor.modelClass());
    }

    private ProfessionalQuestionnaire buildRandomQuestionnaire() {
        ProfessionalQuestionnaire questionnaire = new ProfessionalQuestionnaire();
        questionnaire.setItems(new HashSet<>());
        questionnaire.getItems().add(number(YEARS_IN_DENTAL_FIELD));
        questionnaire.getItems().add(number(YEARS_BY_SPECIALTY));
        questionnaire.getItems().add(text(DIGITAL_RADIOGRAPHY_SYSTEMS));
        questionnaire.getItems().add(text(SOFTWARE_EXPERIENCE));
        questionnaire.getItems().add(bool(CAD_CAM_FAMILIARITY));
        questionnaire.getItems().add(bool(IMAGING_3D));
        questionnaire.getItems().add(bool(XRAY_MACHINES));
        questionnaire.getItems().add(bool(INTRA_ORAL_CAM_FAMILIARITY));
        questionnaire.getItems().add(bool(PANO_CAM_FAMILIARITY));
        questionnaire.getItems().add(bool(RDA_NOMAD_FAMILIARITY));
        questionnaire.getItems().add(bool(CROSS_TRAINED));
        questionnaire.getItems().addAll(specialtyFamiliarity());
        questionnaire.getItems().addAll(duties());
        return questionnaire;
    }

    private AssistantQuestionnaireModel buildRandomModel() {
        AssistantQuestionnaireModel model = new AssistantQuestionnaireModel();

        model.setYoeBySpecialty(nextInt());
        model.setYoeBySpecialty(nextInt());
        model.setDigitalRadiographySystems(randomAlphanumeric(100));
        model.setManagementSoftware(randomAlphanumeric(100));
        model.setCadCam(nextBoolean());
        model.setImaging3D(nextBoolean());
        model.setXray(nextBoolean());
        model.setIntraOralCam(nextBoolean());
        model.setPano(nextBoolean());
        model.setNomad(nextBoolean());
        model.setCrossTrained(nextBoolean());
        model.setSpecialtiesFamiliarity(specialtyFamiliarityModel());
        model.setDuties(dutiesModel());

        return model;
    }
}