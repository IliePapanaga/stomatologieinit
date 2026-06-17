package com.cl.mdd.server.core.validation.validator.multivalue;

import com.cl.mdd.server.core.validation.validator.BayAreaValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class BayAreaCollectionValidatorTest {

    @Spy
    @InjectMocks
    private BayAreaCollectionValidator validator = new BayAreaCollectionValidator();

    @Mock
    private BayAreaValidator bayAreaValidator;

    @Mock
    private ConstraintValidatorContext context;

    private String id = "id";

    Collection<String> specialities = Collections.singleton(id);

    @Test
    public void testIsValid() {
        Mockito.when(bayAreaValidator.isValid(id, context)).thenReturn(true);

        boolean result = validator.isValid(specialities, context);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsNotValid() {
        Mockito.when(bayAreaValidator.isValid(id, context)).thenReturn(false);

        boolean result = validator.isValid(specialities, context);

        Assert.assertFalse(result);
    }
}