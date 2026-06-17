package com.cl.mdd.server.core.service.notification.client.embedded;

import com.cl.mdd.server.core.data.model.notification.FindNotificationTemplatesQuery;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModel;
import com.cl.mdd.server.core.service.notification.NotificationServiceException;
import com.cl.sns.server.core.model.api.template.CreateNotificationTemplate;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModelList;
import com.cl.sns.server.core.model.db.msg.NotificationTemplate;
import com.cl.sns.server.core.service.NotificationTemplateService;
import com.cl.sns.server.core.service.common.SnsApplicationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RunWith(MockitoJUnitRunner.class)
public class EmbeddedNotificationTemplateServiceClientTest {

    private EmbeddedNotificationTemplateServiceClient client;

    @Mock
    private NotificationTemplateService notificationTemplateService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        client = new EmbeddedNotificationTemplateServiceClient(notificationTemplateService);
    }

    @Test
    public void save_whenNew_callSaveWithCreateModel() {
        NotificationTemplateModel toCreate = createRandom();

        doAnswer(invocation -> {
            CreateNotificationTemplate create = invocation.getArgumentAt(0, CreateNotificationTemplate.class);
            return new com.cl.sns.server.core.model.api.template.NotificationTemplateModel().setId("NEW")
                    .setName(create.getName())
                    .setType(create.getType())
                    .setTransport(create.getTransport())
                    .setContent(create.getContent())
                    .setSubject(create.getSubject())
                    .setDescription(create.getDescription());
        }).when(notificationTemplateService).save(any());
        
        NotificationTemplateModel result = client.save(toCreate);

        verify(notificationTemplateService).save(any(CreateNotificationTemplate.class));

        assertNotNull(result);
        toCreate.setId("NEW");

        assertEquals(toCreate, result);
    }

    @Test
    public void save_whenUpdate_callUpdateWithNormalModel() {
        NotificationTemplateModel toUpdate = createRandom();
        toUpdate.setId("NEW");

        doAnswer(invocation -> invocation.getArgumentAt(0, com.cl.sns.server.core.model.api.template.NotificationTemplateModel.class)).when(notificationTemplateService).update(any());

        NotificationTemplateModel result = client.save(toUpdate);

        verify(notificationTemplateService).update(any(com.cl.sns.server.core.model.api.template.NotificationTemplateModel.class));

        assertNotNull(result);
        assertEquals(toUpdate, result);
    }

    @Test
    public void save_whenUpdateFailedWithException_rethrowWrappedException() {
        NotificationTemplateModel toUpdate = createRandom();
        toUpdate.setId("NEW");

        doThrow(new SnsApplicationException("message", "code")).when(notificationTemplateService).update(any());

        expectedException.expect(NotificationServiceException.class);

        client.update(toUpdate);
    }

    @Test
    public void get_callGetAndConvertResult() {
        com.cl.sns.server.core.model.api.template.NotificationTemplateModel fromService = createRandomClientModel();

        doReturn(fromService).when(notificationTemplateService).get(anyString());

        NotificationTemplateModel result = client.get("ID");

        verify(notificationTemplateService).get("ID");

        assertNotNull(result);
        assertEquals(fromService.getId(), result.getId());
        assertEquals(fromService.getName(), result.getName());
        assertEquals(fromService.getDescription(), result.getDescription());
        assertEquals(fromService.getContent(), result.getContent());
        assertEquals(fromService.getSubject(), result.getSubject());
        assertEquals(fromService.getTransport(), result.getTransport());
        assertEquals(fromService.getType(), result.getType());
    }

    @Test
    public void list_callListWithPageRequestAndConvertResult() {
        doReturn(new NotificationTemplateModelList(1L, Arrays.asList(new com.cl.sns.server.core.model.api.template.NotificationTemplateModel())))
                .when(notificationTemplateService).list(any());

        client.list(1, 2, Arrays.asList(FindNotificationTemplatesQuery.NotificationTemplatesOrder.DESCRIPTION_ASC,
                FindNotificationTemplatesQuery.NotificationTemplatesOrder.NAME_DESC));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(notificationTemplateService).list(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(1, pageable.getPageNumber());
        assertEquals(2, pageable.getPageSize());

        Sort.Order nameOrder = pageable.getSort().getOrderFor("name");

        assertNotNull(nameOrder);
        assertEquals(DESC, nameOrder.getDirection());

        Sort.Order descriptionOrder = pageable.getSort().getOrderFor("description");

        assertNotNull(descriptionOrder);
        assertEquals(ASC, descriptionOrder.getDirection());
    }

    @Test
    public void delete() {
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);

        client.delete("ID");

        verify(notificationTemplateService).delete(idCaptor.capture());

        assertEquals("ID", idCaptor.getValue());
    }

    private NotificationTemplateModel createRandom() {
        return new NotificationTemplateModel()
                .setType(randomAlphanumeric(5))
                .setTransport(randomAlphanumeric(10))
                .setName(randomAlphanumeric(15))
                .setDescription(randomAlphanumeric(20))
                .setSubject(randomAlphanumeric(25))
                .setContent(randomAlphanumeric(30));
    }

    private com.cl.sns.server.core.model.api.template.NotificationTemplateModel createRandomClientModel() {
        com.cl.sns.server.core.model.api.template.NotificationTemplateModel model = new com.cl.sns.server.core.model.api.template.NotificationTemplateModel();
        model.setId(randomAlphanumeric(4))
                .setName(randomAlphanumeric(5))
                .setDescription(randomAlphanumeric(6))
                .setSubject(randomAlphanumeric(7))
                .setContent(randomAlphanumeric(8))
                .setTransport(randomAlphanumeric(9))
                .setType(randomAlphanumeric(10));
        return model;
    }
}