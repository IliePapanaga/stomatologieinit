package com.cl.mdd.server.core.validation.validator.multivalue;

import com.cl.mdd.server.core.validation.constraint.Speciality;
import com.cl.mdd.server.core.validation.validator.SpecialityValidator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

import static java.util.Objects.isNull;

public class SpecialityCollectionValidator implements ConstraintValidator<Speciality, Collection<String>> {

    @Autowired
    private SpecialityValidator specialityValidator;

    @Override
    public void initialize(Speciality constraintAnnotation) {

    }

    @Override
    public boolean isValid(Collection<String> specialities, ConstraintValidatorContext context) {
        return isNull(specialities) || specialities.stream().allMatch(speciality -> specialityValidator.isValid(speciality, context));
    }
}
