package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.common.AddressModel;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@Component
class AddressModelFactory extends AbstractModelFactory<AddressModel> {

    public static final double DEFAULT_LAT = 47.03736;

    public static final double DEFAULT_LNG = 28.814521;

    @Override
    public AddressModel fillFields(AddressModel model) {
        model.setCountry(randomAlphabetic(10));
        model.setState(randomAlphabetic(10));
        model.setCity(randomAlphabetic(10));
        model.setStreet(randomAlphabetic(10));
        model.setZipCode(randomNumeric(5));
        model.setLatitude(DEFAULT_LAT);
        model.setLongitude(DEFAULT_LNG);

        return model;
    }
}
