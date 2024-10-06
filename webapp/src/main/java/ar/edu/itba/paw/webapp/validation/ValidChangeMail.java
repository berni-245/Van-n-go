package ar.edu.itba.paw.webapp.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidChangeMailValidator.class)
public @interface ValidChangeMail {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "Mail address already in use";
}
