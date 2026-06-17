package com.cl.mdd.server.core.manager.questionnaire.processor;

import com.cl.mdd.server.core.data.model.questionnaire.DutiesModel;
import com.cl.mdd.server.core.data.model.questionnaire.QuestionnaireModel;
import com.cl.mdd.server.core.data.model.questionnaire.SpecialtyComfortLevelModel;
import com.cl.mdd.server.core.data.model.questionnaire.SpecialtyFamiliarityModel;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.QuestionnaireItem;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.items.NumberItem;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.items.TextItem;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.items.YesNoItem;
import com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor.ItemKey.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AbstractQuestionnaireProcessorTest {

    protected void assertBaseModel(QuestionnaireModel model, ProfessionalQuestionnaire entity) {
        assertFieldAndItemEqual(entity, YEARS_IN_DENTAL_FIELD, model.getYoeInDental());
        assertFieldAndItemEqual(entity, YEARS_BY_SPECIALTY, model.getYoeBySpecialty());
        assertFieldAndItemEqual(entity, DIGITAL_RADIOGRAPHY_SYSTEMS, model.getDigitalRadiographySystems());
        assertFieldAndItemEqual(entity, SOFTWARE_EXPERIENCE, model.getManagementSoftware());
    }

    protected void assertSpecialtyComfortEqual(SpecialtyComfortLevelModel model, ProfessionalQuestionnaire entity) {
        assertFieldAndItemEqual(entity, PEDO_COMFORT_LEVEL, model.getPedo());
        assertFieldAndItemEqual(entity, PROSTHO_COMFORT_LEVEL, model.getProstho());
        assertFieldAndItemEqual(entity, PERIO_COMFORT_LEVEL, model.getPerio());
        assertFieldAndItemEqual(entity, ENDO_COMFORT_LEVEL, model.getEndo());
        assertFieldAndItemEqual(entity, GENERAL_COMFORT_LEVEL, model.getGeneral());
        assertFieldAndItemEqual(entity, COSMETIC_COMFORT_LEVEL, model.getCosmetic());
        assertFieldAndItemEqual(entity, IMPLANTS_COMFORT_LEVEL, model.getImplants());
        assertFieldAndItemEqual(entity, ORAL_SURGERY_COMFORT_LEVEL, model.getOralSurgery());
    }

    protected void assertSpecialtyFamiliarityEqual(SpecialtyFamiliarityModel model, ProfessionalQuestionnaire entity) {
        assertFieldAndItemEqual(entity, PEDO_FAMILIARITY, model.getPedo());
        assertFieldAndItemEqual(entity, PROSTHO_FAMILIARITY, model.getProstho());
        assertFieldAndItemEqual(entity, PERIO_FAMILIARITY, model.getPerio());
        assertFieldAndItemEqual(entity, ENDO_FAMILIARITY, model.getEndo());
        assertFieldAndItemEqual(entity, GENERAL_FAMILIARITY, model.getGeneral());
        assertFieldAndItemEqual(entity, COSMETIC_FAMILIARITY, model.getCosmetic());
        assertFieldAndItemEqual(entity, IMPLANTS_FAMILIARITY, model.getImplants());
        assertFieldAndItemEqual(entity, ORAL_SURGERY_FAMILIARITY, model.getOralSurgery());
    }
    
    protected void assertDutiesEqual(DutiesModel model, ProfessionalQuestionnaire entity) {
        assertFieldAndItemEqual(entity, INSURANCE_BILLING, model.getInsuranceBilling());
        assertFieldAndItemEqual(entity, ELIGIBILITY_VERIFICATION, model.getEligibilityVerification());
        assertFieldAndItemEqual(entity, PATIENT_SCHEDULING, model.getPatientScheduling());
        assertFieldAndItemEqual(entity, HYGIENE_RECALL, model.getHygieneRecall());
        assertFieldAndItemEqual(entity, ACCT_RECEIVABLE, model.getAcctReceivable());
        assertFieldAndItemEqual(entity, CLAIM_SUBMISSION, model.getClaimSubmission());
        assertFieldAndItemEqual(entity, INSURANCE_PAYMENT_COLLECTION, model.getInsurancePaymentCollection());
        assertFieldAndItemEqual(entity, PATIENT_COORDINATION, model.getPatientCoordination());
        assertFieldAndItemEqual(entity, POSTING, model.getPosting());
        assertFieldAndItemEqual(entity, ACCT_PAYABLE, model.getAcctPayable());
        assertFieldAndItemEqual(entity, COLLECTIONS, model.getCollections());
        assertFieldAndItemEqual(entity, TREATMENT_PLANNING, model.getTreatmentPlanning());
        assertFieldAndItemEqual(entity, TREATMENT_PRESENTATION, model.getTreatmentPresentation());
        assertFieldAndItemEqual(entity, MARKETING, model.getMarketingSocialIntegration());
        assertFieldAndItemEqual(entity, FINANCIAL_COORDINATION, model.getFinancialCoordination());
        assertFieldAndItemEqual(entity, PAYROLL, model.getPayroll());
        assertFieldAndItemEqual(entity, OFFICE_MANAGEMENT, model.getOfficeManagement());
    }

    protected void assertFieldAndItemEqual(ProfessionalQuestionnaire entity, QuestionnaireProcessor.ItemKey key, Object fieldValue) {
        Optional<QuestionnaireItem> expectedOptional = entity.getItems().stream()
                .filter(item -> item.getKey().equals(key.name()))
                .findAny();

        if (!expectedOptional.isPresent()) {
            fail("Misconfiguration on test - item with key " + key.name() + " is not present in questionnaire");
        }

        QuestionnaireItem expectedItem = expectedOptional.get();

        if (expectedItem instanceof NumberItem) {
            assertEquals(key.name() + " not equal", ((NumberItem) expectedItem).getNumber(), fieldValue);
        } else if (expectedItem instanceof YesNoItem) {
            assertEquals(key.name() + " not equal", ((YesNoItem) expectedItem).isYes(), fieldValue);
        } else if (expectedItem instanceof TextItem) {
            assertEquals(key.name() + " not equal", ((TextItem) expectedItem).getText(), fieldValue);
        }
    }

    protected SpecialtyFamiliarityModel specialtyFamiliarityModel() {
        SpecialtyFamiliarityModel familiarityModel = new SpecialtyFamiliarityModel();

        familiarityModel.setPedo(nextBoolean());
        familiarityModel.setProstho(nextBoolean());
        familiarityModel.setPerio(nextBoolean());
        familiarityModel.setEndo(nextBoolean());
        familiarityModel.setGeneral(nextBoolean());
        familiarityModel.setCosmetic(nextBoolean());
        familiarityModel.setImplants(nextBoolean());
        familiarityModel.setOralSurgery(nextBoolean());

        return familiarityModel;
    }

    protected Set<QuestionnaireItem> specialtyFamiliarity() {
        Set<QuestionnaireItem> items = new HashSet<>();
        items.add(bool(PEDO_FAMILIARITY));
        items.add(bool(PROSTHO_FAMILIARITY));
        items.add(bool(PERIO_FAMILIARITY));
        items.add(bool(ENDO_FAMILIARITY));
        items.add(bool(GENERAL_FAMILIARITY));
        items.add(bool(COSMETIC_FAMILIARITY));
        items.add(bool(IMPLANTS_FAMILIARITY));
        items.add(bool(ORAL_SURGERY_FAMILIARITY));
        return items;
    }

    protected SpecialtyComfortLevelModel specialtyComfortModel() {
        SpecialtyComfortLevelModel familiarityModel = new SpecialtyComfortLevelModel();

        familiarityModel.setPedo(nextInt());
        familiarityModel.setProstho(nextInt());
        familiarityModel.setPerio(nextInt());
        familiarityModel.setEndo(nextInt());
        familiarityModel.setGeneral(nextInt());
        familiarityModel.setCosmetic(nextInt());
        familiarityModel.setImplants(nextInt());
        familiarityModel.setOralSurgery(nextInt());

        return familiarityModel;
    }

    protected Set<QuestionnaireItem> specialtyComfort() {
        Set<QuestionnaireItem> items = new HashSet<>();
        items.add(number(PEDO_COMFORT_LEVEL));
        items.add(number(PROSTHO_COMFORT_LEVEL));
        items.add(number(PERIO_COMFORT_LEVEL));
        items.add(number(ENDO_COMFORT_LEVEL));
        items.add(number(GENERAL_COMFORT_LEVEL));
        items.add(number(COSMETIC_COMFORT_LEVEL));
        items.add(number(IMPLANTS_COMFORT_LEVEL));
        items.add(number(ORAL_SURGERY_COMFORT_LEVEL));
        return items;
    }

    protected DutiesModel dutiesModel() {
        DutiesModel dutiesModel = new DutiesModel();

        dutiesModel.setInsuranceBilling(nextBoolean());
        dutiesModel.setEligibilityVerification(nextBoolean());
        dutiesModel.setPatientScheduling(nextBoolean());
        dutiesModel.setHygieneRecall(nextBoolean());
        dutiesModel.setAcctReceivable(nextBoolean());
        dutiesModel.setClaimSubmission(nextBoolean());
        dutiesModel.setInsurancePaymentCollection(nextBoolean());
        dutiesModel.setPatientCoordination(nextBoolean());
        dutiesModel.setPosting(nextBoolean());
        dutiesModel.setAcctPayable(nextBoolean());
        dutiesModel.setCollections(nextBoolean());
        dutiesModel.setTreatmentPlanning(nextBoolean());
        dutiesModel.setTreatmentPresentation(nextBoolean());
        dutiesModel.setMarketingSocialIntegration(nextBoolean());
        dutiesModel.setFinancialCoordination(nextBoolean());
        dutiesModel.setPayroll(nextBoolean());
        dutiesModel.setOfficeManagement(nextBoolean());

        return dutiesModel;
    }

    protected Set<QuestionnaireItem> duties() {
        Set<QuestionnaireItem> items = new HashSet<>();
        items.add(bool(INSURANCE_BILLING));
        items.add(bool(ELIGIBILITY_VERIFICATION));
        items.add(bool(PATIENT_SCHEDULING));
        items.add(bool(HYGIENE_RECALL));
        items.add(bool(ACCT_RECEIVABLE));
        items.add(bool(CLAIM_SUBMISSION));
        items.add(bool(INSURANCE_PAYMENT_COLLECTION));
        items.add(bool(PATIENT_COORDINATION));
        items.add(bool(POSTING));
        items.add(bool(ACCT_PAYABLE));
        items.add(bool(COLLECTIONS));
        items.add(bool(TREATMENT_PLANNING));
        items.add(bool(TREATMENT_PRESENTATION));
        items.add(bool(MARKETING));
        items.add(bool(FINANCIAL_COORDINATION));
        items.add(bool(PAYROLL));
        items.add(bool(OFFICE_MANAGEMENT));
        return items;
    }

    protected NumberItem number(QuestionnaireProcessor.ItemKey key) {
        return new NumberItem(key.name(), nextInt());
    }

    protected YesNoItem bool(QuestionnaireProcessor.ItemKey key) {
        return new YesNoItem(key.name(), nextBoolean());
    }

    protected TextItem text(QuestionnaireProcessor.ItemKey key) {
        return new TextItem(key.name(), randomAlphanumeric(100));
    }
}
