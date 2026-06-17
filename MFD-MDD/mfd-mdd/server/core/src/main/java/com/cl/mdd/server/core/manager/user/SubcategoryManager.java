package com.cl.mdd.server.core.manager.user;

import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;

import java.util.List;
import java.util.Set;

public interface SubcategoryManager {
    List<SubCategory> findAll();
    SubCategory findOne(String id);
    Set<String> mandatoryCertificatesForSubcategory(SubCategory subCategory);
}
