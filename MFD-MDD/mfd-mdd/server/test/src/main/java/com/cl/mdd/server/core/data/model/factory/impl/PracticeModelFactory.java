package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.PracticeModel;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@Component
class PracticeModelFactory extends AbstractModelFactory<PracticeModel> {

    @Autowired
    private AddressModelFactory addressModelFactory;

    @Override
    public PracticeModel fillFields(PracticeModel model) {
        model.setPhone(randomNumeric(10));
        model.setAfterWorkPhone(randomNumeric(10));
        model.setWebSite("https://google.com");
        model.setBillingAddress(addressModelFactory.create());
        model.setOfficeManagerName(randomAlphanumeric(60));
        model.setSoftwares(randomAlphanumeric(255));
        model.setStatus(Practice.ACTIVE);
        model.setName(randomAlphanumeric(60));
        return model;
    }
}
