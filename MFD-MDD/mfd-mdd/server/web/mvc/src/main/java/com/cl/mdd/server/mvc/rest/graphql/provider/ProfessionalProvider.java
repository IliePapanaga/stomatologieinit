package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.ProfessionalModel;
import com.cl.mdd.server.core.data.model.ProfessionalProfileModel;
import com.cl.mdd.server.core.data.model.UserActivateDeactivateResult;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.service.user.ProfessionalService;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Professional data provider.
 * <p/>
 * Exposes professional's operations to graph ql.
 */
@Component
public class ProfessionalProvider implements GraphQLProvider {

    @Autowired
    private ProfessionalService professionalService;

    @Autowired
    private WebSecurityAccess webSecurityAccess;

    @GraphQLQuery(name = "professional")
    public ProfessionalModel get(@GraphQLArgument(name = "id") String id) {
        return professionalService.get(id);
    }

    @GraphQLQuery(name = "jobPreference")
    public ProfessionalJobPreferenceModel jobPreference(@GraphQLContext ProfessionalModel professionalModel) {
        return professionalService.getProfessionalJobPreference(professionalModel.getId());
    }

    @GraphQLQuery(name = "profile")
    public ProfessionalProfileModel profile(@GraphQLContext ProfessionalModel professionalModel) {
        return professionalService.getProfessionalProfile(professionalModel.getId());
    }

    @GraphQLMutation(name = "updateProfessionalProfile")
    public void updateProfessionalProfile(@GraphQLArgument(name = "profile") ProfessionalProfileModel professionalProfileModel) {
        professionalService.updateProfile(webSecurityAccess.currentUserId(), professionalProfileModel);
    }

    @GraphQLMutation(name = "updateProfessionalProfile")
    public void updateProfessionalProfile(@GraphQLArgument(name = "professionalId") String professionalId, @GraphQLArgument(name = "profile") ProfessionalProfileModel professionalProfileModel) {
        professionalService.updateProfile(professionalId, professionalProfileModel);
    }

    @GraphQLMutation(name = "activateDeactivateProfessional")
    public UserActivateDeactivateResult activateDeactivateProfessional(@GraphQLArgument(name = "id")String id,
                                                                        @GraphQLArgument(name = "enabled") boolean enabled){
        return professionalService.activateDeactivateAccount(id, enabled);
    }
}
