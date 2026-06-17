package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.persistent.model.contact.Address;
import com.cl.mdd.server.core.service.notification.definition.NotificationContextSupplier;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.cl.mdd.server.core.service.notification.definition.impl.BasePredefinedVariables;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class PracticeLocationAddressVariables extends BasePredefinedVariables implements NotificationContextSupplier<Address> {

    public static final String STATE_PLACE_HOLDER = "{practice.location.state}";

    public static final String CITY_PLACE_HOLDER = "{practice.location.city}";

    public static final String STREET_PLACE_HOLDER = "{practice.location.street}";

    public static final String ZIP_CODE_PLACE_HOLDER = "{practice.location.zip_code}";

    private static final List<Variable> USER_VARS = Arrays.asList(
            variable("notification.var.practice.location.state", STATE_PLACE_HOLDER),
            variable("notification.var.practice.location.city", CITY_PLACE_HOLDER),
            variable("notification.var.practice.location.street", STREET_PLACE_HOLDER),
            variable("notification.var.practice.location.zipCode", ZIP_CODE_PLACE_HOLDER)
    );

    @Override
    public List<Variable> expand() {
        return USER_VARS;
    }

    @Override
    public void supply(Address object, Map<String, String> context) {
        context.put(STATE_PLACE_HOLDER, object.getState());
        context.put(CITY_PLACE_HOLDER, object.getCity());
        context.put(STREET_PLACE_HOLDER, object.getStreet());
        context.put(ZIP_CODE_PLACE_HOLDER, object.getZipCode());
    }
}
