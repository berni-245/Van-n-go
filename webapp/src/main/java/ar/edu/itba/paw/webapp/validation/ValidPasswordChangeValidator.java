package ar.edu.itba.paw.webapp.validation;


import ar.edu.itba.paw.webapp.dto.user.UpdateUserPasswordDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidPasswordChangeValidator extends ValidatorWithLoggedUser implements ConstraintValidator<ValidPasswordChange, UpdateUserPasswordDTO> {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public boolean isValid(UpdateUserPasswordDTO dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null && dto.getOldPassword() == null) {
            return true;
        }

        if (dto.getPassword() == null || dto.getOldPassword() == null) {
            return false;
        }

        return passwordEncoder.matches(dto.getOldPassword(), getUser().getPassword());
    }
}
