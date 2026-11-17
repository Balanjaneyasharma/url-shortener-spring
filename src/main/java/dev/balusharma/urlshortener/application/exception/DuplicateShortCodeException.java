package dev.balusharma.urlshortener.application.exception;

public class DuplicateShortCodeException extends RuntimeException {

    public DuplicateShortCodeException(String code) {
        super("Short code '" + code + "' already exists.");
    }
}