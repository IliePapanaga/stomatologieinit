package com.cl.mdd.server.core.service.notification.client.embedded;

import com.amazonaws.AmazonServiceException;
import com.cl.mdd.server.core.service.notification.Notification;
import com.cl.mdd.server.core.service.notification.NotificationServiceException;
import com.cl.sns.server.core.model.api.notification.SendNotificationRequest;
import com.cl.sns.server.core.service.NotificationService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EmbeddedNotificationServiceClientTest {

    private EmbeddedNotificationServiceClient client;

    @Mock
    private NotificationService notificationService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        client = new EmbeddedNotificationServiceClient(notificationService);
    }

    @Test
    public void send_convertToRequestAndCallSendOnNotificationService() {
        ArgumentCaptor<SendNotificationRequest> requestArgumentCaptor = ArgumentCaptor.forClass(SendNotificationRequest.class);

        Notification notification = new Notification();
        notification.setType("TYPE");
        notification.setEmail("E-MAIL");
        notification.setPhone("1234567890");
        notification.setContext(new HashMap<String, String>() {
            {
                put("KEY1", "VALUE1");
                put("KEY2", "VALUE2");
            }
        });

        client.send(notification);

        verify(notificationService).send(requestArgumentCaptor.capture());

        SendNotificationRequest request = requestArgumentCaptor.getValue();

        assertNotNull(request);
        assertEquals("TYPE", request.getNotificationType());
        assertNotNull(request.getRecipientDetails());
        assertEquals("E-MAIL", request.getRecipientDetails().getEmail());
        assertEquals("1234567890", request.getRecipientDetails().getPhone());
        assertNotNull(request.getRecipientDetails().getPlaceHolders());
        assertEquals("VALUE1", request.getRecipientDetails().getPlaceHolders().get("KEY1"));
        assertEquals("VALUE2", request.getRecipientDetails().getPlaceHolders().get("KEY2"));
    }

    @Test
    public void send_whenAmazonException_wrapToNotificationServiceException() {
        doThrow(new AmazonServiceException("error")).when(notificationService).send(any());

        expectedException.expect(NotificationServiceException.class);

        client.send(new Notification());
    }
}