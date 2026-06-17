package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.RegisterUser;
import org.springframework.beans.factory.annotation.Autowired;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

abstract class RegisterUserFactory<T extends RegisterUser> extends AbstractModelFactory<T> {

    @Autowired
    private ContactModelFactory contactFactory;

    @Override
    public T fillFields(T model) {
        model.setContact(contactFactory.create())
                .setPassword("QAZwsx123!" + randomAlphanumeric(8))
                .setUsername(randomAlphanumeric(8) + "@gmail.com");
        return model;
    }

}
