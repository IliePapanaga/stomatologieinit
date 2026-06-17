package com.cl.mdd.server.core.security.token.impl;

import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.security.token.RegistrationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import static com.cl.mdd.server.core.data.persistent.model.user.User.EMAIL_CONFIRMATION_PENDING;

@Component
public class RegistrationTokenImpl extends PrefixedIdBasedToken implements RegistrationToken {

    @Value("${token.registration.validity.minutes:#{24*60}}")
    private int tokenValidity = 24 * 60;

    @Autowired
    public RegistrationTokenImpl(TextEncryptor encryptor, UserDao userDao) {
        super(encryptor, userDao);
    }

    @Override
    protected String prefix() {
        return "reg|";
    }

    @Override
    protected int validityMinutes() {
        return tokenValidity;
    }

    @Override
    protected String name() {
        return "Registration";
    }

    @Override
    protected boolean accept(User user) {
        return EMAIL_CONFIRMATION_PENDING.equals(user.getStatus());
    }
}
