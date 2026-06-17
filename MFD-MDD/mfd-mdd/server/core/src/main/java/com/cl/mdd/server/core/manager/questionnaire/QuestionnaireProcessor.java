package com.cl.mdd.server.core.manager.questionnaire;

import com.cl.mdd.server.core.data.model.questionnaire.QuestionnaireModel;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;

public interface QuestionnaireProcessor<T extends QuestionnaireModel> {

    void updateEntity(T model, ProfessionalQuestionnaire entity);

    T convertToModel(ProfessionalQuestionnaire entity);

    String relativeCategoryId();

    Class<T> modelClass();

    enum ItemKey {
        YEARS_IN_DENTAL_FIELD,
        YEARS_BY_SPECIALTY,
        DIGITAL_RADIOGRAPHY_SYSTEMS,
        PEDO_COMFORT_LEVEL,
        PROSTHO_COMFORT_LEVEL,
        PERIO_COMFORT_LEVEL,
        ENDO_COMFORT_LEVEL,
        GENERAL_COMFORT_LEVEL,
        COSMETIC_COMFORT_LEVEL,
        IMPLANTS_COMFORT_LEVEL,
        ORAL_SURGERY_COMFORT_LEVEL,
        SOFTWARE_EXPERIENCE,

        PEDO_FAMILIARITY,
        PROSTHO_FAMILIARITY,
        PERIO_FAMILIARITY,
        ENDO_FAMILIARITY,
        GENERAL_FAMILIARITY,
        COSMETIC_FAMILIARITY,
        IMPLANTS_FAMILIARITY,
        ORAL_SURGERY_FAMILIARITY,

        INTRA_ORAL_CAM_FAMILIARITY,
        PANO_CAM_FAMILIARITY,
        CAD_CAM_FAMILIARITY,

        DDS_TEMPORARY_AS_RDH,
        DDS_SURGERY_COMFORT,
        DDS_8_HOURS_ON_FEET,
        DDS_8_10_PATIENTS_PER_DAY,

        RDH_NO_COMFORT,
        RDH_ANESTHETIZE,
        RDH_ANTI_MICROBIAL,
        RDH_RECARE,

        INSURANCE_BILLING,
        ELIGIBILITY_VERIFICATION,
        PATIENT_SCHEDULING,
        HYGIENE_RECALL,
        ACCT_RECEIVABLE,
        CLAIM_SUBMISSION,
        INSURANCE_PAYMENT_COLLECTION,
        PATIENT_COORDINATION,
        POSTING,
        ACCT_PAYABLE,
        COLLECTIONS,
        TREATMENT_PLANNING,
        TREATMENT_PRESENTATION,
        MARKETING,
        FINANCIAL_COORDINATION,
        PAYROLL,
        OFFICE_MANAGEMENT,

        IMAGING_3D,
        XRAY_MACHINES,

        RDA_NOMAD_FAMILIARITY,

        CROSS_TRAINED,

        FO_XRAYS_TO_INSURANCE
    }
}
