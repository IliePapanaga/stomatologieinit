package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.common.ProfessionalSubcategoryModel;
import com.cl.mdd.server.core.data.model.query.FindProfessionalSubcategories;
import com.cl.mdd.server.core.service.user.ProfessionalSubcategoryService;
import com.cl.mdd.server.mvc.rest.graphql.model.Connection;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.cl.mdd.server.mvc.rest.graphql.model.Connection.fromQueryResult;

@Component
public class ProfessionalSubcategoryProvider implements GraphQLProvider {

    @Autowired
    private ProfessionalSubcategoryService professionalSubcategoryService;

    @Autowired
    private WebSecurityAccess webSecurityAccess;

    @GraphQLMutation(name = "addProfessionalSubcategories",
            description = "Add provided subcategories to professional subcategories")
    public void addProfessionalSubcategories(@GraphQLArgument(name = "subcategories") Set<String> subCategories) {
        professionalSubcategoryService.addProfessionalSubcategories(webSecurityAccess.currentUserId(), subCategories);
    }

    @GraphQLMutation(name = "deleteProfessionalSubcategory",
            description = "Remove professional subcategory by id")
    public void deleteProfessionalSubcategory(@GraphQLNonNull @GraphQLArgument(name = "id") String id) {
        professionalSubcategoryService.deleteProfessionalSubcategory(webSecurityAccess.currentUserId(), id);
    }

    @GraphQLQuery(name = "professionalSubcategories",
            description = "List current logged in professional subcategories")
    public Connection<ProfessionalSubcategoryModel> listProfessionalSubcategories(@GraphQLArgument(name = "page") Integer page,
                                                                                  @GraphQLArgument(name = "perPage") Integer perPage,
                                                                                  @GraphQLArgument(name = "orders") List<FindProfessionalSubcategories.FindProfessionalSubcategoriesOrders> orders) {
        return listProfessionalSubcategoriesByProfessionalId(webSecurityAccess.currentUserId(),
                page,
                perPage,
                orders);
    }

    @GraphQLQuery(name = "professionalSubcategoriesByProfessionalId",
            description = "List professional subcategories by Professional id")
    public Connection<ProfessionalSubcategoryModel> listProfessionalSubcategoriesByProfessionalId(
            @GraphQLNonNull @GraphQLArgument(name = "professionalId") String professionalId,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "perPage") Integer perPage,
            @GraphQLArgument(name = "orders") List<FindProfessionalSubcategories.FindProfessionalSubcategoriesOrders> orders) {
        FindProfessionalSubcategories queryInfo = new FindProfessionalSubcategories();
        queryInfo.getFilters().setProfessionalId(professionalId);
        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);
        return fromQueryResult(professionalSubcategoryService.fetch(queryInfo));
    }
}
