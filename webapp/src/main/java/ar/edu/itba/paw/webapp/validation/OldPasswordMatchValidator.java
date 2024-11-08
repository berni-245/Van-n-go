package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.ChangePasswordForm;
import ar.edu.itba.paw.webapp.form.ChangeUserInfoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class OldPasswordMatchValidator implements ConstraintValidator<OldPasswordMatch, ChangePasswordForm> {

    @Autowired
    private ClientService cs;
    @Autowired
    private DriverService ds;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean isValid(ChangePasswordForm form, ConstraintValidatorContext context) {
        Optional<? extends User> user;
        if(form.isDriver()) user = ds.findById(form.getUserId());
        else user = cs.findById(form.getUserId());
        return user.filter(value -> passwordEncoder.matches(form.getOldPassword(), value.getPassword())).isPresent();
    }
}
