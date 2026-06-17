package com.cl.mdd.server.mvc.rest.graphql.provider.unsecured;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.SpecialityModel;
import com.cl.mdd.server.core.security.SecurityAccess;
import com.cl.mdd.server.core.service.common.SpecialityService;
import com.cl.mdd.server.core.service.user.AccountService;
import com.cl.mdd.server.core.service.user.UserValidationService;
import com.cl.mdd.server.mvc.captcha.CaptchaValidator;
import com.cl.mdd.server.mvc.captcha.annotation.RequiresCaptcha;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Account data provider.
 * <p/>
 * Exposes user's operations to graph ql.
 */
@Component
public class UnsecuredAccountProvider implements UnsecuredGraphQLProvider {

    @Autowired
    private AccountService accountService;

    @Autowired
    private SecurityAccess securityAccess;

    @Autowired
    private SpecialityService specialityService;

    @Autowired
    private UserValidationService userValidationService;

    @Autowired
    private CaptchaValidator reCaptcha;

    @GraphQLMutation(name = "validateUserMail")
    public void validateEmail(@GraphQLArgument(name = "model") UserValidationModel model) {
        userValidationService.validateEmail(model);
    }

    @GraphQLQuery(name = "listAllSpecialities")
    public List<SpecialityModel> specialities() {
        return specialityService.getAll();
    }

    @GraphQLMutation(name = "requestResetPassword")
    @RequiresCaptcha
    public void requestResetPassword(String username) {
        accountService.requestResetPassword(username);
    }

    @GraphQLMutation(name = "resetPassword")
    public void resetPassword(@GraphQLArgument(name="newPassword") NewPassword newPassword) {
        accountService.resetPassword(newPassword);
    }

    @GraphQLMutation(name = "completeRegistration")
    public void completeRegistration(@GraphQLArgument(name = "token") String token) {
        accountService.completeRegistration(token);
    }

    @GraphQLMutation(name = "confirmUsernameChange")
    public void confirmUsernameChange(@GraphQLArgument(name = "token") String token) {
        String newUsername = accountService.confirmUsernameChange(token);
        securityAccess.login(newUsername);
    }

    @GraphQLMutation(name = "completeRegistrationSystemUser")
    public void completeRegistrationSystemUser(@GraphQLArgument(name = "token") String token,
                                               @GraphQLArgument(name = "password") String password) {
        accountService.completeRegistrationSystemUser(token, password);
    }

    @GraphQLMutation(name = "registerPracticeOwner")
    @RequiresCaptcha
    public PracticeOwnerModel register(@GraphQLArgument(name = "practiceOwner") RegisterPracticeOwner registerPracticeOwner) {
        return accountService.register(registerPracticeOwner);
    }

    @GraphQLMutation(name = "registerProfessional")
    @RequiresCaptcha
    public ProfessionalModel register(@GraphQLArgument(name = "professional") RegisterProfessional registerProfessional) {
        return accountService.register(registerProfessional);
    }

    @GraphQLMutation(name = "sendWelcomeMailAgain")
    @RequiresCaptcha
    public void sendWelcomeMailAgain(@GraphQLArgument(name = "userId") String id) {
        accountService.sendWelcomeMailAgain(id);
    }

    @GraphQLQuery(name = "reCaptchaClientKey")
    public String reCaptchaClientKey() {
        return reCaptcha.clientKey();
    }


}
