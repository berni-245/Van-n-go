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
        return  !form.getUsernameChanged() || (isSizeValid(username) && username.matches("^[a-zA-Z]\\w*$") &&!us.usernameExists(username));
    }

    private boolean isSizeValid(String username){
        return username != null && username.length() >= 5 && username.length() <= 20;
    }
}
