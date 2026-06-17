package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.access.common.SpecialityDao;
import com.cl.mdd.server.core.validation.annotation.Validator;
import com.cl.mdd.server.core.validation.constraint.Speciality;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

@Validator
public class SpecialityValidator implements ConstraintValidator<Speciality, String> {

    @Autowired
    private SpecialityDao specialityDao;

    @Override
    public void initialize(Speciality constraintAnnotation) {

    }

    @Override
    public boolean isValid(String id, ConstraintValidatorContext context) {
        return Objects.isNull(id) || specialityDao.exists(id);
    }
}
