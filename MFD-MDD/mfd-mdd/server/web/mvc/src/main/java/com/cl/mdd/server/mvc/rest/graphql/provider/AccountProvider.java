package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.AddressModel;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.service.user.AccountService;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Account data provider.
 * <p/>
 * Exposes user's operations to graph ql.
 */
@Component
public class AccountProvider implements GraphQLProvider {

    @Autowired
    private AccountService accountService;

    @Autowired
    private WebSecurityAccess securityAccess;

    @GraphQLMutation(name = "changePassword")
    public void changePassword(@GraphQLArgument(name = "changePassword") ChangePassword changePassword) {
        accountService.changePassword(securityAccess.currentUserId(), changePassword);
    }

    @GraphQLMutation(name = "requestChangeUsername")
    public void requestChangeUsername(@GraphQLArgument(name = "request") ChangeUsername changeUsername) {
        accountService.requestChangeUsername(securityAccess.currentUserId(), changeUsername);
    }

    @GraphQLQuery(name = "currentAuthenticatedUserInfo")
    public UserInfo currentAuthenticatedUserInfo() {
        UserInfo user = accountService.userInfo(securityAccess.currentUserId());
        String realId = securityAccess.currentRealUserId();
        if (!StringUtils.equalsIgnoreCase(user.getId(), realId)) {
            user.setRealUser(accountService.userInfo(realId));
        }
        return user;
    }

    @GraphQLMutation(name = "updateProfessionalGeneral")
    public void updateProfessionalGeneral(@GraphQLArgument(name = "professional") ProfessionalModel professionalModel,
                                          @GraphQLArgument(name = "jobPreference") ProfessionalJobPreferenceModel jobPreference) {
        accountService.update(professionalModel, jobPreference);
    }

    @GraphQLMutation(name = "updatePracticeOwnerGeneral")
    public void updatePracticeOwnerGeneral(@GraphQLArgument(name = "practiceOwner") PracticeOwnerModel practiceOwnerModel,
                                           @GraphQLArgument(name = "practice") PracticeModel updatePractice) {
        accountService.update(practiceOwnerModel, updatePractice);
    }

    @GraphQLMutation(name = "registerSystemUser")
    public SystemUserModel register(@GraphQLArgument(name = "systemUser") RegisterSystemUser registerSystemUser) {
        // System user has same model as other users. So, a lot of restrictions valid for other users are applied
        // automatically. Currently system user model is filled with some stub data to avoid this. To be discussed later
        fillSystemUserFieldsWithStubData(registerSystemUser);
        return accountService.register(registerSystemUser);
    }

    private void fillSystemUserFieldsWithStubData(RegisterSystemUser registerSystemUser) {
        registerSystemUser
//                .setPassword("ABCdef123$%^")
                .getContact().setAddress(
                new AddressModel()
                        .setCity("N/A")
                        .setCountry("N/A")
                        .setState("N/A")
                        .setStreet("N/A")
                        .setZipCode("00000")
        )
                .setEmail(registerSystemUser.getUsername())
                .setPhone(registerSystemUser.getContact().getPhone());
    }
}
