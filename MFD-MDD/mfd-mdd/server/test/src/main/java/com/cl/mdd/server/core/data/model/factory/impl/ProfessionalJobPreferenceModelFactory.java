package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;

@Component
public class ProfessionalJobPreferenceModelFactory extends AbstractModelFactory<ProfessionalJobPreferenceModel> {

    @Override
    public ProfessionalJobPreferenceModel fillFields(ProfessionalJobPreferenceModel model) {
        model.setWillingToRelocate(nextBoolean());
        model.setLookingForFullTimeJob(nextBoolean());
        model.setLookingForPartTimeJob(nextBoolean());
        model.setLookingForPermanentJob(nextBoolean());
        model.setLookingForTemporaryJob(nextBoolean());
        model.setEveningWorkingHoursOk(nextBoolean());
        model.setDesiredRatePerHour(new BigDecimal(nextInt(1, 200)));
//        model.setSalaryFrom(new BigDecimal(nextInt(1, 100)));
//        model.setSalaryTo(new BigDecimal(nextInt(100, 200)));
        model.setSalaryFrom(null);
        model.setSalaryTo(null);
        model.setCommutingRadius(new BigDecimal(nextInt(1, 101)));
        model.setAvailabilityDays(mddRandomUtils.randomWeekDays());
        model.setBayAreas(mddRandomUtils.randomBayAreas());
        return model;
    }

}
