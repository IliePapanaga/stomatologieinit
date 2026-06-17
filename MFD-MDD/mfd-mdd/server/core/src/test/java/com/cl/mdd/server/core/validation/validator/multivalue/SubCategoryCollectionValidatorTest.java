package com.cl.mdd.server.core.validation.validator.multivalue;

import com.cl.mdd.server.core.validation.validator.SubCategoryValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

public class SubCategoryCollectionValidatorTest {

    @Spy
    @InjectMocks
    private SubCategoryCollectionValidator validator = new SubCategoryCollectionValidator();

    @Mock
    private SubCategoryValidator categoryValidator;

    @Mock
    private ConstraintValidatorContext context;

    private String id = "DA";

    private Collection<String> subCategories = Collections.singleton(id);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsValid() {
        Mockito.when(categoryValidator.isValid(id, context)).thenReturn(true);

        boolean result = validator.isValid(subCategories, context);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsNotValid() {
        Mockito.when(categoryValidator.isValid(id, context)).thenReturn(false);

        boolean result = validator.isValid(subCategories, context);

        Assert.assertFalse(result);
    }
}