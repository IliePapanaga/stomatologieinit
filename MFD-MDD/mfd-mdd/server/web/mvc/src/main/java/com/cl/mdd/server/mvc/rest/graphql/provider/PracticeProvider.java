package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.PracticeModel;
import com.cl.mdd.server.core.data.model.common.SpecialityModel;
import com.cl.mdd.server.core.service.practice.PracticeService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Practice data provider.
 * <p/>
 * Exposes practice's operations to graph ql.
 */
@Component
public class PracticeProvider implements GraphQLProvider {

    @Autowired
    private PracticeService practiceService;

    @GraphQLQuery(name = "practice")
    public PracticeModel getById(@GraphQLArgument(name = "id") String id) {
        return practiceService.getById(id);
    }

    @GraphQLQuery(name = "listPracticeSpecialities")
    public List<SpecialityModel> getPracticeSpecialities(@GraphQLArgument(name = "practice") @GraphQLContext PracticeModel practiceModel) {
        return practiceService.getPracticeSpecialities(practiceModel);
    }

}
