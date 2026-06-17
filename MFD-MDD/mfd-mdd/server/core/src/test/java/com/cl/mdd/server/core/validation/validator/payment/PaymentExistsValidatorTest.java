package com.cl.mdd.server.core.validation.validator.payment;

import com.cl.mdd.server.core.data.persistent.access.payment.PaymentDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PaymentExistsValidatorTest {

    @InjectMocks
    private PaymentExistsValidator validator;

    @Mock
    private PaymentDao dao;

    @Test
    public void testIsValidSkip() {
        Assert.assertTrue(validator.isValid(null, null));
        Assert.assertTrue(validator.isValid("", null));
    }

    @Test
    public void testIsValid() {
        Assert.assertFalse(validator.isValid("52", null));
        Mockito.when(dao.exists("52")).thenReturn(true);
        Assert.assertTrue(validator.isValid("52", null));
    }

}