package dev.balusharma.urlshortener.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LongUrlValidator.class)
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.ANNOTATION_TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLongUrl {

    String message() default "Invalid URL format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
