package ar.edu.itba.paw.webapp.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidPasswordChangeValidator.class)
public @interface ValidPasswordChange {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "{ValidPasswordChange}";
}
