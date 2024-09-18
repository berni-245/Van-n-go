package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidPlateNumberValidator implements ConstraintValidator<ValidPlateNumber, String> {
    @Autowired
    private VehicleService vs;

    @Override
    public boolean isValid(String plateNumber, ConstraintValidatorContext constraintValidatorContext) {
        return plateNumber != null && !vs.plateNumberExists(plateNumber);
    }
}