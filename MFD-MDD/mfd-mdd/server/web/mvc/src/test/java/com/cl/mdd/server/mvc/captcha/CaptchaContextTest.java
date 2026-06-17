package com.cl.mdd.server.core.security.captcha;

import com.cl.mdd.server.mvc.captcha.CaptchaContext;
import com.cl.mdd.server.mvc.captcha.CaptchaValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.is;


@RunWith(MockitoJUnitRunner.class)
public class CaptchaContextTest {

    @Spy
    @InjectMocks

    private CaptchaContext captchaContext;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private CaptchaValidator captchaValidator;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(captchaContext, "httpServletRequest", httpServletRequest);
    }

    @Test
    public void setCaptchaPassed() {
        Assert.assertThat(captchaContext.isCaptchaPassed(), is(false));
    }

    @Test
    public void setCaptchaPassedAfterPopulation() {
        Mockito.when(captchaValidator.verify(httpServletRequest)).thenReturn(true);
        Assert.assertThat(captchaContext.isCaptchaPassed(), is(true));
    }

    @Test
    public void isCaptchaPassedAfterPopulationFalse() {
        Mockito.when(captchaValidator.verify(httpServletRequest)).thenReturn(false);
        Assert.assertThat(captchaContext.isCaptchaPassed(), is(false));
    }

}