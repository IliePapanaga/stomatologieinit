package com.cl.mdd.server.mvc.rest.notifications;

import com.cl.mdd.server.core.data.model.ErrorAssert;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModel;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.GraphQLRequestRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static com.cl.mdd.server.mvc.rest.GraphQLRequestRepository.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hsqldb-local")
public class NotificationTemplateIT extends BaseMvcIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void create_whenTemplateIsValid_createAndVerifyById() throws Exception {
        NotificationTemplateModel created = createNotificationTemplate(createNotificationTemplate("Template1"));

        MvcResult mvcResult = mockMvc.perform(getNotificationTemplateRequest(created.getId()).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        NotificationTemplateModel actual = valueFromPath("data.notificationTemplate", mvcResult.getResponse().getContentAsString(), NotificationTemplateModel.class);
        assertEquals(created, actual);
    }

    @Test
    public void create_whenNotUniqueTypeAndTransportPair_returnError() throws Exception {
        NotificationTemplateModel existing = createNotificationTemplate("Template1");
        existing.setType("SIGN_UP_FOR_TEST");
        createNotificationTemplate(existing);

        NotificationTemplateModel toCreate = createNotificationTemplate("Template1");
        toCreate.setType("SIGN_UP_FOR_TEST");

        MockHttpServletRequestBuilder request = addNotificationTemplateRequest(toCreate);

        MvcResult mvcResult = mockMvc.perform(request.with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
            .andExpect("Notification template should be unique in combination of transport and type", "save.arg0");
    }

    @Test
    public void create_whenTemplateIsInvalidWithMissingValues_returnError() throws Exception {
        NotificationTemplateModel toCreate = createNotificationTemplate("Template1");
        toCreate.setContent(null);
        toCreate.setSubject(null);
        toCreate.setType(null);
        toCreate.setName(null);
        toCreate.setTransport(null);

        MockHttpServletRequestBuilder request = addNotificationTemplateRequest(toCreate);

        MvcResult mvcResult = mockMvc.perform(request.with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Notification template content cannot be empty", "save.arg0.content")
                .andExpect("Notification template type cannot be empty", "save.arg0.type")
//                .andExpect("Notification template name cannot be empty", "save.arg0.name")
                .andExpect("Notification template transport cannot be empty", "save.arg0.transport");
    }
    @Test
    public void create_whenTemplateIsInvalidWithLongValues_returnError() throws Exception {
        NotificationTemplateModel toCreate = createNotificationTemplate("Template1");
        toCreate.setContent(randomAlphanumeric(5000));
        toCreate.setSubject(randomAlphanumeric(300));
        toCreate.setType(randomAlphanumeric(300));
        toCreate.setDescription(randomAlphanumeric(1200));
        toCreate.setName(randomAlphanumeric(300));

        MockHttpServletRequestBuilder request = addNotificationTemplateRequest(toCreate);

        MvcResult mvcResult = mockMvc.perform(request.with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Notification template content length should be from 1 to 4000 chars", "save.arg0.content")
                .andExpect("Notification template subject length should be from 1 to 255 chars", "save.arg0.subject")
                .andExpect("Notification template type length should be from 1 to 255 chars", "save.arg0.type")
                .andExpect("Notification template description length should not exceed 1000 chars", "save.arg0.description")
                .andExpect("Notification template name length should be from 1 to 255 chars", "save.arg0.name");
    }

    @Test
    public void create_whenTransportIsNotSupported_returnError() throws Exception {
        NotificationTemplateModel toCreate = createNotificationTemplate("Template1");
        toCreate.setTransport("Pigeon Mail");

        MockHttpServletRequestBuilder request = addNotificationTemplateRequest(toCreate);

        MvcResult mvcResult = mockMvc.perform(request.with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Notification transport Pigeon Mail is not available", "save.arg0.transport");
    }

    @Test
    public void create_whenSmsContentIsMore1400Chars_returnError() throws Exception {
        NotificationTemplateModel toCreate = createNotificationTemplate("Template1");
        toCreate.setTransport("SMS");
        toCreate.setContent(randomAlphanumeric(2000));

        MockHttpServletRequestBuilder request = addNotificationTemplateRequest(toCreate);

        MvcResult mvcResult = mockMvc.perform(request.with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
            .andExpect("Notification template content for SMS transport should be from 1 to 1400 chars", "save.arg0");
    }

    @Test
    public void update_whenTemplateNotAvailableById_returnError() throws Exception {
        NotificationTemplateModel toUpdate = createNotificationTemplate("Name1");
        toUpdate.setId("Some_Not_Valid_Id");

        MvcResult mvcResult = mockMvc.perform(updateNotificationTemplateRequest(toUpdate).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Non existent notification template id:Some_Not_Valid_Id", "updateNotificationTemplate");
    }

    @Test
    public void update_whenTemplateAvailableById_updateAndVerifyById() throws Exception {
        NotificationTemplateModel existing = createNotificationTemplate(createNotificationTemplate("Name1"));
        NotificationTemplateModel toUpdate = createNotificationTemplate("Updated");
        toUpdate.setId(existing.getId());

        mockMvc.perform(updateNotificationTemplateRequest(toUpdate).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        MvcResult mvcResult = mockMvc.perform(getNotificationTemplateRequest(existing.getId()).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        NotificationTemplateModel actual = valueFromPath("data.notificationTemplate", mvcResult.getResponse().getContentAsString(), NotificationTemplateModel.class);
        assertEquals(toUpdate, actual);
    }

    @Test
    public void update_whenTemplateIsNotValid_returnError() throws Exception {
        NotificationTemplateModel existing = createNotificationTemplate(createNotificationTemplate("Name1"));
        NotificationTemplateModel toUpdate = new NotificationTemplateModel();
        toUpdate.setId(existing.getId());
        toUpdate.setName("");
        toUpdate.setSubject(randomAlphanumeric(300));
        toUpdate.setContent(randomAlphanumeric(5000));
        toUpdate.setTransport("Pigeon Mail");
        toUpdate.setType(null);

        MvcResult mvcResult = mockMvc.perform(updateNotificationTemplateRequest(toUpdate).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andReturn();

        ErrorAssert.of(mvcResult.getResponse().getContentAsString())
                .andExpect("Notification template subject length should be from 1 to 255 chars", "update.arg0.subject")
                .andExpect("Notification template content length should be from 1 to 4000 chars", "update.arg0.content")
                .andExpect("Notification transport Pigeon Mail is not available", "update.arg0.transport")
                .andExpect("Notification template type cannot be empty", "update.arg0.type")
                .andExpect("Notification template name length should be from 1 to 255 chars", "update.arg0.name");
    }

    @Test
    public void get_whenTemplateAvailableById_returnTemplate() throws Exception {
        NotificationTemplateModel expected = createNotificationTemplate(createNotificationTemplate("Name1"));

        MvcResult mvcResult = mockMvc.perform(getNotificationTemplateRequest(expected.getId()).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        NotificationTemplateModel actual = valueFromPath("data.notificationTemplate", mvcResult.getResponse().getContentAsString(), NotificationTemplateModel.class);
        assertEquals(expected, actual);
    }

    @Test
    public void get_whenTemplateIsNotAvailableById_returnError() throws Exception {
        MvcResult mvcResult = mockMvc.perform(getNotificationTemplateRequest("Not_Valid_ID").with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        NotificationTemplateModel actual = valueFromPath("data.notificationTemplate", mvcResult.getResponse().getContentAsString(), NotificationTemplateModel.class);

        assertNull(actual);
    }

    @Test
    public void delete_whenTemplateAvailableById_removeTemplate() throws Exception {
        NotificationTemplateModel toBeRemoved = createNotificationTemplate(createNotificationTemplate("Name1"));
        NotificationTemplateModel expected2 = createNotificationTemplate(createNotificationTemplate("Name2"));
        NotificationTemplateModel expected3 = createNotificationTemplate(createNotificationTemplate("Name3"));

        mockMvc.perform(GraphQLRequestRepository.deleteNotificationTemplateRequest(toBeRemoved.getId()).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));

        MvcResult mvcResult = mockMvc.perform(GraphQLRequestRepository.listNotificationTemplateRequest(0, 20, Collections.emptyList()).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        List<NotificationTemplateModel> result = valueFromPath("data.notificationTemplates.nodes", mvcResult.getResponse().getContentAsString(), new TypeReference<List<NotificationTemplateModel>>() {});

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(expected2, expected3)));
        assertFalse(result.contains(toBeRemoved));
    }

    @Test
    public void delete_whenTemplateIsNotAvailableById_returnError() throws Exception {
        mockMvc.perform(GraphQLRequestRepository.deleteNotificationTemplateRequest("Not_Valid_Id").with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())));
    }

    @Test
    public void findAll_returnsAllAvailableNotificationTemplates() throws Exception {
        NotificationTemplateModel expected1 = createNotificationTemplate(createNotificationTemplate("Name1"));
        NotificationTemplateModel expected2 = createNotificationTemplate(createNotificationTemplate("Name2"));
        NotificationTemplateModel expected3 = createNotificationTemplate(createNotificationTemplate("Name3"));

        MvcResult mvcResult = mockMvc.perform(GraphQLRequestRepository.listNotificationTemplateRequest(0, 20, Collections.emptyList()).with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        List<NotificationTemplateModel> result = valueFromPath("data.notificationTemplates.nodes", mvcResult.getResponse().getContentAsString(), new TypeReference<List<NotificationTemplateModel>>() {});

        assertTrue(result.containsAll(Arrays.asList(expected1, expected2, expected3)));
    }

    private NotificationTemplateModel createNotificationTemplate(String name) {
        NotificationTemplateModel model = new NotificationTemplateModel();
        model.setType(randomNumeric(10));
        model.setTransport("EMAIL");
        model.setContent(randomAlphanumeric(200));
        model.setSubject(randomAlphanumeric(50));
        model.setDescription(randomAlphanumeric(200));
        model.setName(name);
        return model;
    }

    private NotificationTemplateModel createNotificationTemplate(NotificationTemplateModel notificationTemplateModel) throws Exception {
        MockHttpServletRequestBuilder request = addNotificationTemplateRequest(notificationTemplateModel);

        MvcResult mvcResult = mockMvc.perform(request.with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        return valueFromPath("data.addNotificationTemplate", mvcResult.getResponse().getContentAsString(), NotificationTemplateModel.class);
    }
}
