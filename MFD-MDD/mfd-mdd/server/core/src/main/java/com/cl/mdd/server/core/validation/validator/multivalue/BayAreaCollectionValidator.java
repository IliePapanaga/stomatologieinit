package com.cl.mdd.server.core.validation.validator.multivalue;

import com.cl.mdd.server.core.validation.constraint.BayArea;
import com.cl.mdd.server.core.validation.validator.BayAreaValidator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Objects;

public class BayAreaCollectionValidator implements ConstraintValidator<BayArea, Collection<String>> {

    @Autowired
    private BayAreaValidator validator;

    @Override
    public void initialize(BayArea constraintAnnotation) {
    }

    @Override
    public boolean isValid(Collection<String> areas, ConstraintValidatorContext context) {
        return Objects.isNull(areas) || areas.stream().allMatch(area -> validator.isValid(area, context));
    }
}
