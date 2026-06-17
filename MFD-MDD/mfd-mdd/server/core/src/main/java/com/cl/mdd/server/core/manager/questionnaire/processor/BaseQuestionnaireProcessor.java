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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static com.cl.mdd.server.core.manager.questionnaire.QuestionnaireProcessor.ItemKey.*;

public abstract class BaseQuestionnaireProcessor<T extends QuestionnaireModel> implements QuestionnaireProcessor<T> {

    private final String relativeCategoryId;

    private final Class<T> modelClass;

    protected BaseQuestionnaireProcessor(final String relativeCategoryId,
                                         final Class<T> modelClass) {
        this.relativeCategoryId = relativeCategoryId;
        this.modelClass = modelClass;
    }

    @Override
    public String relativeCategoryId() {
        return relativeCategoryId;
    }

    @Override
    public Class<T> modelClass() {
        return modelClass;
    }

    @Override
    public void updateEntity(T model, ProfessionalQuestionnaire entity) {
        updateBaseItems(model, entity);
        updateCustomItems(model, entity);
    }

    private void updateBaseItems(QuestionnaireModel model, ProfessionalQuestionnaire entity) {
        setNumber(entity, YEARS_IN_DENTAL_FIELD, model.getYoeInDental());
        setNumber(entity, YEARS_BY_SPECIALTY, model.getYoeBySpecialty());
        setText(entity, DIGITAL_RADIOGRAPHY_SYSTEMS, model.getDigitalRadiographySystems());
        setText(entity, SOFTWARE_EXPERIENCE, model.getManagementSoftware());
    }

    protected abstract void updateCustomItems(T model, ProfessionalQuestionnaire entity);

    @Override
    public T convertToModel(ProfessionalQuestionnaire entity) {
        T model = create();

        if (entity != null) {
            model.setId(entity.getId());

            convertBaseModel(entity, model);
            convertCustomModel(entity, model);
        }

        return model;
    }

    protected abstract T create();

    private void convertBaseModel(ProfessionalQuestionnaire entity, QuestionnaireModel model) {
        model.setDigitalRadiographySystems(getText(entity, DIGITAL_RADIOGRAPHY_SYSTEMS))
                .setManagementSoftware(getText(entity, SOFTWARE_EXPERIENCE))
                .setYoeInDental(getNumber(entity, YEARS_IN_DENTAL_FIELD))
                .setYoeBySpecialty(getNumber(entity, YEARS_BY_SPECIALTY));
    }

    protected abstract void convertCustomModel(ProfessionalQuestionnaire entity, T model);

    protected Integer getNumber(ProfessionalQuestionnaire questionnaire, ItemKey key) {
        return itemByKeyAndType(questionnaire, key, NumberItem.class)
                .map(item -> item.getNumber()).orElse(null);
    }

    protected String getText(ProfessionalQuestionnaire questionnaire, ItemKey key) {
        return itemByKeyAndType(questionnaire, key, TextItem.class)
                .map(item -> item.getText()).orElse(null);
    }

    protected Boolean getBoolean(ProfessionalQuestionnaire questionnaire, ItemKey key) {
        return itemByKeyAndType(questionnaire, key, YesNoItem.class)
                .map(item -> item.isYes()).orElse(null);
    }

    protected void setNumber(ProfessionalQuestionnaire entity, ItemKey key, Integer value) {
        Optional<NumberItem> itemOptional = itemByKeyAndType(entity, key, NumberItem.class);

        if (itemOptional.isPresent()) {
            itemOptional.get().setNumber(value);
        } else {
            entity.getItems().add(new NumberItem(key.name(), value));
        }
    }

    protected void setBoolean(ProfessionalQuestionnaire entity, ItemKey key, Boolean value) {
        Optional<YesNoItem> itemOptional = itemByKeyAndType(entity, key, YesNoItem.class);

        if (itemOptional.isPresent()) {
            itemOptional.get().setYes(value);
        } else {
            entity.getItems().add(new YesNoItem(key.name(), value));
        }
    }

    protected void setText(ProfessionalQuestionnaire entity, ItemKey key, String value) {
        Optional<TextItem> itemOptional = itemByKeyAndType(entity, key, TextItem.class);

        if (itemOptional.isPresent()) {
            itemOptional.get().setText(value);
        } else {
            entity.getItems().add(new TextItem(key.name(), value));
        }
    }

    private <T extends QuestionnaireItem> Optional<T> itemByKeyAndType(ProfessionalQuestionnaire questionnaire, ItemKey key, Class<T> expectedType) {
        return (Optional<T>) CollectionUtils.emptyIfNull(questionnaire.getItems()).stream()
                .filter(item -> StringUtils.equalsIgnoreCase(key.name(), item.getKey()) && item.getClass().equals(expectedType))
                .findFirst();
    }

    protected void updateSpecialtyComfort(SpecialtyComfortLevelModel model, ProfessionalQuestionnaire entity) {
        if (model != null) {
            setNumber(entity, PEDO_COMFORT_LEVEL, model.getPedo());
            setNumber(entity, PROSTHO_COMFORT_LEVEL, model.getProstho());
            setNumber(entity, PERIO_COMFORT_LEVEL, model.getPerio());
            setNumber(entity, ENDO_COMFORT_LEVEL, model.getEndo());
            setNumber(entity, GENERAL_COMFORT_LEVEL, model.getGeneral());
            setNumber(entity, COSMETIC_COMFORT_LEVEL, model.getCosmetic());
            setNumber(entity, IMPLANTS_COMFORT_LEVEL, model.getImplants());
            setNumber(entity, ORAL_SURGERY_COMFORT_LEVEL, model.getOralSurgery());
        }
    }

