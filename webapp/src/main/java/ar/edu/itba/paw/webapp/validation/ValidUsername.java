package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidUsernameValidator.class)
public @interface ValidUsername {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "Username already in use";
}
