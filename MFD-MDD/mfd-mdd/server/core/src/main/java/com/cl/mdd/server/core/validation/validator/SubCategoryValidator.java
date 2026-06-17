package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.access.specialty.SubCategoryDao;
import com.cl.mdd.server.core.validation.annotation.Validator;
import com.cl.mdd.server.core.validation.constraint.SubCategory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

@Validator
public class SubCategoryValidator implements ConstraintValidator<SubCategory, String> {

    @Autowired
    private SubCategoryDao subCategoryDao;

    @Override
    public void initialize(SubCategory constraintAnnotation) {

    }

    @Override
    public boolean isValid(String id, ConstraintValidatorContext context) {
        return Objects.isNull(id) || subCategoryDao.exists(id);
    }
}
