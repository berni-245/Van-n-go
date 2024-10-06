package ar.edu.itba.paw.webapp.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidChangeUsernameValidator.class)
public @interface ValidChangeUsername {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "Username already in use";
}
