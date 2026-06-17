package com.cl.mdd.server.core.validation.validator.payment;

import com.cl.mdd.server.core.data.model.payment.PaymentOptionsModel;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentDao;
import com.cl.mdd.server.core.data.persistent.model.payment.Payment;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentStatusOptionsValidatorTest {

    private static class Config {
        @Bean
        public PaymentDao paymentDao() {
            return mock(PaymentDao.class);
        }
        @Bean
        public PaymentStatusOptionsValidator validator() {
            return new PaymentStatusOptionsValidator();
        }
    }

    private PaymentStatusOptionsValidator validator;

    private Payment payment = new Payment();

    private PaymentOptionsModel options = new PaymentOptionsModel();

    @Before
    public void setUp() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
        validator = ctx.getBean(PaymentStatusOptionsValidator.class);
        PaymentDao dao = ctx.getBean(PaymentDao.class);
        validator.initialize(null);

        payment.setId("52");
        options.setPaymentId(payment.getId());
        when(dao.findOne(payment.getId())).thenReturn(payment);
    }

    @Test
    public void testIsValid() {
        payment.setStatus(Payment.STATUS_FAILED);
        options.setOption(PaymentOptionsModel.COMPLETE);
        assertTrue(validator.isValid(options, null));
    }

    @Test
    public void testIsValidNot() {
        payment.setStatus(Payment.STATUS_PAID);
        options.setOption(PaymentOptionsModel.CANCEL);
        assertFalse(validator.isValid(options, null));
    }

}
