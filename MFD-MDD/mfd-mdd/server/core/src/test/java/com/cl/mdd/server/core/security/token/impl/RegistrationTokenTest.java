package com.cl.mdd.server.core.security.token.impl;

import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationTokenTest {

    @InjectMocks
    private RegistrationTokenImpl processor;

    @Mock
    private TextEncryptor encryptor;

    @Mock
    private UserDao identityDao;

    @Before
    public void setUp() {
    }

    @Test
    public void testAutowired() {
        Assert.assertSame(encryptor, ReflectionTestUtils.getField(processor, "encryptor"));
        Assert.assertSame(identityDao, ReflectionTestUtils.getField(processor, "userDao"));
    }

    @Test
    public void testPrefix() {
        Assert.assertEquals("reg|", processor.prefix());
    }

    @Test
    public void testValidityHours() {
        Assert.assertEquals(1440, processor.validityMinutes());
    }

    @Test
    public void testName() {
        Assert.assertEquals("Registration", processor.name());
    }

    @Test
    public void testAccept() {
        User identity = new Professional();
        Assert.assertFalse(processor.accept(identity));
        identity.setStatus(User.ACTIVE);
        Assert.assertFalse(processor.accept(identity));
        identity.setStatus(User.INACTIVE);
        Assert.assertFalse(processor.accept(identity));
        identity.setStatus(User.EMAIL_CONFIRMATION_PENDING);
        Assert.assertTrue(processor.accept(identity));
    }
}
