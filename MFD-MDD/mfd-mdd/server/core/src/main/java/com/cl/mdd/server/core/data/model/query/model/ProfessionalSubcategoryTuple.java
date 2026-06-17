package com.cl.mdd.server.core.data.model.query.model;

import com.cl.mdd.server.core.data.model.MDDModel;

public class ProfessionalSubcategoryTuple extends MDDModel {

    private String id;

    private String subCategoryName;

    private String categoryName;

    private String status;

    public ProfessionalSubcategoryTuple(String id,
                                        String subCategoryName,
                                        String categoryName,
                                        String status) {
        this.id = id;
        this.subCategoryName = subCategoryName;
        this.categoryName = categoryName;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getStatus() {
        return status;
    }
}
