package com.cl.mdd.server.core.service.specialty;

import com.cl.mdd.server.core.data.model.common.CategoryModel;

import java.util.List;

public interface CategoryService {

    CategoryModel get(String id);

    List<CategoryModel> getAll();

}
