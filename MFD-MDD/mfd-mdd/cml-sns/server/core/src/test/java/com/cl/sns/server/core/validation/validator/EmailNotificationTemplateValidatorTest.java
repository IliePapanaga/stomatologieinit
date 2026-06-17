package com.cl.sns.server.core.validation.validator;

import com.cl.sns.server.core.model.api.template.BaseNotificationTemplateModel;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmailNotificationTemplateValidatorTest {

    private EmailNotificationTemplateValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Before
    public void setUp() throws Exception {
        validator = new EmailNotificationTemplateValidator("EMAIL");

        doReturn(builder).when(context).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    public void isValid_whenNotEmailTransport_returnTrue() {
        BaseNotificationTemplateModel model = new NotificationTemplateModel();
        model.setTransport("SMS");

        assertTrue(validator.isValid(model, context));

        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    public void isValid_whenEmailTransportAndSubjectIsEmpty_returnFalse() {
        BaseNotificationTemplateModel model = new NotificationTemplateModel();
        model.setTransport("EMAIL");
        model.setSubject("");

        assertFalse(validator.isValid(model, context));

        verify(context).buildConstraintViolationWithTemplate(anyString());
        verify(builder).addConstraintViolation();
    }

    @Test
    public void isValid_whenEmailTransportAndSubjectIsNotEmpty_returnTrue() {
        BaseNotificationTemplateModel model = new NotificationTemplateModel();
        model.setTransport("EMAIL");
        model.setSubject("Not Empty Subject");

        assertTrue(validator.isValid(model, context));

        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }
}