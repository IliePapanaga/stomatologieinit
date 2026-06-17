package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.common.FullNameModel;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.common.embeddable.FullName;
import com.cl.mdd.server.core.data.persistent.model.contact.Contact;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.SignUpCompletedEvent;
import com.cl.mdd.server.core.manager.converter.CommonConverter;
import com.cl.mdd.server.core.security.SecurityAccess;
import com.cl.mdd.server.core.security.token.ChangeUsernameToken;
import com.cl.mdd.server.core.security.token.ChangeUsernameTokenOutput;
import com.cl.mdd.server.core.security.token.RegistrationToken;
import com.cl.mdd.server.core.security.token.ResetPasswordToken;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.core.service.contact.ContactService;
import com.cl.mdd.server.core.service.notification.AdminVariables;
import com.cl.mdd.server.core.service.notification.Notification;
import com.cl.mdd.server.core.service.notification.NotificationService;
import com.cl.mdd.server.core.service.notification.UserVariables;
import com.cl.mdd.server.core.service.practice.PracticeService;
import com.cl.mdd.server.core.service.user.PracticeOwnerService;
import com.cl.mdd.server.core.service.user.ProfessionalService;
import com.cl.mdd.server.core.service.user.SystemUserService;
import org.apache.commons.collections4.SetUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Set;

