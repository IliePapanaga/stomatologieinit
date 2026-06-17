package com.cl.mdd.server.mvc.rest.graphql.provider.sample;

import com.cl.mdd.server.core.data.model.common.AcademicDegreeModel;
import com.cl.mdd.server.core.service.common.AcademicDegreeService;
import com.cl.mdd.server.mvc.rest.graphql.provider.GraphQLProvider;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Academic degree data provider.
 * <p/>
 * Exposes Academic degree's operations to graph ql.
 */
@Component
public class AcademicDegreeProvider implements GraphQLProvider {

    @Autowired
    private AcademicDegreeService academicDegreeService;

    @GraphQLQuery(name = "academicDegree")
    public AcademicDegreeModel get(@GraphQLNonNull @GraphQLArgument(name = "id") String id) {
        return academicDegreeService.get(id);
    }

    @GraphQLQuery(name = "academicDegrees")
    public List<AcademicDegreeModel> getAll() {
        return academicDegreeService.getAll();
    }

}
