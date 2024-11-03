package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.ChangeUserInfoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidChangeUsernameValidator implements ConstraintValidator<ValidChangeUsername, ChangeUserInfoForm> {
    @Qualifier("userServiceImpl")
    @Autowired
    private UserService us;

    @Override
    public boolean isValid(ChangeUserInfoForm form, ConstraintValidatorContext constraintValidatorContext) {
        String username = form.getUsername();
        String oldUsername = form.getOldUsername();
        if(username.equals(oldUsername)) return true;
        return   !us.usernameExists(username);
    }
}
