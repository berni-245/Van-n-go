package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.services.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidArrayZoneIdValidator implements ConstraintValidator<ValidArrayZoneId, List<Integer>> {
    @Autowired
    private ZoneService zs;

    @Override
    public boolean isValid(List<Integer> zones, ConstraintValidatorContext context) {
        if (zones == null) return true;
        return zones.stream().allMatch(zone -> zs.isValidZone(zone));
    }
}
