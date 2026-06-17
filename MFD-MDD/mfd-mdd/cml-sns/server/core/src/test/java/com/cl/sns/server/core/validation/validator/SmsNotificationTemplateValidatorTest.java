package com.cl.sns.server.core.validation.validator;

import com.cl.sns.server.core.model.api.template.BaseNotificationTemplateModel;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SmsNotificationTemplateValidatorTest {

    private SmsNotificationTemplateValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Before
    public void setUp() throws Exception {
        validator = new SmsNotificationTemplateValidator("SMS", 100);

        doReturn(builder).when(context).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    public void isValid_whenNotEmailTransport_returnTrue() {
        BaseNotificationTemplateModel model = new NotificationTemplateModel();
        model.setTransport("EMAIL");

        assertTrue(validator.isValid(model, context));

        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    public void isValid_whenEmailTransportAndContentIsLongerThanMax_returnFalse() {
        BaseNotificationTemplateModel model = new NotificationTemplateModel();
        model.setTransport("SMS");
        model.setContent(randomAlphanumeric(120));

        assertFalse(validator.isValid(model, context));

        verify(context).buildConstraintViolationWithTemplate(anyString());
        verify(builder).addConstraintViolation();
    }

    @Test
    public void isValid_whenEmailTransportAndContentIsLessaThanMax_returnTrue() {
        BaseNotificationTemplateModel model = new NotificationTemplateModel();
        model.setTransport("SMS");
        model.setContent("Less than 100");

        assertTrue(validator.isValid(model, context));

        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }
}