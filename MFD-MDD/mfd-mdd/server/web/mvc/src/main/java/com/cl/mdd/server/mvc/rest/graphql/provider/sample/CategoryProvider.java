package com.cl.mdd.server.mvc.rest.graphql.provider.sample;

import com.cl.mdd.server.core.data.model.common.CategoryModel;
import com.cl.mdd.server.core.service.specialty.CategoryService;
import com.cl.mdd.server.mvc.rest.graphql.provider.GraphQLProvider;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Category data provider.
 * <p/>
 * Exposes category's operations to graph ql.
 */
@Component
public class CategoryProvider implements GraphQLProvider {

    @Autowired
    private CategoryService categoryService;

    @GraphQLQuery(name = "category")
    public CategoryModel get(@GraphQLNonNull @GraphQLArgument(name = "id") String id) {
        return categoryService.get(id);
    }

    @GraphQLQuery(name = "categories")
    public List<CategoryModel> getAll() {
        return categoryService.getAll();
    }

}
