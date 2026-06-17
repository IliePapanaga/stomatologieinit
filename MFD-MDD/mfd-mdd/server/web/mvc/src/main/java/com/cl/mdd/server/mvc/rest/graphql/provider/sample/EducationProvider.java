package com.cl.mdd.server.mvc.rest.graphql.provider.sample;

import com.cl.mdd.server.core.data.model.common.EducationModel;
import com.cl.mdd.server.core.service.common.EducationService;
import com.cl.mdd.server.mvc.rest.graphql.provider.GraphQLProvider;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Education data provider.
 * <p/>
 * Exposes Education's operations to graph ql.
 */
@Component
public class EducationProvider implements GraphQLProvider {

    @Autowired
    private EducationService educationService;

    @GraphQLQuery(name = "education")
    public EducationModel get(@GraphQLNonNull @GraphQLArgument(name = "id") String id) {
        return educationService.get(id);
    }

    @GraphQLQuery(name = "educations")
    public List<EducationModel> getAll() {
        return educationService.getAll();
    }

}
