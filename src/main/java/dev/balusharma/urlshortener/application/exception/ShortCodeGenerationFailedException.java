package dev.balusharma.urlshortener.application.exception;

import org.springframework.http.HttpStatus;

public class ShortCodeGenerationFailedException extends ApplicationException {

    public ShortCodeGenerationFailedException(String message) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                message
        );
    }
}