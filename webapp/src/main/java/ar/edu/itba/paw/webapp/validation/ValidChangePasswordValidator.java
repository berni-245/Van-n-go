package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.form.ChangeUserInfoForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidChangePasswordValidator implements ConstraintValidator<ValidChangePasssword, ChangeUserInfoForm> {

    public boolean isValid(ChangeUserInfoForm form, ConstraintValidatorContext constraintValidatorContext) {
        String password = form.getPassword();
        return  !form.getPasswordChanged() || (isSizeValid(password) &&
                password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]+$") &&
                password.equals(form.getConfirmPassword()));
    }

    private boolean isSizeValid(String password){
        return password != null && password.length() >= 8 && password.length() <= 32;
    }
}
