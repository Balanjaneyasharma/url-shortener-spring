package dev.balusharma.urlshortener.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ShortUrlValidator.class)
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.ANNOTATION_TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidShortUrl {

    String message() default "Invalid short URL";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}