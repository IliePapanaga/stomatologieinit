package com.cl.mdd.server.core.security.token.impl;

import com.cl.mdd.server.core.data.model.RegisterUser;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.security.token.exception.BadToken;
import com.cl.mdd.server.core.security.token.exception.ExpiredToken;
import com.cl.mdd.server.core.security.token.exception.InvalidToken;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.util.UUID;

import static com.cl.mdd.server.core.security.token.impl.PrefixedIdBasedToken.SEPARATOR_BODY;

@RunWith(MockitoJUnitRunner.class)
public class PrefixedIdBasedTokenTest {

    private PrefixedIdBasedToken processor;

    @Mock
    private TextEncryptor encryptor;

    @Mock
    private UserDao userDao;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    private RegisterUser registerUser = new RegisterUser() {
    };

    private Professional user = new Professional();

//    private FullNameModel name = new FullNameModel();

    private String prefix = "pfx";

    private String id = UUID.randomUUID().toString();

    private String tokenBody = prefix + id + SEPARATOR_BODY + System.currentTimeMillis();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        processor = new PrefixedIdBasedToken(encryptor, userDao) {
            @Override
            protected String prefix() {
                return prefix;
            }

            @Override
            protected int validityMinutes() {
                return 48;
            }

            @Override
            protected String name() {
                return "test";
            }

            @Override
            protected boolean accept(User user) {
                return !"not acceptable".equals(user.getUsername());
            }
        };

        registerUser.setUsername(id);
    }

    @Test
    public void testGenerate() {
        long start = System.currentTimeMillis();
        Mockito.when(encryptor.encrypt(stringCaptor.capture())).thenReturn("abc");
        Assert.assertEquals("abc", processor.generate(registerUser.getUsername()));
        String token = stringCaptor.getValue();
        Assert.assertTrue(StringUtils.startsWith(
                token, prefix + registerUser.getUsername() + SEPARATOR_BODY));
        String ts = StringUtils.substringBetween(
                token, SEPARATOR_BODY, RegistrationTokenImpl.SEPARATOR_HASH);
        Assert.assertTrue(Long.parseLong(ts) >= start);
        Assert.assertEquals(
                DigestUtils.md5Hex(prefix + registerUser.getUsername() + SEPARATOR_BODY + ts),
                StringUtils.substringAfterLast(token, RegistrationTokenImpl.SEPARATOR_HASH));
    }

    @Test(expected = BadToken.class)
    public void testVerifyBadValue() {
        Mockito.when(encryptor.decrypt("abc")).thenThrow(new IllegalArgumentException());
        processor.verify("abc");
    }

    @Test(expected = BadToken.class)
    public void testVerifyBad() {
        Mockito.when(encryptor.decrypt("abc")).thenReturn("a*b");
        processor.verify("abc");
    }

    @Test(expected = ExpiredToken.class)
    public void testVerifyExpired() {
        String body = prefix + "l" + "+" + (System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY * 100);
        Mockito.when(encryptor.decrypt("abc")).thenReturn(body + "*" + DigestUtils.md5Hex(body));
        processor.verify("abc");
    }

    @Test(expected = BadToken.class)
    public void testVerifyBadNoPrefix() {
        Mockito.when(encryptor.decrypt("abc")).thenReturn("xyz" + "*" + DigestUtils.md5Hex("xyz"));
        processor.verify("abc");
    }

    @Test(expected = InvalidToken.class)
    public void testVerifyNoUser() {
        Mockito.when(encryptor.decrypt("abc")).thenReturn(tokenBody + "*" + DigestUtils.md5Hex(tokenBody));
        processor.verify("abc");
    }

    @Test(expected = InvalidToken.class)
    public void testVerifyNotAccepted() {
        Mockito.when(encryptor.decrypt("abc")).thenReturn(tokenBody + "*" + DigestUtils.md5Hex(tokenBody));
        user.setUsername("not acceptable");
        Mockito.when(userDao.findByUsernameIgnoreCase(registerUser.getUsername())).thenReturn(user);
        processor.verify("abc");
    }

    @Test
    public void testVerify() {
        Mockito.when(encryptor.decrypt("abc")).thenReturn(tokenBody + "*" + DigestUtils.md5Hex(tokenBody));
        Mockito.when(userDao.findOne(registerUser.getUsername())).thenReturn(user);
        String result = processor.verify("abc");
        Assert.assertEquals(user.getUsername(), result);
    }
}
