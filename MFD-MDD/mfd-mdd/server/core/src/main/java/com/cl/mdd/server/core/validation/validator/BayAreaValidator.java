package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.access.common.BayAreaDao;
import com.cl.mdd.server.core.validation.annotation.Validator;
import com.cl.mdd.server.core.validation.constraint.BayArea;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

@Validator
public class BayAreaValidator implements ConstraintValidator<BayArea, String> {

    @Autowired
    private BayAreaDao bayAreaDao;

    @Override
    public void initialize(BayArea constraintAnnotation) {

    }

    @Override
    public boolean isValid(String area, ConstraintValidatorContext context) {
        return Objects.isNull(area) || bayAreaDao.exists(area);
    }
}
