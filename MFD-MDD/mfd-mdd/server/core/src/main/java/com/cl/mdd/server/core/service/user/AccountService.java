package com.cl.mdd.server.core.service.user;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.service.Service;
import com.cl.mdd.server.core.validation.constraint.composite.Username;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface AccountService extends Service {

    UserInfo userInfo(String id);

    PracticeOwnerModel register(@Valid RegisterPracticeOwner model);

    ProfessionalModel register(@Valid RegisterProfessional model);

    SystemUserModel register(@Valid RegisterSystemUser model);

    void requestChangeUsername(String id, @Valid ChangeUsername changeUsername);

    /**
     * @param token
     * @return new Username
     */
    String confirmUsernameChange(@NotNull(message = "{confirm.username.change.token.not.null}") String token);

    void completeRegistration(@NotNull(message = "{complete.registration.token.not.null}") String token);

    void completeRegistrationSystemUser(@NotNull(message = "{complete.registration.token.not.null}") String token,
                                        @NotNull(message = "{password.not.null}") String newPassword);

    void sendWelcomeMailAgain(String userId);

    void changePassword(String userId, @Valid ChangePassword changePassword);

    void requestResetPassword(@Username(unique = false) @NotNull(message = "{username.not.null}") String username);

    void resetPassword(@Valid NewPassword newPassword);

    void update(@Valid ProfessionalModel professionalModel, @Valid ProfessionalJobPreferenceModel jobPreference);

    void update(@Valid PracticeOwnerModel practiceOwnerModel, @Valid PracticeModel updatePractice);
}