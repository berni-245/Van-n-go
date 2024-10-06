package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidImageValidator.class)
public @interface ValidImage {
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String message() default "Image must be under 10MBs";
}
