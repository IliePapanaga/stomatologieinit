package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.SignUpCompletedEvent;
import com.cl.mdd.server.core.security.token.ChangeUsernameToken;
import com.cl.mdd.server.core.security.token.ChangeUsernameTokenOutput;
import com.cl.mdd.server.core.security.token.RegistrationToken;
import com.cl.mdd.server.core.security.token.ResetPasswordToken;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.service.contact.ContactService;
import com.cl.mdd.server.core.service.notification.AdminVariables;
import com.cl.mdd.server.core.service.notification.Notification;
import com.cl.mdd.server.core.service.notification.NotificationService;
import com.cl.mdd.server.core.service.notification.UserVariables;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.cl.mdd.server.core.service.practice.PracticeService;
import com.cl.mdd.server.core.service.user.AccountService;
import com.cl.mdd.server.core.service.user.PracticeOwnerService;
import com.cl.mdd.server.core.service.user.ProfessionalService;
import com.cl.mdd.server.core.service.user.SystemUserService;
import com.cl.mdd.server.core.validation.group.Register;
import com.cl.mdd.server.core.validation.group.RequireCoordinates;
import com.cl.mdd.server.core.validation.group.Update;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.copyOf;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Primary
@Validated
public class AccountServiceImpl extends ServiceSupport implements AccountService {

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ResetPasswordToken resetPasswordToken;

    @Autowired
    private RegistrationToken registrationToken;

    @Autowired
    private ChangeUsernameToken changeUsernameToken;

    @Value("#{'${mdd.domain}' + '${mdd.registration.complete.relative.hyperlink}'}")
    private String registrationConfirmationHyperlink;

    @Value("#{'${mdd.domain}' + '${mdd.reset.password.relative.hyperlink}'}")
    private String resetPasswordHyperlink;

    @Value("#{'${mdd.domain}' + '${mdd.change.username.relative.hyperlink}'}")
    private String changeUsernameHyperlink;

    @Autowired
    private PracticeOwnerService practiceOwnerService;

    @Autowired
    private ProfessionalService professionalService;

    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private PracticeService practiceService;

    @Autowired
    private EventBus<SignUpCompletedEvent> signUpCompletedEventEventBus;

    @Autowired
    private UserVariables userVariables;

    @Autowired
    private AdminVariables adminVariables;

    public static final String SIGN_UP = "SIGN_UP";

    public static final String SIGN_UP_COMPLETED = "SIGN_UP_COMPLETED";

    public static final String RESET_PASSWORD = "RESET_PASSWORD";

    public static final String CHANGE_USERNAME = "CHANGE_USERNAME";

    public static final String MAIN_URL_PLACEHOLDER = "{main.hyperlink}";

    public static final String SLASH = "/";

    @Transactional(readOnly = true)
    @Override
    public UserInfo userInfo(String id) {
        User user = nonNullUser(id);
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setUsername(user.getUsername());
        userInfo.setRoles(securityAccess.currentUserRoles());
        userInfo.setStatus(user.getStatus());
        userInfo.setName(commonConverter.toFullNameModel(user.getContact().getName()));
        return userInfo;
    }

    @Override
    @Transactional
    public void changePassword(String userId, @Valid ChangePassword changePassword) {
        User user = nonNullUser(userId);
        if (passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        } else {
            throw new WrongPassword("Provided password is not correct.");
        }
    }

    @Override
    @NotificationDefinition(value = RESET_PASSWORD,
            vars = {@Variable(macro = MAIN_URL_PLACEHOLDER, name = "notification.var.main.link")},
            predefined = {UserVariables.class, AdminVariables.class}
    )
    public void requestResetPassword(String username) {
        User user = nonNullUserByUsername(username);
        sendMail(user, RESET_PASSWORD, resetPasswordUrl(user), user.getContact().getEmail());
    }

    @Override
    @Transactional
    public void resetPassword(@Valid NewPassword newPassword) {
        String userId = resetPasswordToken.verify(newPassword.getToken());
        User user = nonNullUser(userId);
        user.setPassword(passwordEncoder.encode(newPassword.getPassword()));
    }

    @Override
    @NotificationDefinition(value = CHANGE_USERNAME,
            vars = {@Variable(macro = MAIN_URL_PLACEHOLDER, name = "notification.var.main.link")},
            predefined = {UserVariables.class, AdminVariables.class}
    )
    public void requestChangeUsername(String id, ChangeUsername changeUsername) {
        User user = nonNullUser(id);
        if (passwordEncoder.matches(changeUsername.getPassword(), user.getPassword())) {
            String newUsername = changeUsername.getNewUsername();
            sendMail(user, CHANGE_USERNAME, changeUsernameUrl(user, newUsername), newUsername);
        } else {
            throw new WrongPassword("Provided password is not correct.");
        }
    }

    @Transactional
    @Override
    public String confirmUsernameChange(String token) {
        ChangeUsernameTokenOutput changeUsernameTokenOutput = changeUsernameToken.verify(token);
        User user = userDao.findOne(changeUsernameTokenOutput.getUserId());
        if (user != null) {
            String newUsername = changeUsernameTokenOutput.getNewUsername();
            user.setUsername(newUsername);
            user.getContact().setEmail(newUsername);
            userDao.updateLastActivity(user.getId());
            return user.getUsername();
        } else {
            throw new WrongUsername();
        }
    }

    @Override
    public void completeRegistration(String token) {
        String userId = registrationToken.verify(token);
        User db = userDao.findOne(userId);

        if (nonNull(db) && User.EMAIL_CONFIRMATION_PENDING.equals(db.getStatus())) {
            db.setStatus(User.INACTIVE);
            userDao.save(db);
            signUpCompletedEventEventBus.publishEvent(event -> event.setUserId(userId));
        } else {
            throw new EmailAlreadyConfirmed();
        }
    }

