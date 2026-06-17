package com.cl.mdd.server.core.validation.validator.multivalue;

import com.cl.mdd.server.core.validation.constraint.SubCategory;
import com.cl.mdd.server.core.validation.validator.SubCategoryValidator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

import static java.util.Objects.isNull;

public class SubCategoryCollectionValidator implements ConstraintValidator<SubCategory, Collection<String>> {

    @Autowired
    private SubCategoryValidator categoryValidator;

    @Override
    public void initialize(SubCategory constraintAnnotation) {

    }

    @Override
    public boolean isValid(Collection<String> subCategories, ConstraintValidatorContext context) {
        return isNull(subCategories) || subCategories.stream().allMatch(subCategory -> categoryValidator.isValid(subCategory, context));
    }
}
