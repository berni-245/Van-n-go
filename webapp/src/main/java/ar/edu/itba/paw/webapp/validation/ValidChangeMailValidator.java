package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.ChangeUserInfoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidChangeMailValidator implements ConstraintValidator<ValidChangeMail, ChangeUserInfoForm> {
    @Qualifier("userServiceImpl")
    @Autowired
    private UserService us;

    @Override
    public boolean isValid(ChangeUserInfoForm form, ConstraintValidatorContext constraintValidatorContext) {
        String mail = form.getMail();
        String oldMail = form.getOldMail();
        if(mail.equals(oldMail)) return true;
        return  !us.mailExists(mail) ;
    }

}
