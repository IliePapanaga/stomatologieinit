package com.cl.sns.server.mvc.rest.controller.mapper;

import com.cl.sns.server.core.model.api.template.CreateNotificationTemplate;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.mvc.SNSBaseIntegrationTest;
import com.cl.sns.server.mvc.rest.controller.AntiCorruptionLayer;
import com.cl.sns.server.mvc.rest.controller.model.templates.CreateNotificationTemplateDTO;
import com.cl.sns.server.mvc.rest.controller.model.templates.NotificationTemplateDTO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificationTemplateMapperTest extends SNSBaseIntegrationTest {
    @Autowired
    private AntiCorruptionLayer antiCorruptionLayer;

    @Test
    public void fromDTO() throws Exception {
        CreateNotificationTemplateDTO createTemplateRequest = new CreateNotificationTemplateDTO();
        createTemplateRequest.setSubject("subject");
        createTemplateRequest.setContent("content");
        createTemplateRequest.setType("type");
        createTemplateRequest.setTransport("transport");

        CreateNotificationTemplate createNotificationTemplate = antiCorruptionLayer.convert(createTemplateRequest);

        Assert.assertNotNull(createNotificationTemplate);

        Assert.assertEquals(createTemplateRequest.getSubject(), createNotificationTemplate.getSubject());
        Assert.assertEquals(createTemplateRequest.getContent(), createNotificationTemplate.getContent());
        Assert.assertEquals(createTemplateRequest.getTransport(), createNotificationTemplate.getTransport());
        Assert.assertEquals(createTemplateRequest.getType(), createNotificationTemplate.getType());
    }

    @Test
    public void toDTO() throws Exception {
        NotificationTemplateModel notificationTemplateModel = new NotificationTemplateModel();
        notificationTemplateModel.setId("id");
        notificationTemplateModel.setSubject("subject");
        notificationTemplateModel.setContent("content");
        notificationTemplateModel.setType("type");
        notificationTemplateModel.setTransport("transport");

        NotificationTemplateDTO notificationTemplateDTO = antiCorruptionLayer.convert(notificationTemplateModel);

        Assert.assertNotNull(notificationTemplateDTO);

        Assert.assertEquals(notificationTemplateModel.getSubject(), notificationTemplateDTO.getSubject());
        Assert.assertEquals(notificationTemplateModel.getContent(), notificationTemplateDTO.getContent());
        Assert.assertEquals(notificationTemplateModel.getTransport(), notificationTemplateDTO.getTransport());
        Assert.assertEquals(notificationTemplateModel.getType(), notificationTemplateDTO.getType());
        Assert.assertEquals(notificationTemplateModel.getId(), notificationTemplateDTO.getId());
    }
}