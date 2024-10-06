package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class ValidChangeUsernameValidator implements ConstraintValidator<ValidChangeUsername, String> {
    @Qualifier("userServiceImpl")
    @Autowired
    private UserService us;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        return  /* check si no cambio*/(!us.usernameExists(username) && username.matches("^[a-zA-Z]\\w*$"));
    }
}
