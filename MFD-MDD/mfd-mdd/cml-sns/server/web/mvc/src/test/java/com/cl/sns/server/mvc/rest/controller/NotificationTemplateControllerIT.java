package com.cl.sns.server.mvc.rest.controller;

import com.cl.sns.server.core.dao.MessagingTransportDao;
import com.cl.sns.server.core.model.db.msg.MessagingTransport;
import com.cl.sns.server.mvc.SNSMvcBaseIntegrationTest;
import com.cl.sns.server.mvc.rest.controller.model.common.ResponseDTO;
import com.cl.sns.server.mvc.rest.controller.model.templates.BaseNotificationTemplateDTO;
import com.cl.sns.server.mvc.rest.controller.model.templates.NotificationTemplateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.core.Is.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class NotificationTemplateControllerIT extends SNSMvcBaseIntegrationTest {
    private static final String API_PATH = "/api/notification/template";
    private static final int HTTP_STATUS_OK = HttpStatus.OK.value();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MessagingTransportDao messagingTransportDao;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void save() throws Exception {
        saveNotificationTemplate();
    }

    private NotificationTemplateDTO saveNotificationTemplate() throws Exception {
        MessagingTransport transport = new MessagingTransport("Message_Transport");
        messagingTransportDao.save(transport);

        NotificationTemplateDTO notificationTemplateDTO = new NotificationTemplateDTO();
        notificationTemplateDTO.setType("type");
        notificationTemplateDTO.setSubject("Subject");
        notificationTemplateDTO.setContent("content");
        notificationTemplateDTO.setTransport("Message_Transport");
        notificationTemplateDTO.setName("name");
        notificationTemplateDTO.setDescription("description");

        String json = objectMapper.writeValueAsString(notificationTemplateDTO);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(API_PATH)
                .accept(MediaType.APPLICATION_JSON).content(json)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        Assert.assertEquals(HTTP_STATUS_OK, response.getStatus());
        ResponseDTO<NotificationTemplateDTO> result = objectMapper.readValue(response.getContentAsByteArray(), TypeFactory.defaultInstance().constructParametricType(ResponseDTO.class, NotificationTemplateDTO.class));
        checkTemplateResult(notificationTemplateDTO, result);

        NotificationTemplateDTO resultData = result.getData();
        Assert.assertNotNull(resultData);
        Assert.assertNotNull(resultData.getId());


        return resultData;
    }

    @Test
    public void get() throws Exception {
        NotificationTemplateDTO notificationTemplateDTO = saveNotificationTemplate();

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(API_PATH + "/" + notificationTemplateDTO.getId())
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        ResponseDTO result =  objectMapper.readValue(response.getContentAsByteArray(), TypeFactory.defaultInstance().constructParametricType(ResponseDTO.class, NotificationTemplateDTO.class));

        checkTemplateResult(notificationTemplateDTO, result);
    }


    @Test
    public void delete() throws Exception {

        // save a new template
        NotificationTemplateDTO notificationTemplateDTO = saveNotificationTemplate();

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(API_PATH + "/" + notificationTemplateDTO.getId())
                .accept(MediaType.APPLICATION_JSON);

        // delete the template
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse deleteResponse = mvcResult.getResponse();

        Assert.assertEquals(HTTP_STATUS_OK, deleteResponse.getStatus());

        requestBuilder = MockMvcRequestBuilders
                .get(API_PATH + "/" + notificationTemplateDTO.getId())
                .accept(MediaType.APPLICATION_JSON);

        // get by id
        mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse getResponse = mvcResult.getResponse();
        Assert.assertEquals(HTTP_STATUS_OK, getResponse.getStatus());
        ResponseDTO result = objectMapper.readValue(getResponse.getContentAsByteArray(), TypeFactory.defaultInstance().constructParametricType(ResponseDTO.class, NotificationTemplateDTO.class));

        Assert.assertNotNull(result);
        Assert.assertNull(result.getData());
        Assert.assertEquals(HTTP_STATUS_OK, result.getStatus());
    }

    private void checkTemplateResult(BaseNotificationTemplateDTO expected, ResponseDTO<NotificationTemplateDTO> result) {
        Assert.assertNotNull(result);
        Assert.assertEquals(HTTP_STATUS_OK, result.getStatus());

        NotificationTemplateDTO actual = result.getData();
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getContent(), actual.getContent());
        Assert.assertEquals(expected.getSubject(), actual.getSubject());
        Assert.assertEquals(expected.getType(), actual.getType());
        Assert.assertEquals(expected.getTransport(), actual.getTransport());
    }
}