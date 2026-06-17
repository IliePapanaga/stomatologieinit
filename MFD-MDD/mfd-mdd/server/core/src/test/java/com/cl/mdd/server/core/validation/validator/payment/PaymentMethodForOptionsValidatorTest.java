package com.cl.mdd.server.core.validation.validator.payment;

import com.cl.mdd.server.core.data.persistent.access.payment.PaymentMethodDao;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethod;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethodAch;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethodCard;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PaymentMethodForOptionsValidatorTest {

    @InjectMocks
    private PaymentMethodForOptionsValidator validator;

    @Mock
    private PaymentMethodDao dao;

    private PaymentMethod card = new PaymentMethodCard();

    private PaymentMethod ach = new PaymentMethodAch();

    @Test
    public void testIsValidSkip() {
        Assert.assertTrue(validator.isValid(null, null));
        Assert.assertTrue(validator.isValid("", null));
    }

    @Test
    public void testIsValidNot() {
        Assert.assertFalse(validator.isValid("52", null));
        Mockito.when(dao.findOne("52")).thenReturn(ach);
        Assert.assertFalse(validator.isValid("52", null));
    }

    @Test
    public void testIsValid() {
        Mockito.when(dao.findOne("52")).thenReturn(card);
        Assert.assertTrue(validator.isValid("52", null));
    }

}
