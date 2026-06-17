package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.access.specialty.SubCategoryDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.*;

public class SubCategoryValidatorTest {

    @Spy
    @InjectMocks
    private SubCategoryValidator validator = new SubCategoryValidator();

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private SubCategoryDao subCategoryDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsValid() {
        String id = "DA";
        Mockito.when(subCategoryDao.exists(id)).thenReturn(true);
        boolean result = validator.isValid(id, context);
        Assert.assertTrue(result);
    }

    @Test
    public void testIsNotValid() {
        String id = "DA";
        Mockito.when(subCategoryDao.exists(id)).thenReturn(false);
        boolean result = validator.isValid(id, context);
        Assert.assertFalse(result);
    }
}