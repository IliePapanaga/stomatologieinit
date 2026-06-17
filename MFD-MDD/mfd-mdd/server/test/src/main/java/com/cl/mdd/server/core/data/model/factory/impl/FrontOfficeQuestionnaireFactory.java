package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.questionnaire.*;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;

@Component
public class FrontOfficeQuestionnaireFactory extends AbstractModelFactory<FrontOfficeQuestionnaireModel> {

    @Override
    public FrontOfficeQuestionnaireModel fillFields(FrontOfficeQuestionnaireModel model) {
        return (FrontOfficeQuestionnaireModel) model
                .setCrossTrained(nextBoolean())
                .setxRaysAndCameraImagesToInsurance(nextBoolean())
                .setDuties(new DutiesModel()
                        .setInsuranceBilling(nextBoolean())
                        .setEligibilityVerification(nextBoolean())
                        .setPatientScheduling(nextBoolean())
                        .setHygieneRecall(nextBoolean())
                        .setAcctReceivable(nextBoolean())
                        .setClaimSubmission(nextBoolean())
                        .setInsurancePaymentCollection(nextBoolean())
                        .setPatientCoordination(nextBoolean())
                        .setPosting(nextBoolean())
                        .setAcctPayable(nextBoolean())
                        .setCollections(nextBoolean())
                        .setTreatmentPlanning(nextBoolean())
                        .setTreatmentPresentation(nextBoolean())
                        .setMarketingSocialIntegration(nextBoolean())
                        .setFinancialCoordination(nextBoolean())
                        .setPayroll(nextBoolean())
                        .setOfficeManagement(nextBoolean())
                )
                .setSpecialtiesFamiliarity(new SpecialtyFamiliarityModel()
                        .setPedo(nextBoolean())
                        .setProstho(nextBoolean())
                        .setPerio(nextBoolean())
                        .setEndo(nextBoolean())
                        .setGeneral(nextBoolean())
                        .setCosmetic(nextBoolean())
                        .setImplants(nextBoolean())
                        .setOralSurgery(nextBoolean()))
                .setYoeInDental(nextInt())
                .setYoeBySpecialty(nextInt())
                .setDigitalRadiographySystems(randomAlphanumeric(100))
                .setManagementSoftware(randomAlphanumeric(100));
    }
}
