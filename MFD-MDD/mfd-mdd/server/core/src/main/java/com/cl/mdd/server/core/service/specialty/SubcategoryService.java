package com.cl.mdd.server.core.service.specialty;

import com.cl.mdd.server.core.data.model.common.SubcategoryModel;

import java.util.List;

public interface SubcategoryService {

    SubcategoryModel get(String id);

    List<SubcategoryModel> getAll();

}
