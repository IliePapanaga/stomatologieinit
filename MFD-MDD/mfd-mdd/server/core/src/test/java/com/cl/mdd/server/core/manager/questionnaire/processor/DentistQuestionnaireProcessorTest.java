package com.cl.mdd.server.core.manager.questionnaire.processor;

import com.cl.mdd.server.core.data.model.questionnaire.DentistQuestionnaireModel;
import com.cl.mdd.server.core.data.model.questionnaire.HygienistQuestionnaireModel;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor.ItemKey.*;
import static com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor.ItemKey.DDS_8_10_PATIENTS_PER_DAY;
import static com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor.ItemKey.DDS_8_HOURS_ON_FEET;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.Assert.*;

public class DentistQuestionnaireProcessorTest extends AbstractQuestionnaireProcessorTest {

    private DentistQuestionnaireProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new DentistQuestionnaireProcessor("categoryId");
    }

    @Test
    public void updateEntity() {
        DentistQuestionnaireModel model = buildRandomModel();
        ProfessionalQuestionnaire questionnaire = new ProfessionalQuestionnaire();

        processor.updateEntity(model, questionnaire);

        assertBaseModel(model, questionnaire);
        assertFieldAndItemEqual(questionnaire, DDS_TEMPORARY_AS_RDH, model.getTemporaryAsRdh());
        assertFieldAndItemEqual(questionnaire, CAD_CAM_FAMILIARITY, model.getCadCam());
        assertFieldAndItemEqual(questionnaire, INTRA_ORAL_CAM_FAMILIARITY, model.getIntraOralCam());
        assertFieldAndItemEqual(questionnaire, PANO_CAM_FAMILIARITY, model.getPano());
        assertFieldAndItemEqual(questionnaire, DDS_SURGERY_COMFORT, model.getSurgery());
        assertFieldAndItemEqual(questionnaire, DDS_8_HOURS_ON_FEET, model.getHoursOnFeet());
        assertFieldAndItemEqual(questionnaire, DDS_8_10_PATIENTS_PER_DAY, model.getPatientsPerDay());

        assertSpecialtyComfortEqual(model.getSpecialtiesComfort(), questionnaire);
    }

    @Test
    public void convertToModel() {
        ProfessionalQuestionnaire questionnaire = buildRandomQuestionnaire();

        DentistQuestionnaireModel result = processor.convertToModel(questionnaire);

        assertBaseModel(result, questionnaire);
        assertFieldAndItemEqual(questionnaire, DDS_TEMPORARY_AS_RDH, result.getTemporaryAsRdh());
        assertFieldAndItemEqual(questionnaire, CAD_CAM_FAMILIARITY, result.getCadCam());
        assertFieldAndItemEqual(questionnaire, INTRA_ORAL_CAM_FAMILIARITY, result.getIntraOralCam());
        assertFieldAndItemEqual(questionnaire, PANO_CAM_FAMILIARITY, result.getPano());
        assertFieldAndItemEqual(questionnaire, DDS_SURGERY_COMFORT, result.getSurgery());
        assertFieldAndItemEqual(questionnaire, DDS_8_HOURS_ON_FEET, result.getHoursOnFeet());
        assertFieldAndItemEqual(questionnaire, DDS_8_10_PATIENTS_PER_DAY, result.getPatientsPerDay());

        assertSpecialtyComfortEqual(result.getSpecialtiesComfort(), questionnaire);
    }

    @Test
    public void relativeCategoryId() {
        assertEquals("categoryId", processor.relativeCategoryId());
    }

    @Test
    public void modelClass() {
        assertEquals(DentistQuestionnaireModel.class, processor.modelClass());
    }

    private ProfessionalQuestionnaire buildRandomQuestionnaire() {
        ProfessionalQuestionnaire questionnaire = new ProfessionalQuestionnaire();
        questionnaire.setItems(new HashSet<>());
        questionnaire.getItems().add(number(YEARS_IN_DENTAL_FIELD));
        questionnaire.getItems().add(number(YEARS_BY_SPECIALTY));
        questionnaire.getItems().add(text(DIGITAL_RADIOGRAPHY_SYSTEMS));
        questionnaire.getItems().add(text(SOFTWARE_EXPERIENCE));
        questionnaire.getItems().add(bool(DDS_TEMPORARY_AS_RDH));
        questionnaire.getItems().add(bool(CAD_CAM_FAMILIARITY));
        questionnaire.getItems().add(bool(INTRA_ORAL_CAM_FAMILIARITY));
        questionnaire.getItems().add(bool(PANO_CAM_FAMILIARITY));
        questionnaire.getItems().add(bool(DDS_SURGERY_COMFORT));
        questionnaire.getItems().add(bool(DDS_8_HOURS_ON_FEET));
        questionnaire.getItems().add(bool(DDS_8_10_PATIENTS_PER_DAY));
        questionnaire.getItems().addAll(specialtyComfort());
        return questionnaire;
    }

    private DentistQuestionnaireModel buildRandomModel() {
        DentistQuestionnaireModel model = new DentistQuestionnaireModel();

        model.setYoeBySpecialty(nextInt());
        model.setYoeBySpecialty(nextInt());
        model.setDigitalRadiographySystems(randomAlphanumeric(100));
        model.setManagementSoftware(randomAlphanumeric(100));
        model.setTemporaryAsRdh(nextBoolean());
        model.setCadCam(nextBoolean());
        model.setIntraOralCam(nextBoolean());
        model.setPano(nextBoolean());
        model.setSurgery(nextBoolean());
        model.setHoursOnFeet(nextBoolean());
        model.setPatientsPerDay(nextBoolean());
        model.setSpecialtiesComfort(specialtyComfortModel());

        return model;
    }
}