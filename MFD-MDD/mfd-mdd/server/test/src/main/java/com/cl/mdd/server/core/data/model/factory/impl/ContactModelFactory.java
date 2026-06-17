package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.common.ContactModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@Component
class ContactModelFactory extends AbstractModelFactory<ContactModel> {

    @Autowired
    private AddressModelFactory addressFactory;
    @Autowired
    private FullNameModelFactory nameFactory;

    @Override
    public ContactModel fillFields(ContactModel model) {
        return model.setAddress(addressFactory.create())
                .setFax(randomNumeric(10))
                .setPhone(randomNumeric(10))
                .setName(nameFactory.create())
                .setEmail(randomAlphabetic(10) + "@gmail.com");
    }
}
