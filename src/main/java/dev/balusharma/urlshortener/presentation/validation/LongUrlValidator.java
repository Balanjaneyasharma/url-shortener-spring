package dev.balusharma.urlshortener.presentation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.InetAddress;
import java.net.URI;

public class LongUrlValidator implements ConstraintValidator<ValidLongUrl, String> {

    private static final int MAX_LENGTH = 2000;

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {

        if (value == null || value.isBlank()) {
            return setError(context, "URL is required");
        }

        String url = value.trim();

        if (url.length() > MAX_LENGTH) {
            return setError(context,
                    "URL must not exceed 2000 characters");
        }

        URI uri;

        try {
            uri = new URI(url);
        } catch (Exception ex) {
            return setError(context, "Invalid URL format");
        }

        if (!uri.isAbsolute() || uri.getHost() == null) {
            return setError(context, "Invalid URL format");
        }

        String scheme = uri.getScheme();

        if (!"http".equalsIgnoreCase(scheme)
                && !"https".equalsIgnoreCase(scheme)) {
            return setError(context,
                    "Only HTTP and HTTPS URLs are allowed");
        }

        String host = uri.getHost();

        if ("localhost".equalsIgnoreCase(host)) {
            return setError(context,
                    "Localhost URLs are not allowed");
        }

        try {
            if (isPrivateIp(InetAddress.getByName(host))) {
                return setError(context,
                        "Private or internal IPs are not allowed");
            }
        } catch (Exception ex) {
            // A hostname that cannot be resolved is not accepted.
            return setError(context, "Invalid hostname");
        }

        return true;
    }

    private boolean isPrivateIp(InetAddress ip) {
        return ip.isAnyLocalAddress()
                || ip.isLoopbackAddress()
                || ip.isLinkLocalAddress()
                || ip.isSiteLocalAddress()
                || ip.isMulticastAddress();
    }

    private boolean setError(
            ConstraintValidatorContext context,
            String message) {

        context.disableDefaultConstraintViolation();

        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();

        return false;
    }
}