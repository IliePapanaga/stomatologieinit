package com.cl.mdd.server.core.validation.validator.multivalue;

import com.cl.mdd.server.core.validation.constraint.WeekDay;
import com.cl.mdd.server.core.validation.validator.WeekDayValidator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Objects;

public class WeekDayCollectionValidator implements ConstraintValidator<WeekDay, Collection<String>> {

    @Autowired
    private WeekDayValidator validator;

    @Override
    public void initialize(WeekDay constraintAnnotation) {
    }

    @Override
    public boolean isValid(Collection<String> weekdays, ConstraintValidatorContext context) {
        return Objects.isNull(weekdays) || weekdays.stream().allMatch(weekDay -> validator.isValid(weekDay, context));
    }
}
