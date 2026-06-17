package com.cl.mdd.server.core.service.payment.impl.primerate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GatewayClientTest {

    @Mock
    private RestTemplate primerateRestTemplate;

    @InjectMocks
    private GatewayClient client;

    @Test
    public void testApiKey() {
        assertEquals("abc", client.apiKey(new Credentials(null, null, "abc")));
    }

    @Test(expected = IllegalStateException.class)
    public void testApiKeyMissing() {
        client.apiKey(new Credentials(null, null, " "));
    }
}
