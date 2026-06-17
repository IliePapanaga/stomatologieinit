package com.cl.sns.server.core.validation.validator;

import com.cl.sns.server.core.manager.NotificationTemplateManager;
import com.cl.sns.server.core.model.api.template.CreateNotificationTemplate;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NotificationTemplateUniquenessValidatorTest {

    private NotificationTemplateUniquenessValidator validator;
    
    @Mock
    private NotificationTemplateManager notificationTemplateManager;

    @Mock
    private ConstraintValidatorContext context;

    @Before
    public void setUp() throws Exception {
        validator = new NotificationTemplateUniquenessValidator(notificationTemplateManager);
    }

    @Test
    public void isValid_whenTypeIsEmpty_returnTrue() {
        NotificationTemplateModel model = new NotificationTemplateModel();
        model.setType("");

        assertTrue(validator.isValid(model, context));
    }

    @Test
    public void isValid_whenTransportIsEmpty_returnTrue() {
        NotificationTemplateModel model = new NotificationTemplateModel();
        model.setType("Type");
        model.setTransport("");

        assertTrue(validator.isValid(model, context));
    }

    @Test
    public void isValid_whenTemplateIsNewAndNoExistingTemplate_returnTrue() {
        CreateNotificationTemplate model = new CreateNotificationTemplate();
        model.setType("Type");
        model.setTransport("Transport");

        doReturn(Collections.emptyList()).when(notificationTemplateManager).findByTypeAndTransport(anyString(), anyString());

        assertTrue(validator.isValid(model, context));

        verify(notificationTemplateManager).findByTypeAndTransport("Type", "Transport");
    }

    @Test
    public void isValid_whenTemplateIsNewAndExistTemplate_returnFalse() {
        CreateNotificationTemplate model = new CreateNotificationTemplate();
        model.setType("Type");
        model.setTransport("Transport");

        doReturn(Collections.singletonList(new NotificationTemplateModel())).when(notificationTemplateManager).findByTypeAndTransport(anyString(), anyString());

        assertFalse(validator.isValid(model, context));

        verify(notificationTemplateManager).findByTypeAndTransport("Type", "Transport");
    }

    @Test
    public void isValid_whenTemplateIsNotNewAndExistOtherTemplate_returnFalse() {
        NotificationTemplateModel model = new NotificationTemplateModel();
        model.setType("Type");
        model.setTransport("Transport");
        model.setId("ID");

        doReturn(Collections.singletonList(new NotificationTemplateModel())).when(notificationTemplateManager).findByTypeAndTransport(anyString(), anyString());

        assertFalse(validator.isValid(model, context));

        verify(notificationTemplateManager).findByTypeAndTransport("Type", "Transport");
    }

    @Test
    public void isValid_whenTemplateIsNotNewAndNoOtherTemplates_returnTrue() {
        NotificationTemplateModel model = new NotificationTemplateModel();
        model.setType("Type");
        model.setTransport("Transport");
        model.setId("ID");

        doReturn(Collections.emptyList()).when(notificationTemplateManager).findByTypeAndTransport(anyString(), anyString());

        assertTrue(validator.isValid(model, context));

        verify(notificationTemplateManager).findByTypeAndTransport("Type", "Transport");
    }

    @Test
    public void isValid_whenTemplateIsNotNewAndOnlySameIDTemplateExist_returnTrue() {
        NotificationTemplateModel model = new NotificationTemplateModel();
        model.setType("Type");
        model.setTransport("Transport");
        model.setId("ID");

        NotificationTemplateModel persisted = new NotificationTemplateModel();
        persisted.setId("ID");

        doReturn(Collections.singletonList(persisted)).when(notificationTemplateManager).findByTypeAndTransport(anyString(), anyString());

        assertTrue(validator.isValid(model, context));

        verify(notificationTemplateManager).findByTypeAndTransport("Type", "Transport");
    }
}