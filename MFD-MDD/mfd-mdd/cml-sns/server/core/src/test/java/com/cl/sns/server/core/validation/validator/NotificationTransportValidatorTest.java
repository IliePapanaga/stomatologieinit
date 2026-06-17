package com.cl.sns.server.core.validation.validator;

import com.cl.sns.server.core.dao.MessagingTransportDao;
import com.cl.sns.server.core.model.db.msg.MessagingTransport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class NotificationTransportValidatorTest {

    private NotificationTransportValidator validator;

    @Mock
    private MessagingTransportDao messagingTransportDao;

    @Mock
    private ConstraintValidatorContext context;

    @Before
    public void setUp() throws Exception {
        validator = new NotificationTransportValidator(messagingTransportDao);
    }

    @Test
    public void isValid_whenValueIsBlank_returnTrue() {
        assertTrue(validator.isValid("", context));

        verifyZeroInteractions(messagingTransportDao);
    }

    @Test
    public void isValid_whenMessageTransportAvailable_returnTrue() {
        doReturn(new MessagingTransport()).when(messagingTransportDao).findByName(anyString());

        assertTrue(validator.isValid("SMS", context));

        verify(messagingTransportDao).findByName("SMS");
    }

    @Test
    public void isValid_whenMessageTransportNotAvailable_returnFalse() {
        doReturn(null).when(messagingTransportDao).findByName(anyString());

        assertFalse(validator.isValid("SMS", context));

        verify(messagingTransportDao).findByName("SMS");
    }
}