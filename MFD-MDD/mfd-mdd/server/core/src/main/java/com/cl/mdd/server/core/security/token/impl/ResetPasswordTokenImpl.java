package com.cl.mdd.server.core.security.token.impl;

import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.security.token.ResetPasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordTokenImpl extends PrefixedIdBasedToken implements ResetPasswordToken {

    @Value("${token.reset.password.validity.minutes:15}")
    private int tokenValidity = 15;

    @Autowired
    public ResetPasswordTokenImpl(TextEncryptor encryptor, UserDao userDao) {
        super(encryptor, userDao);
    }

    @Override
    protected String prefix() {
        return "pr|";
    }

    @Override
    protected int validityMinutes() {
        return tokenValidity;
    }

    @Override
    protected String name() {
        return "Reset password";
    }

    @Override
    protected boolean accept(User user) {
        return true;
    }
}
