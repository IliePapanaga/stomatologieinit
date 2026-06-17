package com.cl.mdd.server.core.data.model.common;

import com.cl.mdd.server.core.data.model.MDDModel;
import com.cl.mdd.server.core.data.model.certificates.CertificateTypeModel;

import java.util.Set;

public class SubcategoryModel extends MDDModel {

    private String id;

    private String name;

    private CategoryModel category;

    private Set<CertificateTypeModel> certificateTypes;

    public String getName() {
        return name;
    }

    public SubcategoryModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getId() {
        return id;
    }

    public SubcategoryModel setId(String id) {
        this.id = id;
        return this;
    }

    public CategoryModel getCategory() {
        return category;
    }

    public SubcategoryModel setCategory(CategoryModel category) {
        this.category = category;
        return this;
    }

    public Set<CertificateTypeModel> getCertificateTypes() {
        return certificateTypes;
    }

    public void setCertificateTypes(Set<CertificateTypeModel> certificateTypes) {
        this.certificateTypes = certificateTypes;
    }
}
