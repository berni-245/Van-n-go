package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidZoneIdValidator.class)
public @interface ValidZoneId {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "Must be a valid zone id";
}
