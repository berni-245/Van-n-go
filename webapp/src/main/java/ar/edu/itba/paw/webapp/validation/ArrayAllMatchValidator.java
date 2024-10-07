package ar.edu.itba.paw.webapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ArrayAllMatchValidator implements ConstraintValidator<ArrayAllMatch, String[]> {
    private String regexp;

    @Override
    public void initialize(ArrayAllMatch constraintAnnotation) {
        regexp = constraintAnnotation.regexp();
    }

    @Override
    public boolean isValid(String[] values, ConstraintValidatorContext constraintValidatorContext) {
        for (String value : values) {
            if (!value.matches(regexp)) return false;
        }
        return true;
    }
}