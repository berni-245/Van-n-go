package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidChangeMailValidator extends ValidatorWithLoggedUser implements ConstraintValidator<ValidChangeMail, String> {
    // TODO: Ver si esto está bien. Es feo, pero `mailExists` está definida en directo en `UserJpaDao`
    // y se fija en toda la tabla de User de una. Quizá convenga separarlo en Client y Driver como
    // con username y listo...
    @Qualifier("clientServiceImpl")
    @Autowired
    private UserService<User> us;

    @Override
    public boolean isValid(String mail, ConstraintValidatorContext constraintValidatorContext) {
        if (getUser().getMail().equals(mail)) return true;
        return !us.mailExists(mail);
    }

}
