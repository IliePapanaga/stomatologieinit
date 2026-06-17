package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.BasePracticeModel;
import org.springframework.beans.factory.annotation.Autowired;

import static org.apache.commons.lang3.RandomStringUtils.*;


abstract class BasePracticeModelFactory<T extends BasePracticeModel> extends AbstractModelFactory<T> {

    @Autowired
    private AddressModelFactory addressFactory;

    @Override
    public T fillFields(T userModel) {
        userModel.setAfterWorkPhone(randomNumeric(10))
                .setPhone(randomNumeric(10))
                .setSecondEmail("myEmail@gmail.com")
                .setName(randomAlphanumeric(10))
                .setSoftwares(randomAlphanumeric(5)+","+ randomAlphanumeric(5))
                .setWebSite("https://"+ randomAlphabetic(6)+".com")
                .setBillingAddress(addressFactory.create())
                .setOfficeManagerName(randomAlphabetic(8));


        return userModel;
    }
}
