package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.RegisterSystemUser;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.common.FullNameModel;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Component
class RegisterSystemUserFactory extends RegisterUserFactory<RegisterSystemUser> {

    @Override
    public RegisterSystemUser fillFields(RegisterSystemUser model) {
        model
                .setUsername(randomAlphanumeric(8) + "@gmail.com")
                .setPassword("ABCdef123$%^")
                .setContact(
                        new ContactModel().setPhone("0000000000")
                                .setName(
                                        new FullNameModel(
                                                randomAlphabetic(10),
                                                randomAlphabetic(10)
                                        )));
        return model;
    }
}
