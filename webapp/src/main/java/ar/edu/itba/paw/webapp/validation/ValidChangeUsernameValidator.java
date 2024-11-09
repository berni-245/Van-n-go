package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidChangeUsernameValidator extends ValidatorWithLoggedUser implements ConstraintValidator<ValidChangeUsername, String> {
    @Autowired
    private ClientService cs;
    @Autowired
    private DriverService ds;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if (getUser().getUsername().equals(username)) return true;
        return !(cs.usernameExists(username) || ds.usernameExists(username));
    }
}
