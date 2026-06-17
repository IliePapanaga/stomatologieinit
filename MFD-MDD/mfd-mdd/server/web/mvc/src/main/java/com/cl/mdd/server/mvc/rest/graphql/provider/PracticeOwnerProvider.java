package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.PracticeModel;
import com.cl.mdd.server.core.data.model.PracticeOwnerModel;
import com.cl.mdd.server.core.data.model.UserActivateDeactivateResult;
import com.cl.mdd.server.core.service.user.PracticeOwnerService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Practice owner data provider.
 * <p/>
 * Exposes practice owner's operations to graph ql.
 */
@Component
public class PracticeOwnerProvider implements GraphQLProvider {

    @Autowired
    private PracticeOwnerService practiceOwnerService;

    @GraphQLQuery(name = "practiceOwner")
    public PracticeOwnerModel get(@GraphQLArgument(name = "id") String id) {

        return practiceOwnerService.get(id);
    }

    @GraphQLQuery(name = "practiceOwner")
    public PracticeOwnerModel get(@GraphQLArgument(name = "practice") @GraphQLContext PracticeModel practiceModel) {

        return practiceOwnerService.get(practiceModel.getId());
    }

    @GraphQLMutation(name = "activateDeactivatePracticeOwner")
    public UserActivateDeactivateResult activateDeactivatePracticeOwner(@GraphQLArgument(name = "id")String id,
                                                                   @GraphQLArgument(name = "enabled") boolean enabled){
        return practiceOwnerService.activateDeactivateAccount(id, enabled);
    }

}
