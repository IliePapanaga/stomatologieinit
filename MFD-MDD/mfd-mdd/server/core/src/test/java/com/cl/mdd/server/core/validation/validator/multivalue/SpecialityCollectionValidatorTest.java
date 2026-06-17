package com.cl.mdd.server.core.validation.validator.multivalue;

import com.cl.mdd.server.core.validation.validator.SpecialityValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;

public class SpecialityCollectionValidatorTest {

    @Spy
    @InjectMocks
    private SpecialityCollectionValidator validator = new SpecialityCollectionValidator();

    @Mock
    private SpecialityValidator specialityValidator;

    @Mock
    private ConstraintValidatorContext context;

    private String speciality = "id";

    Collection<String> specialities = Collections.singleton(speciality);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsValid() {
        Mockito.when(specialityValidator.isValid(speciality, context)).thenReturn(true);

        boolean result = validator.isValid(specialities, context);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsNotValid() {
        Mockito.when(specialityValidator.isValid(speciality, context)).thenReturn(false);

        boolean result = validator.isValid(specialities, context);

        Assert.assertFalse(result);
    }
}