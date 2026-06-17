package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.access.common.LanguageDao;
import com.cl.mdd.server.core.validation.annotation.Validator;
import com.cl.mdd.server.core.validation.constraint.Language;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

@Validator
public class LanguageValidator implements ConstraintValidator<Language, String> {

    @Autowired
    private LanguageDao languageDao;

    @Override
    public void initialize(Language constraintAnnotation) {
    }

    @Override
    public boolean isValid(String language, ConstraintValidatorContext context) {
        return isNull(language) || languageDao.exists(language);
    }
}
