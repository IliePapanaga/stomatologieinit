package com.cl.mdd.server.mvc.rest.graphql.provider.sample;

import com.cl.mdd.server.core.data.model.common.LanguageModel;
import com.cl.mdd.server.core.data.model.common.SubcategoryModel;
import com.cl.mdd.server.core.service.common.LanguageService;
import com.cl.mdd.server.core.service.specialty.SubcategoryService;
import com.cl.mdd.server.mvc.rest.graphql.provider.GraphQLProvider;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Language data provider.
 * <p/>
 * Exposes language's operations to graph ql.
 */
@Component
public class LanguageProvider implements GraphQLProvider {

    @Autowired
    private LanguageService languageService;

    @GraphQLQuery(name = "language")
    public LanguageModel get(@GraphQLNonNull @GraphQLArgument(name = "id") String id) {
        return languageService.get(id);
    }

    @GraphQLQuery(name = "languages")
    public List<LanguageModel> getAll() {
        return languageService.getAll();
    }

    /**
     * Subcategory data provider.
     * <p/>
     * Exposes subcategory's operations to graph ql.
     */
    @Component
    public static class SubcategoryProvider implements GraphQLProvider {

        @Autowired
        private SubcategoryService subcategoryService;

        @GraphQLQuery(name = "subcategory")
        public SubcategoryModel get(@GraphQLNonNull @GraphQLArgument(name = "id") String id) {
            return subcategoryService.get(id);
        }

        @GraphQLQuery(name = "subcategories")
        public List<SubcategoryModel> getAll() {
            return subcategoryService.getAll();
        }

    }
}
