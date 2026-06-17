package com.cl.mdd.server.core.validation.validator.multivalue;

import com.cl.mdd.server.core.data.model.upload.certificates.CertificateModel;
import com.cl.mdd.server.core.validation.validator.CertificateValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import javax.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

public class CertificateCollectionValidatorTest {

    @Spy
    @InjectMocks
    private CertificateCollectionValidator testClass = new CertificateCollectionValidator();

    @Mock
    private CertificateValidator certificateValidator;

    @Mock
    private ConstraintValidatorContext context;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsValid() {
        CertificateModel certificateModel = new CertificateModel();
        Collection<CertificateModel> certificates = Collections.singletonList(certificateModel);
        Mockito.when(certificateValidator.isValid(certificateModel, context)).thenReturn(true);

        boolean result = testClass.isValid(certificates, context);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsNotValid() {
        CertificateModel certificateModel = new CertificateModel();
        Collection<CertificateModel> certificates = Collections.singletonList(certificateModel);
        Mockito.when(certificateValidator.isValid(certificateModel, context)).thenReturn(false);

        boolean result = testClass.isValid(certificates, context);

        Assert.assertFalse(result);
    }

    @Test
    public void testIsValidWithNullModel() {
        boolean result = testClass.isValid(null, context);

        Assert.assertTrue(result);
    }
}