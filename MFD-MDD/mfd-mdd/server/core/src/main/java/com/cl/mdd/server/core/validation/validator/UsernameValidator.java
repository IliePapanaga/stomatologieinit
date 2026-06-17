package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.access.user.UserDao;
import com.cl.mdd.server.core.validation.constraint.composite.Username;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<Username, String> {

    @Autowired
    private UserDao userDao;

    private boolean unique;

    @Override
    public void initialize(Username constraintAnnotation) {
        unique = constraintAnnotation.unique();
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return !unique || userDao.countByUsernameIgnoreCase(username) == 0;
    }
}
