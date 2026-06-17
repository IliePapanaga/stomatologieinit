package com.cl.mdd.server.core.service.specialty;

import com.cl.mdd.server.core.data.model.common.SubcategoryModel;
import com.cl.mdd.server.core.data.persistent.access.specialty.SubCategoryDao;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.manager.user.SubcategoryManager;
import com.cl.mdd.server.core.service.ServiceSupport;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SubcategoryServiceImpl extends ServiceSupport implements SubcategoryService {

    @Autowired
    private SubcategoryManager subcategoryManager;


    @Override
    public SubcategoryModel get(String id) {
        return commonConverter.toSubcategoryModel(subcategoryManager.findOne(id));
    }

    @Override
    public List<SubcategoryModel> getAll() {
        List<SubCategory> categories = subcategoryManager.findAll();
        return CollectionUtils.emptyIfNull(categories).stream()
                .map(commonConverter::toSubcategoryModel)
                .collect(Collectors.toList());
    }

}
