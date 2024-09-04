package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ArrayAllMinValidator.class)
public @interface ArrayAllMin {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int value();

    String message() default "All elements must be greater than or equal to {value}";
}
