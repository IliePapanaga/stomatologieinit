package com.cl.mdd.server.core.service.user;

import com.cl.mdd.server.core.data.model.common.ProfessionalSubcategoryModel;
import com.cl.mdd.server.core.data.model.query.FindProfessionalSubcategories;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.validation.constraint.SubCategory;

import javax.validation.Valid;
import java.util.Set;

public interface ProfessionalSubcategoryService {

    void addProfessionalSubcategories(String professionalId, @Valid @SubCategory Set<String> subCategories);

    void deleteProfessionalSubcategory(String professionalId, @Valid @SubCategory String subcategoryId);

    QueryResult<ProfessionalSubcategoryModel> fetch(FindProfessionalSubcategories findProfessionalSubcategories);
}
