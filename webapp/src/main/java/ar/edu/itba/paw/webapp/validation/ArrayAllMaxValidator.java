package ar.edu.itba.paw.webapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ArrayAllMaxValidator implements ConstraintValidator<ArrayAllMax, int[]> {
    private int maxValue;

    @Override
    public void initialize(ArrayAllMax constraintAnnotation) {
        maxValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(int[] values, ConstraintValidatorContext constraintValidatorContext) {
        for (Integer value : values) {
            if (value.compareTo(maxValue) > 0) return false;
        }
        return true;
    }
}