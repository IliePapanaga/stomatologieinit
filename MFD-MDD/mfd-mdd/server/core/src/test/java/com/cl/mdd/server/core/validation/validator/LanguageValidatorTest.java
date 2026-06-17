package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.access.common.LanguageDao;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LanguageValidatorTest {

    @Spy
    @InjectMocks
    private LanguageValidator validator = new LanguageValidator();

    @Mock
    private LanguageDao languageDao;

    private String id = RandomStringUtils.randomAlphanumeric(50);

    @Before
    public void setUp() throws Exception {
        Mockito.doReturn(true).when(languageDao).exists(id);
    }

    @Test
    public void testIsNotValid() {

        Assert.assertFalse(validator.isValid("abc", null));
    }

    @Test
    public void testIsValid() {

        Assert.assertTrue(validator.isValid(id, null));
    }

    @Test
    public void testIsValidNull() {

        Assert.assertTrue(validator.isValid(null, null));
    }

}