package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.access.common.AcademicDegreeDao;
import com.cl.mdd.server.core.validation.constraint.AcademicDegree;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class AcademicDegreeValidator implements ConstraintValidator<AcademicDegree, String> {

    @Autowired
    private AcademicDegreeDao academicDegreeDao;

    @Override
    public void initialize(AcademicDegree constraintAnnotation) {
    }

    @Override
    public boolean isValid(String academicDegree, ConstraintValidatorContext context) {
        return isNull(academicDegree) || academicDegreeDao.exists(academicDegree);
    }
}
