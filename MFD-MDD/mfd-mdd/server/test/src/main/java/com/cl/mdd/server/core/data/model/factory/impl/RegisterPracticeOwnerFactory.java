package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.RegisterPracticeOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class RegisterPracticeOwnerFactory extends RegisterUserFactory<RegisterPracticeOwner> {
    @Autowired
    private RegisterPracticeFactory registerPracticeFactory;

    @Override
    public RegisterPracticeOwner fillFields(RegisterPracticeOwner model) {
        super.fillFields(model);
        model.getContact().getAddress().setLatitude(null);
        model.getContact().getAddress().setLongitude(null);
        return model.setRegisterPractice(registerPracticeFactory.create());
    }
}
