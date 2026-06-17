package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.model.upload.certificates.CertificateModel;
import com.cl.mdd.server.core.data.persistent.access.user.CertificateDetailsDao;
import com.cl.mdd.server.core.data.persistent.model.specialty.CertificateType;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import com.cl.mdd.server.core.security.SecurityAccess;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public class CertificateValidatorTest {

    @Spy
    @InjectMocks
    private CertificateValidator testClass = new CertificateValidator();

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private CertificateDetailsDao certificateDetailsDao;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;

    @Mock
    private SecurityAccess securityAccess;

    CertificateModel model = new CertificateModel();

    private Professional professional = new Professional();

    private String id = UUID.randomUUID().toString();


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(constraintViolationBuilder);
        Mockito.when(securityAccess.currentUserId()).thenReturn(id);
        professional.setId(id);
    }

    @Test
    public void testIsValid() {
        CertificateModel model = new CertificateModel();
        model.setType(CertificateType.RDA);
        Mockito.doReturn(true).when(testClass).validateRDA(model, context);

        boolean result = testClass.isValid(model, context);

        Assert.assertTrue(result);
    }

    @Test
    public void testValidateDEA() {
        boolean result = testClass.validateDEA(model, context);

        Assert.assertFalse(result);
        Mockito.verify(context).buildConstraintViolationWithTemplate("{dea.certificate.file.not.null}");
        Mockito.verify(context).buildConstraintViolationWithTemplate("{dea.certificate.number.not.null}");
        Mockito.verify(context).buildConstraintViolationWithTemplate("{dea.certificate.expiration.not.null}");
    }

    @Test
    public void testValidateDDS() {
        boolean result = testClass.validateDDS(model, context);

        Assert.assertFalse(result);
        Mockito.verify(context).buildConstraintViolationWithTemplate("{dds.certificate.file.not.null}");
        Mockito.verify(context).buildConstraintViolationWithTemplate("{dds.certificate.number.not.null}");
    }

    @Test
    public void testValidateRDH() {
        boolean result = testClass.validateRDH(model, context);

        Assert.assertFalse(result);
        Mockito.verify(context).buildConstraintViolationWithTemplate("{rdh.certificate.file.not.null}");
        Mockito.verify(context).buildConstraintViolationWithTemplate("{rdh.certificate.number.not.null}");
        Mockito.verify(context).buildConstraintViolationWithTemplate("{rdh.certificate.expiration.not.null}");
    }

    @Test
    public void testValidateRDA() {
        boolean result = testClass.validateRDA(model, context);

        Assert.assertFalse(result);
        Mockito.verify(context).buildConstraintViolationWithTemplate("{rda.certificate.file.not.null}");
        Mockito.verify(context).buildConstraintViolationWithTemplate("{rda.certificate.number.not.null}");
        Mockito.verify(context).buildConstraintViolationWithTemplate("{rda.certificate.expiration.not.null}");
    }

    @Test
    public void testValidateLiability() {
        Mockito.doReturn(null).when(testClass).findCertificateByType(CertificateType.CPR);

        boolean result = testClass.validateLiability(model, context);

        Assert.assertFalse(result);
        Mockito.verify(context).buildConstraintViolationWithTemplate("{liability.certificate.file.not.null}");
        Mockito.verify(context).buildConstraintViolationWithTemplate("{liability.certificate.expiration.not.null}");
    }

    @Test
    public void testValidateCPR() {
        Mockito.doReturn(null).when(testClass).findCertificateByType(CertificateType.CPR);

        boolean result = testClass.validateCPR(model, context);

        Assert.assertFalse(result);
        Mockito.verify(context).buildConstraintViolationWithTemplate("{crp.certificate.certificate.not.null}");
        Mockito.verify(context).buildConstraintViolationWithTemplate("{crp.certificate.date.not.null}");
    }

    @Test
    public void testConstrainMessage() {
        String message = "message";

        boolean result = testClass.constrainMessage(true, message, context);

        Assert.assertFalse(result);
        Mockito.verify(context).disableDefaultConstraintViolation();
        Mockito.verify(context).buildConstraintViolationWithTemplate(message);
        Mockito.verify(constraintViolationBuilder).addConstraintViolation();
    }

    @Test
    public void testFindCertificateByType() {
        String type = CertificateType.CPR;
        CertificateModel model = new CertificateModel();
        CertificateDetails certificateDetails = new CertificateDetails();
        Mockito.when(certificateDetailsDao.findByProfessionalIdAndCertificateType(professional.getId(), type)).thenReturn(certificateDetails);

        CertificateDetails result = testClass.findCertificateByType(type);

        Assert.assertSame(result, certificateDetails);
    }
}