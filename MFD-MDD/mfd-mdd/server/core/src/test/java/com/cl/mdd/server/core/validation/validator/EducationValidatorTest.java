package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.access.common.EducationDao;
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
public class EducationValidatorTest {

    @Spy
    @InjectMocks
    private EducationValidator validator = new EducationValidator();

    @Mock
    private EducationDao educationDao;

    private String id = RandomStringUtils.randomAlphanumeric(50);

    @Before
    public void setUp() throws Exception {
        Mockito.doReturn(true).when(educationDao).exists(id);
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