package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.validation.constraint.composite.Username;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class UsernameValidatorTest {

    @Spy
    @InjectMocks
    private UsernameValidator validator = new UsernameValidator();

    @Mock
    private UserDao userDao;

    @Mock
    private Username annotation;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(annotation.unique()).thenReturn(true);
        validator.initialize(annotation);
    }

    @Test
    public void testIsValid() {
        String mail = "mail";
        Mockito.when(userDao.countByUsernameIgnoreCase(mail)).thenReturn(0L);

        Assert.assertTrue(validator.isValid(mail, null));
    }

    @Test
    public void testIsNotValid() {
        String mail = "mail";
        Mockito.when(userDao.countByUsernameIgnoreCase(mail)).thenReturn(1L);

        Assert.assertFalse(validator.isValid(mail, null));
    }
}