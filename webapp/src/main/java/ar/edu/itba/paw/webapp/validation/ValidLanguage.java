package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidLanguageValidator.class)
public @interface ValidLanguage {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "Language not supported";
}