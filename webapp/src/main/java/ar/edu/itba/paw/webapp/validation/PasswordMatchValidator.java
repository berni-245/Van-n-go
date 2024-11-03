package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.services.ZoneService;
import ar.edu.itba.paw.webapp.form.ChangePasswordForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, PasswordValidator>  {
    @Autowired
    private ZoneService zs;

    @Override
    public boolean isValid(PasswordValidator form, ConstraintValidatorContext constraintValidatorContext) {
        return form.getPassword().equals(form.getConfirmPassword());
    }
}