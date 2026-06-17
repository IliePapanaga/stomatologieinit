package com.cl.mdd.server.mvc.captcha.local;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.text.producer.TextProducer;
import com.cl.mdd.server.mvc.captcha.CaptchaException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageCaptchaTest {

    @InjectMocks
    private ImageCaptcha validator;

    @Mock
    private PasswordEncoder captchaResponseEncoder;

    @Mock
    private TextProducer textProducer;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Captor
    private ArgumentCaptor<Captcha> captchaCaptor;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    private String responseParameter = "rp";

    private String headerName = "hn";

    private String cookieName = "cn";

    private int cookieAge = 123;

    private int width = 52;

    private int height = 53;

    private String answer = "abc";

    private Captcha captcha;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(textProducer.getText()).thenReturn(answer);

        ReflectionTestUtils.setField(validator, "responseParameter", responseParameter);
        ReflectionTestUtils.setField(validator, "headerName", headerName);
        ReflectionTestUtils.setField(validator, "cookieName", cookieName);
        ReflectionTestUtils.setField(validator, "cookieAge", cookieAge);
        ReflectionTestUtils.setField(validator, "width", width);
        ReflectionTestUtils.setField(validator, "height", height);
        validator = Mockito.spy(validator);
        captcha = new Captcha.Builder(width, height).addText(textProducer).build();
    }

    @Test
    public void testVerifySkip() {
        Assert.assertFalse(validator.verify(httpServletRequest));
    }

    @Test
    public void testVerifyNoAnswer() {
        Mockito.when(httpServletRequest.getParameter(responseParameter)).thenReturn("abc");
        try {
            validator.verify(httpServletRequest);
            Assert.fail();
        } catch (CaptchaException e) {
            Assert.assertEquals("E_WEB_CAPTCHA_LOCAL_INCOMPLETE", e.getCode());
        }
    }

    @Test
    public void testVerifyWrongAnswer() {
        Mockito.when(httpServletRequest.getParameter(responseParameter)).thenReturn("abc");
        Mockito.doReturn("xyz").when(validator).read(httpServletRequest);
        try {
            validator.verify(httpServletRequest);
            Assert.fail();
        } catch (CaptchaException e) {
            Assert.assertEquals("E_WEB_CAPTCHA_LOCAL_ERROR", e.getCode());
        }
    }

    @Test
    public void testVerify() {
        Mockito.when(httpServletRequest.getParameter(responseParameter)).thenReturn(answer);
        Mockito.doReturn("xyz").when(validator).read(httpServletRequest);
        Mockito.when(captchaResponseEncoder.matches(answer, "xyz")).thenReturn(true);
        Assert.assertTrue(validator.verify(httpServletRequest));
    }

    @Test
    public void testReadCookie() {
        Mockito.when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{new Cookie(cookieName, "xyz")});
        Assert.assertEquals("xyz", validator.read(httpServletRequest));
    }

    @Test
    public void testReadHeader() {
        Mockito.when(httpServletRequest.getHeader(headerName)).thenReturn("xyz");
        Assert.assertEquals("xyz", validator.read(httpServletRequest));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testClientKey() {
        validator.clientKey();
    }

    @Test
    public void testBuild() {
        Captcha captcha = validator.build();
        Assert.assertEquals(answer, captcha.getAnswer());
        Assert.assertEquals(width, captcha.getImage().getWidth());
        Assert.assertEquals(height, captcha.getImage().getHeight());
    }

    @Test
    public void testSaveAnswer() {
        Mockito.when(captchaResponseEncoder.encode(answer)).thenReturn("xyz");
        validator.saveAnswer(captcha, httpServletResponse);
        Mockito.verify(httpServletResponse).addCookie(cookieCaptor.capture());
        Mockito.verify(httpServletResponse).addHeader(headerName, "xyz");
        Assert.assertEquals(cookieName, cookieCaptor.getValue().getName());
        Assert.assertEquals(cookieAge, cookieCaptor.getValue().getMaxAge());
        Assert.assertTrue(StringUtils.endsWith(cookieCaptor.getValue().getPath(), "/"));
        Assert.assertEquals("xyz", cookieCaptor.getValue().getValue());
    }

    @Test
    public void testSend() {
        Mockito.doReturn(captcha).when(validator).build();
        Mockito.doNothing().when(validator).saveAnswer(captcha, httpServletResponse);
        Mockito.doNothing().when(validator).write(captcha, httpServletResponse);
        validator.send(httpServletResponse);
        InOrder order = Mockito.inOrder(validator);
        order.verify(validator).build();
        order.verify(validator).saveAnswer(captcha, httpServletResponse);
        order.verify(validator).write(captcha, httpServletResponse);
    }

}
