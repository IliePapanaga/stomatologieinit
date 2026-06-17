package com.cl.mdd.server.mvc.captcha.recaptcha;

import com.cl.mdd.server.mvc.captcha.CaptchaException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;

public class ReCaptchaTest {

    @InjectMocks
    private ReCaptcha reCaptcha;

    @Mock
    private ReCaptchaVerify reCaptchaVerify;

    @Mock
    private HttpServletRequest httpServletRequest;

    private ReCaptchaResponse response = new ReCaptchaResponse();

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(reCaptcha, "secret", "s");
        ReflectionTestUtils.setField(reCaptcha, "site", "123");
        ReflectionTestUtils.setField(reCaptcha, "reCaptchaHeader", "p");

        Mockito.when(httpServletRequest.getHeader("p")).thenReturn("v");
        Mockito.when(reCaptchaVerify.verify("s", "v")).thenReturn(response);
    }

    @Test(expected = CaptchaException.class)
    public void verifyFails() {
        reCaptcha.verify(httpServletRequest);
    }

    @Test
    public void verifySkip() {
        Mockito.reset(httpServletRequest);
        Assert.assertFalse(reCaptcha.verify(httpServletRequest));
        Mockito.verifyZeroInteractions(reCaptchaVerify);
    }

    @Test
    public void verify() {
        response.setSuccess(true);
        Assert.assertTrue(reCaptcha.verify(httpServletRequest));
    }

    @Test
    public void testClientKey() {
        Assert.assertEquals("123", reCaptcha.clientKey());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSend() {
        reCaptcha.send(null);
    }

}
