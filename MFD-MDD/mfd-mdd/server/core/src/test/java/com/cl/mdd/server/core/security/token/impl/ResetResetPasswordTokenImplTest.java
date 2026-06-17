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
public class ResetResetPasswordTokenImplTest {

    @InjectMocks
    private ResetPasswordTokenImpl processor;

    @Mock
    private TextEncryptor encryptor;

    @Mock
    private UserDao userDao;

    @Before
    public void setUp() {
    }

    @Test
    public void testAutowired() {
        Assert.assertSame(encryptor, ReflectionTestUtils.getField(processor, "encryptor"));
        Assert.assertSame(userDao, ReflectionTestUtils.getField(processor, "userDao"));
    }

    @Test
    public void testPrefix() {
        Assert.assertEquals("pr|", processor.prefix());
    }

    @Test
    public void testValidityHours() {
        Assert.assertEquals(15, processor.validityMinutes());
    }

    @Test
    public void testName() {
        Assert.assertEquals("Reset password", processor.name());
    }

    @Test
    public void testAccept() {
        User identity = new Professional();
        Assert.assertTrue(processor.accept(identity));
    }
}
