package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidChangeMailValidator extends ValidatorWithLoggedUser implements ConstraintValidator<ValidChangeMail, String> {
    @Autowired
    private ClientService cs;
    @Autowired
    private DriverService ds;

    @Override
    public boolean isValid(String mail, ConstraintValidatorContext constraintValidatorContext) {
        if (getUser().getMail().equals(mail)) return true;
        return !(cs.mailExists(mail) || ds.mailExists(mail));
    }

}
