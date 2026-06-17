package com.cl.mdd.server.mvc.rest.notifications;

import com.cl.mdd.server.core.data.model.notification.NotificationTypeDescriptorModel;
import com.cl.mdd.server.core.data.model.notification.NotificationTypeVariableModel;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.cl.mdd.server.mvc.rest.GraphQLRequestRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static graphql.Assert.assertNotNull;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("hsqldb-local")
public class NotificationTemplateTypeIT extends BaseMvcIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void descriptors_whenListDescriptors_validateSignUp() throws Exception {
        MockHttpServletRequestBuilder request = GraphQLRequestRepository.listNotificationTypeDescriptors();

        MvcResult mvcResult = mockMvc.perform(request.with(SYSTEM_CREDENTIALS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors", is(empty())))
                .andReturn();

        List<NotificationTypeDescriptorModel> result = valueFromPath("data.notificationTypes.nodes", mvcResult.getResponse().getContentAsString(), new TypeReference<List<NotificationTypeDescriptorModel>>() {});

        assertNotNull(result);

        List<NotificationTypeDescriptorModel> signUps = result.stream()
                .filter(descriptor -> descriptor.getType().equals("SIGN_UP"))
                .collect(Collectors.toList());

        assertEquals(1, signUps.size());

        NotificationTypeDescriptorModel signUp = signUps.get(0);

        assertEquals("SIGN_UP", signUp.getType());
        assertEquals(5, signUp.getVariables().size());
        assertTrue(signUp.getVariables().contains(new NotificationTypeVariableModel("{main.hyperlink}", "notification.var.main.link")));
        assertTrue(signUp.getVariables().contains(new NotificationTypeVariableModel("{first.name}", "notification.var.user.firstName")));
        assertTrue(signUp.getVariables().contains(new NotificationTypeVariableModel("{last.name}", "notification.var.user.lastName")));
        assertTrue(signUp.getVariables().contains(new NotificationTypeVariableModel("{username}", "notification.var.user.username")));
        assertTrue(signUp.getVariables().contains(new NotificationTypeVariableModel("{mdd.admin.email}", "notification.var.admin.email")));
    }
}