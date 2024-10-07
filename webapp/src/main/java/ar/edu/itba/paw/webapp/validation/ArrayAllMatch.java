package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ArrayAllMatchValidator.class)
public @interface ArrayAllMatch {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String regexp();

    String message() default "All elements must match {regexp}";
}
