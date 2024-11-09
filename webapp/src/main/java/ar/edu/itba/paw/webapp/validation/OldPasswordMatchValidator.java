package ar.edu.itba.paw.webapp.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OldPasswordMatchValidator extends ValidatorWithLoggedUser implements ConstraintValidator<OldPasswordMatch, String> {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean isValid(String oldPassword, ConstraintValidatorContext context) {
        return passwordEncoder.matches(oldPassword, getUser().getPassword());
    }
}