    protected void convertSpecialtyComfort(ProfessionalQuestionnaire entity, SpecialtyComfortLevelModel model) {
        model.setPedo(getNumber(entity, PEDO_COMFORT_LEVEL))
                .setProstho(getNumber(entity, PROSTHO_COMFORT_LEVEL))
                .setPerio(getNumber(entity, PERIO_COMFORT_LEVEL))
                .setEndo(getNumber(entity, ENDO_COMFORT_LEVEL))
                .setGeneral(getNumber(entity, GENERAL_COMFORT_LEVEL))
                .setCosmetic(getNumber(entity, COSMETIC_COMFORT_LEVEL))
                .setImplants(getNumber(entity, IMPLANTS_COMFORT_LEVEL))
                .setOralSurgery(getNumber(entity, ORAL_SURGERY_COMFORT_LEVEL));
    }

    protected void updateSpecialtyFamiliarity(SpecialtyFamiliarityModel model, ProfessionalQuestionnaire entity) {
        if (model != null) {
            setBoolean(entity, PEDO_FAMILIARITY, model.getPedo());
            setBoolean(entity, PROSTHO_FAMILIARITY, model.getProstho());
            setBoolean(entity, PERIO_FAMILIARITY, model.getPerio());
            setBoolean(entity, ENDO_FAMILIARITY, model.getEndo());
            setBoolean(entity, GENERAL_FAMILIARITY, model.getGeneral());
            setBoolean(entity, COSMETIC_FAMILIARITY, model.getCosmetic());
            setBoolean(entity, IMPLANTS_FAMILIARITY, model.getImplants());
            setBoolean(entity, ORAL_SURGERY_FAMILIARITY, model.getOralSurgery());
        }
    }

    protected void convertSpecialtyFamiliarity(ProfessionalQuestionnaire entity, SpecialtyFamiliarityModel model) {
        model.setPedo(getBoolean(entity, PEDO_FAMILIARITY))
                .setProstho(getBoolean(entity, PROSTHO_FAMILIARITY))
                .setPerio(getBoolean(entity, PERIO_FAMILIARITY))
                .setEndo(getBoolean(entity, ENDO_FAMILIARITY))
                .setGeneral(getBoolean(entity, GENERAL_FAMILIARITY))
                .setCosmetic(getBoolean(entity, COSMETIC_FAMILIARITY))
                .setImplants(getBoolean(entity, IMPLANTS_FAMILIARITY))
                .setOralSurgery(getBoolean(entity, ORAL_SURGERY_FAMILIARITY));
    }

    protected void updateDuties(DutiesModel model, ProfessionalQuestionnaire entity) {
        if (model != null) {
            setBoolean(entity, INSURANCE_BILLING, model.getInsuranceBilling());
            setBoolean(entity, ELIGIBILITY_VERIFICATION, model.getEligibilityVerification());
            setBoolean(entity, PATIENT_SCHEDULING, model.getPatientScheduling());
            setBoolean(entity, HYGIENE_RECALL, model.getHygieneRecall());
            setBoolean(entity, ACCT_RECEIVABLE, model.getAcctReceivable());
            setBoolean(entity, CLAIM_SUBMISSION, model.getClaimSubmission());
            setBoolean(entity, INSURANCE_PAYMENT_COLLECTION, model.getInsurancePaymentCollection());
            setBoolean(entity, PATIENT_COORDINATION, model.getPatientCoordination());
            setBoolean(entity, POSTING, model.getPosting());
            setBoolean(entity, ACCT_PAYABLE, model.getAcctPayable());
            setBoolean(entity, COLLECTIONS, model.getCollections());
            setBoolean(entity, TREATMENT_PLANNING, model.getTreatmentPlanning());
            setBoolean(entity, TREATMENT_PRESENTATION, model.getTreatmentPresentation());
            setBoolean(entity, MARKETING, model.getMarketingSocialIntegration());
            setBoolean(entity, FINANCIAL_COORDINATION, model.getFinancialCoordination());
            setBoolean(entity, PAYROLL, model.getPayroll());
            setBoolean(entity, OFFICE_MANAGEMENT, model.getOfficeManagement());
        }
    }

    protected void convertDuties(ProfessionalQuestionnaire entity, DutiesModel model) {
        model.setInsuranceBilling(getBoolean(entity, INSURANCE_BILLING))
                .setEligibilityVerification(getBoolean(entity, ELIGIBILITY_VERIFICATION))
                .setPatientScheduling(getBoolean(entity, PATIENT_SCHEDULING))
                .setHygieneRecall(getBoolean(entity, HYGIENE_RECALL))
                .setAcctReceivable(getBoolean(entity, ACCT_RECEIVABLE))
                .setClaimSubmission(getBoolean(entity, CLAIM_SUBMISSION))
                .setInsurancePaymentCollection(getBoolean(entity, INSURANCE_PAYMENT_COLLECTION))
                .setPatientCoordination(getBoolean(entity, PATIENT_COORDINATION))
                .setPosting(getBoolean(entity, POSTING))
                .setAcctPayable(getBoolean(entity, ACCT_PAYABLE))
                .setCollections(getBoolean(entity, COLLECTIONS))
                .setTreatmentPlanning(getBoolean(entity, TREATMENT_PLANNING))
                .setTreatmentPresentation(getBoolean(entity, TREATMENT_PRESENTATION))
                .setMarketingSocialIntegration(getBoolean(entity, MARKETING))
                .setFinancialCoordination(getBoolean(entity, FINANCIAL_COORDINATION))
                .setPayroll(getBoolean(entity, PAYROLL))
                .setOfficeManagement(getBoolean(entity, OFFICE_MANAGEMENT));
    }
}
