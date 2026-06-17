package com.cl.mdd.server.mvc.rest;

import com.cl.mdd.server.core.data.model.RegisterUser;
import com.cl.mdd.server.core.service.notification.AdminVariables;
import com.cl.mdd.server.core.service.notification.Notification;
import com.cl.mdd.server.core.service.notification.UserVariables;
import com.cl.mdd.server.core.service.notification.client.NotificationServiceClient;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cl.mdd.server.core.service.user.impl.AccountServiceImpl.MAIN_URL_PLACEHOLDER;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class NotificationServiceAwareWorker extends Worker {

    @Autowired
    protected NotificationServiceClient notificationServiceClient;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor = forClass(Notification.class);

    @Value("#{'${mdd.domain}' + '${mdd.registration.complete.relative.hyperlink}'}")
    protected String registrationConfirmationHyperlink;

    @Value("#{'${mdd.domain}' + '${mdd.reset.password.relative.hyperlink}'}")
    protected String resetPasswordHyperlink;

    @Value("#{'${mdd.domain}' + '${mdd.change.username.relative.hyperlink}'}")
    protected String changeUsernameHyperlink;

    @Value("${aws.mail.sender}")
    protected String awsMailSender;

    private static final String SIGN_UP = "SIGN_UP";

    private static final String CHANGE_USERNAME = "CHANGE_USERNAME";

    private static final String RESET_PASSWORD = "RESET_PASSWORD";

    public static final Function<Map<String, String>, String> NO_PLACEHOLDERS = placeHolders -> {
        assertThat(placeHolders.size(), is(0));

        return null;
    };

    public String assertSnsWelcomeMailRequest(RegisterUser registerUser) {
        return assertSnsRequest(registerUser, SIGN_UP, startsWith(registrationConfirmationHyperlink), registerUser.getUsername());
    }

    public String assertChangeUsernameRequest(RegisterUser registerUser, String email) {
        return assertSnsRequest(registerUser, CHANGE_USERNAME, startsWith(changeUsernameHyperlink), email);
    }

    public String assertResetPasswordRequest(RegisterUser registerUser) {
        return assertSnsRequest(registerUser, RESET_PASSWORD, startsWith(resetPasswordHyperlink), registerUser.getUsername());
    }

    private String assertSnsRequest(RegisterUser registerUser, String changeUsername, Matcher<String> stringMatcher, String username) {
        return assertSnsRequest(changeUsername, username, registerUser.getContact().getPhone(), placeHolders -> {
            assertNotNull(placeHolders);
            assertEquals(registerUser.getContact().getName().getFirst(), placeHolders.get(UserVariables.FIRST_NAME_PLACEHOLDER));
            assertEquals(registerUser.getContact().getName().getLast(), placeHolders.get(UserVariables.LAST_NAME_PLACEHOLDER));
            assertEquals(awsMailSender, placeHolders.get(AdminVariables.MDD_ADMIN_PLACEHOLDER));

            String hyperlink = placeHolders.get(MAIN_URL_PLACEHOLDER);
            assertNotNull(hyperlink);
            assertThat(hyperlink, stringMatcher);

            // COMPLETE REGISTRATION
            String token = StringUtils.substringAfterLast(hyperlink, "/");
            assertNotNull(token);
            return token;
        });
    }

    protected String assertSnsRequest(String expectedNotificationType, String expectedEmail, String expectedPhone, Function<Map<String, String>, String> contextProcessing) {
        verify(notificationServiceClient, atLeastOnce()).send(notificationCaptor.capture());

        List<Notification> notificationList = notificationCaptor.getAllValues().stream().filter(n -> expectedNotificationType.equals(n.getType())).collect(Collectors.toList());
        Notification notification = notificationList.get(notificationList.size() - 1);
        assertNotNull(notification);
        assertEquals(expectedNotificationType, notification.getType());
        assertEquals(expectedEmail, notification.getEmail());
        assertEquals(expectedPhone, notification.getPhone());

        return contextProcessing.apply(notification.getContext());
    }
}
