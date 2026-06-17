package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.access.common.EducationDao;
import com.cl.mdd.server.core.validation.constraint.Education;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class EducationValidator implements ConstraintValidator<Education, String> {

    @Autowired
    private EducationDao educationDao;

    @Override
    public void initialize(Education constraintAnnotation) {
    }

    @Override
    public boolean isValid(String education, ConstraintValidatorContext context) {
        return isNull(education) || educationDao.exists(education);
    }
}
