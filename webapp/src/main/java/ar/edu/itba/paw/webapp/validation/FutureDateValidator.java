package ar.edu.itba.paw.webapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.ZoneId;

public class FutureDateValidator implements ConstraintValidator<FutureDate, LocalDate> {
    private static final ZoneId ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        return date == null || date.isAfter(LocalDate.now(ZONE));
    }
}
