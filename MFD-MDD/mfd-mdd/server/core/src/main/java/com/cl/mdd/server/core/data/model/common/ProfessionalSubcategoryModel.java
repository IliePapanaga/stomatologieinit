package com.cl.mdd.server.core.data.model.common;

import com.cl.mdd.server.core.data.model.MDDModel;

public class ProfessionalSubcategoryModel extends MDDModel {

    private String id;

    private String subCategoryName;

    private String categoryName;

    private String status;

    public String getId() {
        return id;
    }

    public ProfessionalSubcategoryModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public ProfessionalSubcategoryModel setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
        return this;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public ProfessionalSubcategoryModel setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public ProfessionalSubcategoryModel setStatus(String status) {
        this.status = status;
        return this;
    }
}
