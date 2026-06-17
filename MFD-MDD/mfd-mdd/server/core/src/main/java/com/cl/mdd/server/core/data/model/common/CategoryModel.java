package com.cl.mdd.server.core.data.model.common;

import com.cl.mdd.server.core.data.model.MDDModel;
import com.cl.mdd.server.core.validation.group.Save;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class CategoryModel extends MDDModel {

    private String id;

    @NotNull(groups = Save.class, message = "{invalid.category.name}")
    private String name;

    @Valid
    private Set<SubcategoryModel> subCategories;

    public String getName() {
        return name;
    }

    public CategoryModel setName(String name) {
        this.name = name;
        return this;
    }

    public Set<SubcategoryModel> getSubCategories() {
        return subCategories;
    }

    public CategoryModel setSubCategories(Set<SubcategoryModel> subCategories) {
        this.subCategories = subCategories;
        return this;
    }

    public String getId() {
        return id;
    }

    public CategoryModel setId(String id) {
        this.id = id;
        return this;
    }
}