import static com.cl.mdd.server.core.data.persistent.model.user.User.*;
import static com.cl.mdd.server.core.service.user.impl.AccountServiceImpl.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AccountServiceImplTest {

    private static final String HYPERLINK = "HYPERLINK";

    private static final String USERNAME = "username";

    private static final String ID = "ID";

    private static final String TOKEN = "TOKEN";

    private static final String USERNAME_NEW = "USERNAME_NEW";

    private static final String EMAIL = "owner@email.com";

    private static final String PASSWORD_NEW = "PASSWORD_NEW";

    private static final String PASSWORD_OLD = "PASSWORD_OLD";

    private static final String RESET_PWD_URL = "RESET_PWD_URL";

    private static final String CHANGE_USERNAME_URL = "CH_USERNAME_URL";

    private static final String REGISTER_URL = "REGISTER_URL";

    public static final String AWS_MAIL_SENDER = "awsMailSender";

    @Spy
    @InjectMocks
    private AccountServiceImpl accountService = new AccountServiceImpl();

    @Mock
    protected UserDao userDao;

    @Mock
    private SecurityAccess securityAccess;

    @Mock
    protected PasswordEncoder passwordEncoder;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ResetPasswordToken resetPasswordToken;

    @Mock
    private RegistrationToken registrationToken;

    @Mock
    private ChangeUsernameToken changeUsernameToken;

    @Mock
    private PracticeOwnerService practiceOwnerService;

    @Mock
    private ProfessionalService professionalService;

    @Mock
    private SystemUserService systemUserService;

    @Mock
    private PracticeService practiceService;

    @Mock
    private ContactService contactService;

    @Mock
    private CommonConverter commonConverter;

    @Captor
    private ArgumentCaptor<Notification> emailNotificationArgumentCaptor;

    @Captor
    private ArgumentCaptor<ChangeUsernameTokenOutput> changeUsernameArgumentCaptor;

    @Mock
    private EventBus<SignUpCompletedEvent> signUpCompletedEventEventBus;

    @Spy
    private TransactionHelper transactionHelper;

    private PracticeOwner user;

    private Contact contact;

    private FullName name;

    private ChangePassword changePassword;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        user = new PracticeOwner();
        user.setUsername(USERNAME);
        contact = new Contact();
        name = new FullName();
        name.setFirst("FIRST");
        name.setLast("LAST");
        contact.setName(name);
        contact.setEmail(USERNAME);
        user.setContact(contact);
        user.setStatus(ACTIVE);
        user.setPassword(PASSWORD_OLD);
        user.setId(ID);

        changePassword = new ChangePassword();
        changePassword.setNewPassword(PASSWORD_NEW);
        changePassword.setOldPassword(PASSWORD_OLD);

        ReflectionTestUtils.setField(accountService, "userVariables", new UserVariables());
        ReflectionTestUtils.setField(accountService, "adminVariables", new AdminVariables(AWS_MAIL_SENDER));
    }

    @Test
    public void userInfo() {
        FullNameModel nameModel = new FullNameModel();
        Set<String> roles = SetUtils.emptySet();
        doReturn(user).when(accountService).nonNullUser(ID);
        doReturn(roles).when(securityAccess).currentUserRoles();
        doReturn(nameModel).when(commonConverter).toFullNameModel(name);

        UserInfo userInfo = accountService.userInfo(ID);

        verify(accountService).nonNullUser(ID);
        verify(securityAccess).currentUserRoles();
        verify(commonConverter).toFullNameModel(name);

        assertNotNull(userInfo);
        assertEquals(ID, userInfo.getId());
        assertEquals(USERNAME, userInfo.getUsername());
        assertEquals(ACTIVE, userInfo.getStatus());
        assertSame(nameModel, userInfo.getName());
        assertSame(roles, userInfo.getRoles());
    }

    @Test
    public void changePassword() throws Exception {
        doReturn(user).when(accountService).nonNullUser(USERNAME);
        doReturn(true).when(passwordEncoder).matches(changePassword.getOldPassword(), PASSWORD_OLD);
        doReturn(PASSWORD_NEW).when(passwordEncoder).encode(changePassword.getNewPassword());

        accountService.changePassword(USERNAME, changePassword);

        verify(passwordEncoder).matches(changePassword.getOldPassword(), PASSWORD_OLD);
        verify(passwordEncoder).encode(changePassword.getNewPassword());
        assertEquals(PASSWORD_NEW, user.getPassword());
    }

    @Test(expected = WrongPassword.class)
    public void changePasswordWithWrongPassword() throws Exception {
        doReturn(user).when(accountService).nonNullUser(USERNAME);
        doReturn(false).when(passwordEncoder).matches(changePassword.getOldPassword(), PASSWORD_OLD);

        accountService.changePassword(USERNAME, changePassword);
    }

    @Test
    public void resetPassword() throws Exception {
        NewPassword newPassword = new NewPassword();
        newPassword.setToken(TOKEN);
        newPassword.setPassword(PASSWORD_NEW);

        doReturn(USERNAME).when(resetPasswordToken).verify(TOKEN);
        doReturn(user).when(accountService).nonNullUser(USERNAME);
        doReturn(PASSWORD_NEW).when(passwordEncoder).encode(changePassword.getNewPassword());

        accountService.resetPassword(newPassword);

        verify(resetPasswordToken).verify(TOKEN);
        verify(passwordEncoder).encode(changePassword.getNewPassword());
        assertEquals(PASSWORD_NEW, user.getPassword());
    }

    @Test
    public void requestResetPassword() throws Exception {
        doReturn(user).when(accountService).nonNullUserByUsername(USERNAME);
        doReturn(RESET_PWD_URL).when(accountService).resetPasswordUrl(user);
        doNothing().when(accountService).sendMail(user, RESET_PASSWORD, RESET_PWD_URL, user.getContact().getEmail());

        accountService.requestResetPassword(USERNAME);

        verify(accountService).resetPasswordUrl(user);
        verify(accountService).sendMail(user, RESET_PASSWORD, RESET_PWD_URL, user.getContact().getEmail());
    }

    @Test
    public void requestChangeUsername() throws Exception {
        doReturn(user).when(accountService).nonNullUser(USERNAME);
        doReturn(CHANGE_USERNAME_URL).when(accountService).changeUsernameUrl(user, USERNAME_NEW);
        doNothing().when(accountService).sendMail(user, CHANGE_USERNAME, CHANGE_USERNAME_URL, user.getContact().getEmail());
        doReturn(true).when(passwordEncoder).matches(changePassword.getOldPassword(), PASSWORD_OLD);

        com.cl.mdd.server.core.data.model.ChangeUsername changeUsername = new com.cl.mdd.server.core.data.model.ChangeUsername();
        changeUsername.setPassword(PASSWORD_OLD);
        changeUsername.setNewUsername(USERNAME_NEW);
        accountService.requestChangeUsername(USERNAME, changeUsername);

        verify(accountService).changeUsernameUrl(user, USERNAME_NEW);
        verify(accountService).sendMail(user, CHANGE_USERNAME, CHANGE_USERNAME_URL, USERNAME_NEW);
    }


    @Test(expected = WrongPassword.class)
    public void requestChangeUsernameWrongPassword() throws Exception {
        doReturn(user).when(accountService).nonNullUser(USERNAME);
        doReturn(CHANGE_USERNAME_URL).when(accountService).changeUsernameUrl(user, USERNAME_NEW);
        doNothing().when(accountService).sendMail(user, CHANGE_USERNAME, CHANGE_USERNAME_URL, user.getContact().getEmail());
        doReturn(false).when(passwordEncoder).matches(changePassword.getOldPassword(), PASSWORD_OLD);

        com.cl.mdd.server.core.data.model.ChangeUsername changeUsername = new com.cl.mdd.server.core.data.model.ChangeUsername();
        changeUsername.setPassword(PASSWORD_OLD);
        changeUsername.setNewUsername(USERNAME_NEW);
        accountService.requestChangeUsername(USERNAME, changeUsername);

        verify(accountService, never()).changeUsernameUrl(any(), anyString());
        verify(accountService, never()).sendMail(any(), anyString(), anyString(), anyString());
    }

    @Test
    public void confirmUsernameChange() throws Exception {
        ChangeUsernameTokenOutput changeUsernameTokenOutput = new ChangeUsernameTokenOutput(ID, USERNAME_NEW);
        Mockito.doReturn(changeUsernameTokenOutput).when(changeUsernameToken).verify(TOKEN);
        doReturn(user).when(userDao).findOne(ID);

        String result = accountService.confirmUsernameChange(TOKEN);

        verify(changeUsernameToken).verify(TOKEN);
        verify(userDao).findOne(ID);
        assertEquals(USERNAME_NEW, user.getUsername());
        assertEquals(USERNAME_NEW, user.getContact().getEmail());
        assertEquals(USERNAME_NEW, result);

    }

    @Test(expected = WrongUsername.class)
    public void confirmUsernameChangeForNullUser() throws Exception {
        ChangeUsernameTokenOutput changeUsernameTokenOutput = new ChangeUsernameTokenOutput(ID, USERNAME_NEW);
        Mockito.doReturn(changeUsernameTokenOutput).when(changeUsernameToken).verify(TOKEN);
        doReturn(null).when(userDao).findOne(ID);

        accountService.confirmUsernameChange(TOKEN);
    }

    @Test
    public void completeRegistration() throws Exception {
        user.setStatus(EMAIL_CONFIRMATION_PENDING);
        doReturn(ID).when(registrationToken).verify(TOKEN);
        when(userDao.findOne(ID)).thenReturn(user);

        accountService.completeRegistration(TOKEN);

        verify(registrationToken).verify(TOKEN);
        assertThat(user.getStatus(), is(INACTIVE));
        verify(signUpCompletedEventEventBus).publishEvent(any());
    }

    @Test(expected = EmailAlreadyConfirmed.class)
    public void completeRegistrationAlreadyConfirmed() throws Exception {
        user.setStatus(null);
        doReturn(ID).when(registrationToken).verify(TOKEN);
        when(userDao.findOne(ID)).thenReturn(null);

        accountService.completeRegistration(TOKEN);
    }

    @Test
    public void completeRegistrationSystemUser() {
        final String newPassword = "QAZWSX12345";
        final String newPasswordEncode = "EDCRFV678";

        user.setStatus(null);

        doReturn(ID).when(registrationToken).verify(TOKEN);
        doReturn(user).when(userDao).findOne(ID);
        doReturn(newPasswordEncode).when(passwordEncoder).encode(newPassword);

        accountService.completeRegistrationSystemUser(TOKEN, newPassword);

        verify(registrationToken).verify(TOKEN);
        verify(userDao).findOne(ID);
        verify(userDao).save(user);
        assertEquals(ACTIVE, user.getStatus());
        assertEquals(newPasswordEncode, user.getPassword());
    }

    @Test
    public void registerPracticeOwner() throws Exception {
        RegisterPracticeOwner registerModel = new RegisterPracticeOwner();
        PracticeOwnerModel registered = new PracticeOwnerModel();
        String id = "#$%^&*";
        registered.setId(id);
        doReturn(registered).when(practiceOwnerService).register(registerModel);
        doReturn(user).when(userDao).findOne(id);
        doReturn(REGISTER_URL).when(accountService).registrationUrl(user);
        doNothing().when(accountService).sendMail(user, SIGN_UP, REGISTER_URL, user.getContact().getEmail());

        PracticeOwnerModel actualResult = accountService.register(registerModel);

        verify(practiceOwnerService).register(registerModel);
        verify(userDao).findOne(id);
        verify(accountService).registrationUrl(user);
        verify(accountService).sendMail(user, SIGN_UP, REGISTER_URL, user.getContact().getEmail());
        assertNotNull(actualResult);
        assertSame(registered, actualResult);
    }

    @Test
    public void registerProfessional() throws Exception {
        RegisterProfessional registerModel = new RegisterProfessional();
        ProfessionalModel registered = new ProfessionalModel();
        String id = "#$%^&*";
        registered.setId(id);
        doReturn(registered).when(professionalService).register(registerModel);
        doReturn(user).when(userDao).findOne(id);
        doReturn(REGISTER_URL).when(accountService).registrationUrl(user);
        doNothing().when(accountService).sendMail(user, SIGN_UP, REGISTER_URL, user.getContact().getEmail());

        ProfessionalModel actualResult = accountService.register(registerModel);

        verify(professionalService).register(registerModel);
        verify(userDao).findOne(id);
        verify(accountService).registrationUrl(user);
        verify(accountService).sendMail(user, SIGN_UP, REGISTER_URL, user.getContact().getEmail());
        assertNotNull(actualResult);
        assertSame(registered, actualResult);
    }

    @Test
    public void registerSystemUser() {
        RegisterSystemUser registerModel = new RegisterSystemUser();
        SystemUserModel registered = new SystemUserModel();
        String id = "#$%^&*";
        registered.setId(id);
        doReturn(registered).when(systemUserService).register(registerModel);
        doReturn(user).when(userDao).findOne(id);
        doReturn(REGISTER_URL).when(accountService).registrationUrl(user);
        doNothing().when(accountService).sendMail(user, SIGN_UP, REGISTER_URL, user.getContact().getEmail());

        SystemUserModel actualResult = accountService.register(registerModel);

        verify(systemUserService).register(registerModel);
        verify(userDao).findOne(id);
        verify(accountService).registrationUrl(user);
        verify(accountService).sendMail(user, SIGN_UP, REGISTER_URL, user.getUsername());
        assertNotNull(actualResult);
        assertSame(registered, actualResult);
    }

    @Test
    public void sendWelcomeEmailAgain() throws Exception {
        String id = "strongID";
        user.setStatus(User.EMAIL_CONFIRMATION_PENDING);
        doReturn(user).when(userDao).findOne(id);
        doReturn(REGISTER_URL).when(accountService).registrationUrl(user);
        doNothing().when(accountService).sendMail(user, SIGN_UP, REGISTER_URL, user.getContact().getEmail());

        accountService.sendWelcomeMailAgain(id);

        verify(userDao).findOne(id);
        verify(accountService).registrationUrl(user);
        verify(accountService).sendMail(user, SIGN_UP, REGISTER_URL, user.getContact().getEmail());

    }

    @Test(expected = EmailAlreadyConfirmed.class)
    public void sendWelcomeEmailAgainForAlreadyConfirmedUser() throws Exception {
        String id = "strongID";
        user.setStatus(User.ACTIVE);
        doReturn(user).when(userDao).findOne(id);

        accountService.sendWelcomeMailAgain(id);
    }

    @Test
    public void update() throws Exception {
        String id = "proID";
        ContactModel contact = new ContactModel();
        ProfessionalJobPreferenceModel jobPreference = new ProfessionalJobPreferenceModel();
        ProfessionalModel professionalModel = new ProfessionalModel();

        professionalModel.setId(id);
        accountService.update(professionalModel, jobPreference);

        verify(professionalService).updateJobPreferences(id, jobPreference);
        verify(professionalService).update(professionalModel);
    }

    @Test
    public void updatePracticeOwnerId() throws Exception {
        PracticeOwnerModel practiceOwnerModel = new PracticeOwnerModel();
        PracticeModel practiceModel = new PracticeModel();

        accountService.update(practiceOwnerModel, practiceModel);

        verify(practiceService).update(practiceModel);
        verify(practiceOwnerService).update(practiceOwnerModel);
    }

    @Test
    public void sendMail() throws Exception {
        String hyperLink = "link";

        accountService.sendMail(user, SIGN_UP, hyperLink, user.getContact().getEmail());

        verify(notificationService).send(emailNotificationArgumentCaptor.capture());
        Notification notification = emailNotificationArgumentCaptor.getValue();
        assertNotNull(notification);
        assertEquals(USERNAME, notification.getEmail());
        assertEquals(SIGN_UP, notification.getType());
        Map<String, String> context = notification.getContext();
        assertNotNull(context);
        assertEquals(hyperLink, context.get(MAIN_URL_PLACEHOLDER));
        assertEquals(name.getFirst(), context.get(UserVariables.FIRST_NAME_PLACEHOLDER));
        assertEquals(name.getLast(), context.get(UserVariables.LAST_NAME_PLACEHOLDER));
        assertEquals(AWS_MAIL_SENDER, context.get(AdminVariables.MDD_ADMIN_PLACEHOLDER));
    }

    @Test
    public void registrationUrl() throws Exception {
        ReflectionTestUtils.setField(accountService, "registrationConfirmationHyperlink", HYPERLINK);
        doReturn(TOKEN).when(registrationToken).generate(ID);

        String actual = accountService.registrationUrl(user);

        verify(registrationToken).generate(ID);
        assertNotNull(actual);
        assertEquals(HYPERLINK + SLASH + TOKEN, actual);
    }

    @Test
    public void resetPasswordUrl() throws Exception {
        ReflectionTestUtils.setField(accountService, "resetPasswordHyperlink", HYPERLINK);
        doReturn(TOKEN).when(resetPasswordToken).generate(ID);

        String actual = accountService.resetPasswordUrl(user);

        verify(resetPasswordToken).generate(ID);
        assertNotNull(actual);
        assertEquals(HYPERLINK + SLASH + TOKEN, actual);
    }

    @Test
    public void changeUsernameUrl() throws Exception {
        ReflectionTestUtils.setField(accountService, "changeUsernameHyperlink", HYPERLINK);
        doReturn(TOKEN).when(changeUsernameToken).generate(changeUsernameArgumentCaptor.capture());

        String actual = accountService.changeUsernameUrl(user, USERNAME_NEW);

        assertNotNull(actual);
        ChangeUsernameTokenOutput changeUsernameTokenOutput = changeUsernameArgumentCaptor.getValue();
        assertNotNull(changeUsernameTokenOutput);
        assertEquals(ID, changeUsernameTokenOutput.getUserId());
        assertEquals(USERNAME_NEW, changeUsernameTokenOutput.getNewUsername());
        assertEquals(HYPERLINK + SLASH + TOKEN, actual);
    }

    @Test
    public void nonNullUser() throws Exception {
        doReturn(user).when(userDao).findOne(ID);

        User actual = accountService.nonNullUser(ID);
        verify(userDao).findOne(ID);
        assertSame(user, actual);
    }

    @Test(expected = WrongUsername.class)
    public void nonNullUserNotFound() throws Exception {
        doReturn(null).when(userDao).findByUsernameIgnoreCase(USERNAME);

        User actual = accountService.nonNullUser(USERNAME);

        verify(userDao).findByUsernameIgnoreCase(USERNAME);
        assertSame(user, actual);
    }
}