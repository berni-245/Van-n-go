package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.Language;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidLanguageValidator implements ConstraintValidator<ValidLanguage, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        for(Language language : Language.values()) {
            if(language.name().equals(value)) return true;
        }
        return false;
    }
}
