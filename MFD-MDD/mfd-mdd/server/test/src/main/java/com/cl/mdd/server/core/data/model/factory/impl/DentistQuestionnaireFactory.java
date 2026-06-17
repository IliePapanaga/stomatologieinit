package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.questionnaire.DentistQuestionnaireModel;
import com.cl.mdd.server.core.data.model.questionnaire.SpecialtyComfortLevelModel;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;

@Component
public class DentistQuestionnaireFactory extends AbstractModelFactory<DentistQuestionnaireModel> {

    @Override
    public DentistQuestionnaireModel fillFields(DentistQuestionnaireModel model) {
        return (DentistQuestionnaireModel) model
                .setTemporaryAsRdh(nextBoolean())
                .setCadCam(nextBoolean())
                .setIntraOralCam(nextBoolean())
                .setPano(nextBoolean())
                .setSurgery(nextBoolean())
                .setHoursOnFeet(nextBoolean())
                .setPatientsPerDay(nextBoolean())
                .setSpecialtiesComfort(new SpecialtyComfortLevelModel()
                        .setPedo(nextInt())
                        .setProstho(nextInt())
                        .setPerio(nextInt())
                        .setEndo(nextInt())
                        .setGeneral(nextInt())
                        .setCosmetic(nextInt())
                        .setImplants(nextInt())
                        .setOralSurgery(nextInt()))
                .setYoeInDental(nextInt())
                .setYoeBySpecialty(nextInt())
                .setDigitalRadiographySystems(randomAlphanumeric(100))
                .setManagementSoftware(randomAlphanumeric(100));
    }
}
