package dev.balusharma.urlshortener.application.exception;

public class ShortCodeGenerationFailedException extends RuntimeException {

    public ShortCodeGenerationFailedException(String message) {
        super(message);
    }
}