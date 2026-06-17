package com.cl.sns.server.core.service.msg.sms.aws;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

public class AwsSmsMessageDetailsFactoryTest {

    private AwsSmsMessageDetailsFactory factory = new AwsSmsMessageDetailsFactory();

    @Test
    public void testNormalizeNullSafe() {
        assertNull(factory.normalize(null));
        assertEquals(" ", factory.normalize(" "));
    }

    @Test
    public void testNormalizeAsIs() {
        assertEquals("+12345678901", factory.normalize("+12345678901"));
    }

    @Test
    public void testNormalizeAddPlus() {
        assertEquals("+12345678901", factory.normalize("12345678901"));
    }

    @Test
    public void testNormalizeReplace00() {
        assertEquals("+12345678901", factory.normalize("0012345678901"));
    }

    @Test
    public void testNormalizeRemoveSymbols() {
        assertEquals("+12345678901", factory.normalize("(00)1-(234)567 8901 "));
    }

    @Test
    public void testNormalizeNoDefaultCodeSet() {
        assertEquals("+1234567890", factory.normalize("1234567890"));
    }

    @Test
    public void testNormalizeNoDefaultCodeApplied() {
        ReflectionTestUtils.setField(factory, "defaultCountryCode", "1");
        assertEquals("+01234567890", factory.normalize("01234567890"));
    }

    @Test
    public void testNormalizeAddDefaultCode() {
        ReflectionTestUtils.setField(factory, "defaultCountryCode", "1");
        assertEquals("+12345678901", factory.normalize("(234)567-8901"));
    }
}
