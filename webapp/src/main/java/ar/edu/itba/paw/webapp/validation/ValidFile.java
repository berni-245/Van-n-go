package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidFileValidator.class)
public @interface ValidFile {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "{ValidFile}";
}

