package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.access.common.SpecialityDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.*;

public class SpecialityValidatorTest {

    @Spy
    @InjectMocks
    private SpecialityValidator validator = new SpecialityValidator();

    @Mock
    private SpecialityDao specialityDao;

    @Mock
    private ConstraintValidatorContext context;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsValid() {
        String id = "id";
        Mockito.when(specialityDao.exists(id)).thenReturn(true);

        boolean result = validator.isValid(id, context);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsNotValid() {
        String id = "id";
        Mockito.when(specialityDao.exists(id)).thenReturn(false);

        boolean result = validator.isValid(id, context);

        Assert.assertFalse(result);
    }
}