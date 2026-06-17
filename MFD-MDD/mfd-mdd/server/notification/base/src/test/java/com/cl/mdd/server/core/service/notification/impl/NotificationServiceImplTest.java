package com.cl.mdd.server.core.service.notification.impl;

import com.cl.mdd.server.core.service.notification.Notification;
import com.cl.mdd.server.core.service.notification.client.NotificationServiceClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceImplTest {

    private NotificationServiceImpl service;

    @Mock
    private NotificationServiceClient client;

    @Before
    public void setUp() throws Exception {
        service = new NotificationServiceImpl(client);
    }

    @Test
    public void send() {
        Notification notification = new Notification();

        service.send(notification);

        verify(client).send(notification);
    }
}