package com.cl.mdd.server.mvc.rest.professional;

import com.cl.mdd.server.core.data.model.RegisterProfessional;
import com.cl.mdd.server.core.data.model.common.ProfessionalSubcategoryModel;
import com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@ActiveProfiles("hsqldb-local")
public class ProfessionalSpecialityCRUDIT extends BaseMvcIntegrationTest {

    @Autowired
    private ProfessionalWorker professionalWorker;

    private RegisterProfessional professional;

    @Before
    public void setUp() throws Exception {
        professional = create(RegisterProfessional.class);
        professionalWorker.registerAndActivate(professional);
    }

    @Test
    public void testCRUD() throws Exception {
        professionalWorker.addSubCategories(singleton("DA"), professional);
        List<ProfessionalSubcategoryModel> professionalSubcategories = professionalWorker.listProfessionalSubCategories(professional, "");
        assertThat(professionalSubcategories.size(), is(1));
        assertSubcategory(professionalSubcategories.get(0), "DA", "DA", "Assistants", "PENDING");

        professionalWorker.addSubCategories(singleton("RDA"), professional);
        professionalSubcategories = professionalWorker.listProfessionalSubCategories(professional, "");
        assertThat(professionalSubcategories.size(), is(2));
        assertSubcategory(professionalSubcategories.get(0), "DA", "DA", "Assistants", "PENDING");
        assertSubcategory(professionalSubcategories.get(1), "RDA", "RDA", "Assistants", "PENDING");

        professionalWorker.addSubCategories(singleton("RDH"), professional);
        professionalSubcategories = professionalWorker.listProfessionalSubCategories(professional, "");
        assertThat(professionalSubcategories.size(), is(3));
        assertSubcategory(professionalSubcategories.get(0), "DA", "DA", "Assistants", "PENDING");
        assertSubcategory(professionalSubcategories.get(1), "RDA", "RDA", "Assistants", "PENDING");
        assertSubcategory(professionalSubcategories.get(2), "RDH", "RDH", "Hygienists", "PENDING");

        professionalWorker.deleteProSubCategory("RDH", professional);
        professionalSubcategories = professionalWorker.listProfessionalSubCategories(professional, "");
        assertThat(professionalSubcategories.size(), is(2));
        assertSubcategory(professionalSubcategories.get(0), "DA", "DA", "Assistants", "PENDING");
        assertSubcategory(professionalSubcategories.get(1), "RDA", "RDA", "Assistants", "PENDING");

        professionalWorker.deleteProSubCategory("RDA", professional);
        professionalSubcategories = professionalWorker.listProfessionalSubCategories(professional, "");
        assertThat(professionalSubcategories.size(), is(1));
        assertSubcategory(professionalSubcategories.get(0), "DA", "DA", "Assistants", "PENDING");

        professionalWorker.deleteProSubCategory("DA", professional);
        professionalSubcategories = professionalWorker.listProfessionalSubCategories(professional, "");
        assertThat(professionalSubcategories.size(), is(0));

        //DELETE DELETED
        professionalWorker.deleteProSubCategory("RDA", professional);
        professionalWorker.deleteProSubCategory("DA", professional);

        professionalWorker.addSubCategories(singleton("FINANCIAL_COORDINATOR"), professional);
        professionalSubcategories = professionalWorker.listProfessionalSubCategories(professional, "");
        assertThat(professionalSubcategories.size(), is(1));
        assertSubcategory(professionalSubcategories.get(0), "FINANCIAL_COORDINATOR", "Financial coordinator", "Front Office Personnel", "APPROVED");

        for (String sort : Sets.newHashSet(
                "SUBCATEGORY_NAME_ASC",
                "SUBCATEGORY_NAME_DESC",
                "CATEGORY_NAME_ASC",
                "CATEGORY_NAME_DESC",
                "STATUS_ASC",
                "STATUS_DESC")) {
            assertThat(professionalWorker.listProfessionalSubCategories(professional, sort).size(), is(1));
        }

    }

    private void assertSubcategory(ProfessionalSubcategoryModel professionalSubcategoryModel, String id, String name, String subcategoryName, String status) {
        assertThat(professionalSubcategoryModel.getId(), is(id));
        assertThat(professionalSubcategoryModel.getSubCategoryName(), is(name));
        assertThat(professionalSubcategoryModel.getCategoryName(), is(subcategoryName));
        assertThat(professionalSubcategoryModel.getStatus(), is(status));
    }

}
