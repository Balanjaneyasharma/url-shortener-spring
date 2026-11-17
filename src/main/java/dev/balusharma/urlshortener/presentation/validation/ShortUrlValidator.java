package dev.balusharma.urlshortener.presentation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ShortUrlValidator implements ConstraintValidator<ValidShortUrl, String> {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;

    private static final Pattern SHORT_URL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_-]+$");

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {

        if (value == null || value.isBlank()) {
            return setError(context, "Short URL is required");
        }

        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            return setError(context,
                    "Short URL must be between 3 and 50 characters");
        }

        if (!SHORT_URL_PATTERN.matcher(value).matches()) {
            return setError(context,
                    "Short URL can only contain letters, numbers, hyphens, and underscores");
        }

        return true;
    }

    private boolean setError(
            ConstraintValidatorContext context,
            String message
    ) {
        context.disableDefaultConstraintViolation();

        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();

        return false;
    }
}