package com.cl.mdd.server.core.service.notification.impl;

import com.cl.mdd.server.core.data.model.notification.FindNotificationTemplatesQuery;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModel;
import com.cl.mdd.server.core.data.model.notification.NotificationTypeDescriptorModel;
import com.cl.mdd.server.core.service.notification.client.NotificationTemplateServiceClient;
import com.cl.mdd.server.core.service.notification.definition.impl.NotificationTypeDescriptorRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificationTemplateServiceImplTest {

    private NotificationTemplateServiceImpl service;

    @Mock
    private NotificationTemplateServiceClient client;

    @Mock
    private NotificationTypeDescriptorRegistry registry;

    @Before
    public void setUp() throws Exception {
        service = new NotificationTemplateServiceImpl(client, registry);
    }

    @Test
    public void save_whenTypeIsPresent_shouldUpdateNameAndDescription() {
        NotificationTemplateModel model = mock(NotificationTemplateModel.class);
        doReturn("type1").when(model).getType();

        NotificationTypeDescriptorModel descriptor = new NotificationTypeDescriptorModel();
        descriptor.setName("descriptor_name");
        descriptor.setDescription("descriptor_description");

        doReturn(Optional.ofNullable(descriptor)).when(registry).byType("type1");

        service.save(model);

        verify(model).setName("descriptor_name");
        verify(model).setDescription("descriptor_description");
        verify(client).save(model);
    }

    @Test
    public void save_whenTypeIsNotPresent_shouldSaveAsIs() {
        NotificationTemplateModel model = mock(NotificationTemplateModel.class);
        doReturn("type1").when(model).getType();

        doReturn(Optional.ofNullable(null)).when(registry).byType("type1");

        service.save(model);

        verify(model, never()).setName(anyString());
        verify(model, never()).setDescription(anyString());
        verify(client).save(model);
    }

    @Test
    public void get() {
        NotificationTemplateModel expected = new NotificationTemplateModel();
        doReturn(expected).when(client).get(eq("ID"));

        NotificationTemplateModel actual = service.get("ID");

        assertSame(expected, actual);
    }

    @Test
    public void list() {
        List<FindNotificationTemplatesQuery.NotificationTemplatesOrder> orders = Arrays.asList(FindNotificationTemplatesQuery.NotificationTemplatesOrder.NAME_DESC,
                FindNotificationTemplatesQuery.NotificationTemplatesOrder.DESCRIPTION_ASC);

        service.list(5, 10, orders);

        verify(client).list(5, 10, orders);
    }

    @Test
    public void delete() {
        service.delete("ID");
        verify(client).delete("ID");
    }

    @Test
    public void descriptors() {
        List<NotificationTypeDescriptorModel> expected = Arrays.asList(new NotificationTypeDescriptorModel(),
                new NotificationTypeDescriptorModel());
        doReturn(expected).when(registry).descriptors();

        List<NotificationTypeDescriptorModel> actual = service.descriptors();

        assertEquals(expected, actual);
    }

    @Test
    public void descriptorByType_whenDescriptorIsFound_returnDescriptor() {
        NotificationTypeDescriptorModel expected = new NotificationTypeDescriptorModel();

        doReturn(Optional.ofNullable(expected)).when(registry).byType(eq("type1"));

        assertSame(expected, service.descriptorByType("type1"));
    }

    @Test
    public void descriptorByType_whenDescriptorIsNotFound_returnNull() {
        doReturn(Optional.ofNullable(null)).when(registry).byType(anyString());

        assertNull(service.descriptorByType("type1"));
    }
}