package com.cl.mdd.server.core.service.payment.impl;

import com.cl.mdd.server.core.data.persistent.access.payment.PaymentAttemptDao;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentDao;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentMethodDao;
import com.cl.mdd.server.core.data.persistent.access.practice.PracticeDao;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethodAch;
import com.cl.mdd.server.core.data.persistent.model.payment.PaymentMethodCard;
import com.cl.mdd.server.core.event.impl.bus.payment.PaymentStatusEventBus;
import com.cl.mdd.server.core.service.payment.impl.primerate.Credentials;
import com.cl.mdd.server.core.service.payment.impl.primerate.GatewayClient;
import com.cl.mdd.server.core.settings.SystemSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static com.cl.mdd.server.core.data.persistent.model.payment.Payment.STATUS_FAILED;
import static com.cl.mdd.server.core.data.persistent.model.payment.Payment.STATUS_FAILED_FINAL;
import static com.cl.mdd.server.core.settings.Settings.PaymentPrimeRateSettings.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentProcessorTest {

    @Spy
    @InjectMocks
    private PaymentProcessor processor;

    @Mock
    private PaymentAttemptDao paymentAttemptDao;
    
    @Mock
    private PaymentMethodDao paymentMethodDao;
    
    @Mock
    private PaymentDao paymentDao;
    
    @Mock
    private PracticeDao practiceDao;
    
    @Mock
    private GatewayClient gateway;
    
    @Mock
    private SystemSettings systemSettings;

    @Mock
    private PaymentStatusEventBus eventBus;

    @Test
    public void testCredentials() {
        when(systemSettings.getString(eq(LOGIN.getKey()))).thenReturn("l");
        when(systemSettings.getString(eq(PASSWORD.getKey()))).thenReturn("p");
        when(systemSettings.getString(eq(API_KEY.getKey()))).thenReturn("k");
        Credentials credentials = processor.credentials();
        assertEquals("l", credentials.getLogin());
        assertEquals("p", credentials.getPassword());
        assertEquals("k", credentials.getApiKey());
    }

    @Test
    public void testFailedStatus() {
        assertEquals(STATUS_FAILED_FINAL, processor.failedStatus(null, 1, 0, 1, 0));
        assertEquals(STATUS_FAILED, processor.failedStatus(Collections.singleton(new PaymentMethodAch()), 1, 0, 2, 0));
        assertEquals(STATUS_FAILED_FINAL, processor.failedStatus(null, 0, 1, 0, 1));
        assertEquals(STATUS_FAILED, processor.failedStatus(Collections.singleton(new PaymentMethodCard()), 0, 1, 0, 2));
    }
    
}
