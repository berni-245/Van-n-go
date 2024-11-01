package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.services.VehicleService;
import ar.edu.itba.paw.webapp.form.VehicleForm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidPlateNumberValidator implements ConstraintValidator<ValidPlateNumber, VehicleForm> {
    @Autowired
    private VehicleService vs;

    @Override
    public boolean isValid(VehicleForm form, ConstraintValidatorContext constraintValidatorContext) {
        String newPN = form.getPlateNumber();
        if (newPN == null) return false;
        String ogPN = form.getOgPlateNumber();
        boolean isValid = false;
        if (ogPN != null) isValid = ogPN.equals(newPN);
        if (!isValid) isValid = !vs.plateNumberExists(newPN);
        return isValid;
    }
}