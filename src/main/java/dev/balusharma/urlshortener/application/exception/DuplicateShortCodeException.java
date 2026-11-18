package dev.balusharma.urlshortener.application.exception;

import org.springframework.http.HttpStatus;

public class DuplicateShortCodeException extends ApplicationException {

    public DuplicateShortCodeException(String code) {
        String message =  "Short code '" + code + "' already exists.";
        super(
                HttpStatus.CONFLICT,
                message
        );
    }
}