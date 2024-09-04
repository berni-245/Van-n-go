package ar.edu.itba.paw.webapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ArrayAllMinValidator implements ConstraintValidator<ArrayAllMin, int[]> {
    private int minValue;

    @Override
    public void initialize(ArrayAllMin constraintAnnotation) {
        minValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(int[] values, ConstraintValidatorContext constraintValidatorContext) {
        for (Integer value : values) {
            if (value.compareTo(minValue) < 0) return false;
        }
        return true;
    }
}