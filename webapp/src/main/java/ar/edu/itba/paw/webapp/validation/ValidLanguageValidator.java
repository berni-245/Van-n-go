package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.Language;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class ValidLanguageValidator implements ConstraintValidator<ValidLanguage, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Arrays.stream(Language.values()).anyMatch(language -> language.name().equals(value));
    }
}
