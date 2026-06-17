package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.RegisterProfessional;
import org.springframework.stereotype.Component;

@Component
class RegisterProfessionalFactory extends RegisterUserFactory<RegisterProfessional> {

    @Override
    public RegisterProfessional fillFields(RegisterProfessional model) {
        super.fillFields(model);
        return model;
    }
}
