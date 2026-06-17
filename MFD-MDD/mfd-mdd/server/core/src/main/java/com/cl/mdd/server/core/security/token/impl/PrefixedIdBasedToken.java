package com.cl.mdd.server.core.security.token.impl;

import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.security.token.TokenGenerator;
import com.cl.mdd.server.core.security.token.TokenVerifier;
import com.cl.mdd.server.core.security.token.exception.BadToken;
import com.cl.mdd.server.core.security.token.exception.ExpiredToken;
import com.cl.mdd.server.core.security.token.exception.InvalidToken;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

public abstract class PrefixedIdBasedToken implements TokenGenerator<String>, TokenVerifier<String> {

    static final String SEPARATOR_BODY = "+";

    static final String SEPARATOR_HASH = "*";

    private final TextEncryptor encryptor;

    private final UserDao userDao;

    public PrefixedIdBasedToken(TextEncryptor encryptor, UserDao userDao) {
        this.encryptor = encryptor;
        this.userDao = userDao;
    }

    protected abstract String prefix();

    protected abstract int validityMinutes();

    protected abstract String name();

    protected abstract boolean accept(User user);

    @PostConstruct
    private void checkConfiguration() {
        if (StringUtils.containsAny(prefix(), SEPARATOR_BODY, SEPARATOR_HASH)) {
            throw new IllegalArgumentException("Token prefix cannot contain separator symbols: "
                    + SEPARATOR_BODY + " or " + SEPARATOR_HASH);
        }
    }

    @Override
    public String generate(String id) {
        String body = prefix() + id + SEPARATOR_BODY + System.currentTimeMillis();
        String plain = body + SEPARATOR_HASH + DigestUtils.md5Hex(body);
        return encryptor.encrypt(plain);
    }

    @Override
    @Transactional(readOnly = true)
    public String verify(String token) {
        String plain;
        try {
            plain = encryptor.decrypt(token);
        } catch (Exception e) {
            throw new BadToken(name() + " token invalid", "E_TK_BAD");
        }
        String[] parts = StringUtils.split(plain, SEPARATOR_HASH);
        if (!parts[1].equals(DigestUtils.md5Hex(parts[0]))) {
            throw new BadToken(name() + " token invalid", "E_TK_BAD");
        }
        if (!StringUtils.startsWith(parts[0], prefix())) {
            throw new BadToken(name() + " token invalid", "E_TK_BAD");
        }
        parts = StringUtils.split(StringUtils.substringAfter(parts[0], prefix()), SEPARATOR_BODY);
        if (System.currentTimeMillis() > Long.parseLong(parts[1]) + validityMinutes() * DateUtils.MILLIS_PER_MINUTE) {
            throw new ExpiredToken(name() + " token expired", "E_TK_EXPIRED");
        }
        String userId = parts[0];
        User user = userDao.findOne(userId);
        if (user == null || !accept(user)) {
            throw new InvalidToken(name() + " token points to invalid user", "E_TK_INVALID");
        }
        return user.getId();
    }
}
