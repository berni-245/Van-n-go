package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.services.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidZoneIdValidator implements ConstraintValidator<ValidZoneId, Long> {
    @Autowired
    private ZoneService zs;

    @Override
    public boolean isValid(Long zoneId, ConstraintValidatorContext constraintValidatorContext) {
        return zoneId == null || zs.isValidZone(zoneId);
    }
}