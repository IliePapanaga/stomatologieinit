package com.cl.mdd.server.core.service.specialty;

import com.cl.mdd.server.core.data.model.common.CategoryModel;
import com.cl.mdd.server.core.data.persistent.access.specialty.CategoryDao;
import com.cl.mdd.server.core.data.persistent.model.specialty.Category;
import com.cl.mdd.server.core.service.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;


@Service
public class CategoryServiceImpl extends ServiceSupport implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;


    @Override
    public CategoryModel get(String id) {
        return this.toCategoryModel(categoryDao.findOne(id));
    }

    @Override
    public List<CategoryModel> getAll() {
        List<Category> categories = categoryDao.findAll();
        return categories.stream()
                .map(this::toCategoryModel)
                .collect(Collectors.toList());
    }

    public CategoryModel toCategoryModel(Category category) {
        CategoryModel model = new CategoryModel();
        model.setId(category.getId());
        model.setName(category.getName());
        model.setSubCategories(category
                .getSubCategories()
                .stream()
                .map(commonConverter::toSubcategoryModel)
                .collect(toSet()));
        return model;
    }

}
