
package ar.edu.itba.paw.webapp.validation;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidImageValidator implements ConstraintValidator<ValidImage, byte[]> {
    @Override
    public boolean isValid(byte[] value, ConstraintValidatorContext context) {
        return value == null || value.length < 10 * 1024 * 1024;
    }
}

