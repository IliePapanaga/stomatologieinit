package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.access.common.WeekDayDao;
import com.cl.mdd.server.core.validation.annotation.Validator;
import com.cl.mdd.server.core.validation.constraint.WeekDay;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

@Validator
public class WeekDayValidator implements ConstraintValidator<WeekDay, String> {

    @Autowired
    private WeekDayDao weekDayDao;

    @Override
    public void initialize(WeekDay constraintAnnotation) {
    }

    @Override
    public boolean isValid(String weekday, ConstraintValidatorContext context) {
        return Objects.isNull(weekday) || weekDayDao.exists(weekday);
    }
}
