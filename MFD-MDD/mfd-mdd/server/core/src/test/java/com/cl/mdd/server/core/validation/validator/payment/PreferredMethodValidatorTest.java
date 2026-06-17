package com.cl.mdd.server.core.validation.validator.payment;

import com.cl.mdd.server.core.data.model.payment.CreditCard;
import com.cl.mdd.server.core.data.model.payment.PaymentInstrumentBase;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentMethodDao;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethod;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethodCard;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class PreferredMethodValidatorTest {

    @InjectMocks
    private PreferredMethodValidator validator;

    @Mock
    private PaymentMethodDao dao;

    private PaymentInstrumentBase instrument = new CreditCard().withId("52");

    private PaymentMethod method1 = new PaymentMethodCard();
    private PaymentMethod method2 = new PaymentMethodCard();

    private Practice practice = new Practice();

    @Test
    public void testIsValidSkip() {
        Assert.assertTrue(validator.isValid(null, null));
        Assert.assertTrue(validator.isValid(instrument, null));
        Assert.assertTrue(validator.isValid(instrument.withPreferred(true), null));
        Assert.assertTrue(validator.isValid(instrument.withId(null), null));
    }

    @Test
    public void testIsValid() {
        method1.setPreferred(true);
        method1.setId(instrument.getId());
        method1.setPractice(practice);
        Mockito.when(dao.findOne(instrument.getId())).thenReturn(method1);
        Mockito.when(dao.findByPracticeAndLabelNotNullOrderByPreferredDesc(practice))
                .thenReturn(Arrays.asList(method1, method2));
        Assert.assertFalse(validator.isValid(instrument, null));
    }
}
