package com.cl.mdd.server.core.validation.validator.payment;

import com.cl.mdd.server.core.data.model.settings.SystemSettingModel;
import com.cl.mdd.server.core.service.payment.impl.primerate.Credentials;
import com.cl.mdd.server.core.service.payment.impl.primerate.GatewayClient;
import com.cl.mdd.server.core.settings.SystemSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static com.cl.mdd.server.core.settings.Settings.PaymentPrimeRateSettings.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GatewayCredentialsValidatorTest {

    @InjectMocks
    private GatewayCredentialsValidator validator;

    @Mock
    private SystemSettings systemSettings;

    @Mock
    private GatewayClient gateway;

    @Captor
    private ArgumentCaptor<Credentials> credentialsCaptor;

    @Test
    public void testPasswordValueAsIs() {
        assertEquals("abc", validator.passwordValue("abc"));
        verifyZeroInteractions(systemSettings);
    }

    @Test
    public void testPasswordValueFromDb() {
        when(systemSettings.getString(PASSWORD.getKey())).thenReturn("abc");
        assertEquals("abc", validator.passwordValue(SystemSettingModel.ENCRYPTED_VALUE_MASK));
    }

    @Test
    public void testIsValidNoProperties() {
        assertTrue(validator.isValid(null, null));
        assertTrue(validator.isValid(Collections.emptyList(), null));
    }

    private SystemSettingModel setting(String key, String value) {
        SystemSettingModel model = new SystemSettingModel();
        model.setKey(key);
        model.setValue(value);
        return model;
    }

    @Test
    public void testIsValidBadLoginPass() {
        when(gateway.validateLoginPass(credentialsCaptor.capture())).thenReturn(false);
        assertFalse(validator.isValid(
                Arrays.asList(setting(LOGIN.getKey(), "l"), setting(PASSWORD.getKey(), "p")), null));
        verify(gateway, never()).validateApiKey(any(), any());
        assertEquals("l", credentialsCaptor.getValue().getLogin());
        assertEquals("p", credentialsCaptor.getValue().getPassword());
    }

    @Test
    public void testIsValidBadKey() {
        when(gateway.validateApiKey(credentialsCaptor.capture(), any())).thenReturn(false);
        assertFalse(validator.isValid(Collections.singletonList(setting(API_KEY.getKey(), "k")), null));
        verify(gateway, never()).validateLoginPass(any());
        assertEquals("k", credentialsCaptor.getValue().getApiKey());
    }

    @Test
    public void testIsValid() {
        when(gateway.validateLoginPass(credentialsCaptor.capture())).thenReturn(true);
        when(gateway.validateApiKey(credentialsCaptor.capture(), any())).thenReturn(true);
        assertTrue(validator.isValid(Arrays.asList(
                setting(LOGIN.getKey(), "l"), setting(PASSWORD.getKey(), "p"), setting(API_KEY.getKey(), "k")), null));
        assertEquals("l", credentialsCaptor.getValue().getLogin());
        assertEquals("p", credentialsCaptor.getValue().getPassword());
        assertEquals("k", credentialsCaptor.getValue().getApiKey());
    }
}