    @Override
    @Transactional
    public void completeRegistrationSystemUser(String token, String newPassword) {
        String userId = registrationToken.verify(token);
        User user = nonNullUser(userId);
        user.setStatus(User.ACTIVE);
        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.save(user);
    }

    @Override
    @Validated(Register.class)
    @NotificationDefinition(value = SIGN_UP,
            vars = {@Variable(macro = MAIN_URL_PLACEHOLDER, name = "notification.var.main.link")},
            predefined = {UserVariables.class, AdminVariables.class}
    )
    public PracticeOwnerModel register(@Valid RegisterPracticeOwner registerPracticeOwner) {
        PracticeOwnerModel registered = executeInTransaction(() -> practiceOwnerService.register(registerPracticeOwner));
        User user = userDao.findOne(registered.getId());
        sendMail(user, SIGN_UP, registrationUrl(user), user.getContact().getEmail());
        return registered;
    }

    @Override
    @Validated({Register.class, RequireCoordinates.class})
    @NotificationDefinition(value = SIGN_UP,
            vars = {@Variable(macro = MAIN_URL_PLACEHOLDER, name = "notification.var.main.link")},
            predefined = {UserVariables.class, AdminVariables.class}
    )
    public ProfessionalModel register(@Valid RegisterProfessional registerUser) {
        ProfessionalModel registered = executeInTransaction(() -> professionalService.register(registerUser));
        User user = userDao.findOne(registered.getId());
        sendMail(user, SIGN_UP, registrationUrl(user), user.getContact().getEmail());
        return registered;
    }

    @Override
    @Validated(Register.class)
    @NotificationDefinition(value = SIGN_UP,
            vars = {@Variable(macro = MAIN_URL_PLACEHOLDER, name = "notification.var.main.link")},
            predefined = {UserVariables.class, AdminVariables.class}
    )
    public SystemUserModel register(@Valid RegisterSystemUser registerUser) {
        SystemUserModel registered = executeInTransaction(() -> systemUserService.register(registerUser));
        User user = userDao.findOne(registered.getId());
        sendMail(user, SIGN_UP, registrationUrl(user), user.getUsername());
        return registered;
    }

    @Override
    @NotificationDefinition(value = SIGN_UP,
            vars = {@Variable(macro = MAIN_URL_PLACEHOLDER, name = "notification.var.main.link")},
            predefined = {UserVariables.class, AdminVariables.class}
    )
    public void sendWelcomeMailAgain(String id) {
        User user = userDao.findOne(id);
        if (User.EMAIL_CONFIRMATION_PENDING.equals(user.getStatus())) {
            sendMail(user, SIGN_UP, registrationUrl(user), user.getContact().getEmail());
        } else {
            throw new EmailAlreadyConfirmed();
        }
    }


    @Override
    @Transactional
    @Validated({Update.class, RequireCoordinates.class})
    //TODO THINK IF WE CAN TRIGGER MULTIPLE GRAPHQL MUTATIONS IN ONE @TRANSACTIONAL
    public void update(@Valid ProfessionalModel professionalModel, @Valid ProfessionalJobPreferenceModel jobPreference) {
        professionalService.updateJobPreferences(professionalModel.getId(), jobPreference);
        professionalService.update(professionalModel);
    }

    @Override
    @Transactional
    @Validated(Update.class)
    //TODO THINK IF WE CAN TRIGGER MULTIPLE GRAPHQL MUTATIONS IN ONE @TRANSACTIONAL
    public void update(PracticeOwnerModel practiceOwnerModel, PracticeModel updatePractice) {
        practiceService.update(updatePractice);
        practiceOwnerService.update(practiceOwnerModel);
    }

    protected void sendMail(User user, String type, String hyperlink, String to) {
        Notification notification = new Notification();
        notification.setEmail(to);
        notification.setPhone(user.getContact().getPhone());
        notification.setType(type);
        ImmutableMap<String, String> of = buildNotificationContext(user, hyperlink);
        notification.setContext(of);
        notificationService.send(notification);
    }

    private ImmutableMap<String, String> buildNotificationContext(User user, String hyperlink) {
        Map<String, String> context = new HashMap<>();
        userVariables.supply(user, context);
        adminVariables.supply(null, context);
        context.put(MAIN_URL_PLACEHOLDER, hyperlink);
        return copyOf(context);
    }

    protected String registrationUrl(User user) {
        return registrationConfirmationHyperlink + SLASH + registrationToken.generate(user.getId());
    }

    protected String resetPasswordUrl(User user) {
        return resetPasswordHyperlink + SLASH + resetPasswordToken.generate(user.getId());
    }

    protected String changeUsernameUrl(User user, String newUsername) {
        return changeUsernameHyperlink + SLASH + changeUsernameToken.generate(new ChangeUsernameTokenOutput(user.getId(), newUsername));
    }

    protected User nonNullUser(String id) {
        if (isNull(id)) {
            throw new WrongUsername();
        }
        User user = userDao.findOne(id);
        if (nonNull(user)) {
            return user;
        } else {
            throw new WrongUsername();
        }
    }

    protected User nonNullUserByUsername(String username) {
        if (isNull(username)) {
            throw new WrongUsername();
        }
        User user = userDao.findByUsernameIgnoreCase(username);
        if (nonNull(user)) {
            return user;
        } else {
            throw new WrongUsername();
        }
    }

}
