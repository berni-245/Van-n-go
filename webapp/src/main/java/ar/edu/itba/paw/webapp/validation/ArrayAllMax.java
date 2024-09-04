package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ArrayAllMaxValidator.class)
public @interface ArrayAllMax {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int value();

    String message() default "All elements must be less than or equal to {value}";
}
