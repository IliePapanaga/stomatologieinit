package com.cl.mdd.server.core.security.token.impl;

import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.security.token.ChangeUsernameToken;
import com.cl.mdd.server.core.security.token.ChangeUsernameTokenOutput;
import com.cl.mdd.server.core.security.token.exception.BadToken;
import com.cl.mdd.server.core.security.token.exception.ExpiredToken;
import com.cl.mdd.server.core.security.token.exception.InvalidToken;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
public class ChangeUsernameTokenImpl implements ChangeUsernameToken {

    private static final String PREFIX = "cu|";

    private static final String NAME = "Change username";

    @Value("${token.change.username.validity.minutes:15}")
    private int tokenValidity = 15;

    private static final String SEPARATOR_BODY = "+";

    private static final String SEPARATOR_HASH = "*";

    private final TextEncryptor encryptor;

    private final UserDao userDao;

    @Autowired
    public ChangeUsernameTokenImpl(TextEncryptor encryptor, UserDao userDao) {
        this.encryptor = encryptor;
        this.userDao = userDao;
    }

    @PostConstruct
    private void checkConfiguration() {
        if (StringUtils.containsAny(PREFIX, SEPARATOR_BODY, SEPARATOR_HASH)) {
            throw new IllegalArgumentException("Token prefix cannot contain separator symbols: "
                    + SEPARATOR_BODY + " or " + SEPARATOR_HASH);
        }
    }

    @Override
    public String generate(ChangeUsernameTokenOutput changeUsernameTokenOutput) {
        String body = PREFIX + changeUsernameTokenOutput.getUserId() + SEPARATOR_BODY + changeUsernameTokenOutput.getNewUsername() + SEPARATOR_BODY + System.currentTimeMillis();
        String plain = body + SEPARATOR_HASH + DigestUtils.md5Hex(body);
        return encryptor.encrypt(plain);
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeUsernameTokenOutput verify(String token) {
        String plain;
        try {
            plain = encryptor.decrypt(token);
        } catch (Exception e) {
            throw new BadToken(NAME + " token invalid", "E_TK_BAD");
        }
        String[] parts = StringUtils.split(plain, SEPARATOR_HASH);
        if (!parts[1].equals(DigestUtils.md5Hex(parts[0]))) {
            throw new BadToken(NAME + " token invalid", "E_TK_BAD");
        }
        if (!StringUtils.startsWith(parts[0], PREFIX)) {
            throw new BadToken(NAME + " token invalid", "E_TK_BAD");
        }
        parts = StringUtils.split(StringUtils.substringAfter(parts[0], PREFIX), SEPARATOR_BODY);
        if (System.currentTimeMillis() > Long.parseLong(parts[2]) + tokenValidity * DateUtils.MILLIS_PER_MINUTE) {
            throw new ExpiredToken(NAME + " token expired", "E_TK_EXPIRED");
        }
        String id = parts[0];
        String newUsername = parts[1];
        User user = userDao.findOne(id);
        if (user == null) {
            throw new InvalidToken(NAME + " token points to invalid user", "E_TK_INVALID");
        }
        return new ChangeUsernameTokenOutput(user.getId(), newUsername);
    }
}
