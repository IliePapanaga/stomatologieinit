package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.ProfessionalModel;
import org.springframework.stereotype.Component;

@Component
class ProfessionalModelFactory extends UserModelFactory<ProfessionalModel> {
    @Override
    public ProfessionalModel fillFields(ProfessionalModel userModel) {
        super.fillFields(userModel);
        return userModel;
    }
}
