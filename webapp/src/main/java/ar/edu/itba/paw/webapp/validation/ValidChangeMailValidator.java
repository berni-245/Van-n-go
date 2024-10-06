package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidChangeMailValidator implements ConstraintValidator<ValidChangeMail, String> {
    @Qualifier("userServiceImpl")
    @Autowired
    private UserService us;

    @Override
    public boolean isValid(String mail, ConstraintValidatorContext constraintValidatorContext) {
        return mail == null || (!us.mailExists(mail) && mail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") );
    }

}
