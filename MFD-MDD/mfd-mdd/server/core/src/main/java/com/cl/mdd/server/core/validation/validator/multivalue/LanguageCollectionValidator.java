package com.cl.mdd.server.core.validation.validator.multivalue;

import com.cl.mdd.server.core.validation.constraint.Language;
import com.cl.mdd.server.core.validation.validator.LanguageValidator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

import static java.util.Objects.isNull;

public class LanguageCollectionValidator implements ConstraintValidator<Language, Collection<String>> {

    @Autowired
    private LanguageValidator languageValidator;

    @Override
    public void initialize(Language constraintAnnotation) {
    }

    @Override
    public boolean isValid(Collection<String> languages, ConstraintValidatorContext context) {
        return isNull(languages) || languages.stream().allMatch(language -> languageValidator.isValid(language, context));
    }
}
