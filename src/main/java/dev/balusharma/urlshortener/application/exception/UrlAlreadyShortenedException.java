package dev.balusharma.urlshortener.application.exception;

import org.springframework.http.HttpStatus;

public class UrlAlreadyShortenedException extends ApplicationException {

    public UrlAlreadyShortenedException(String longUrl, String existingShortUrl) {
        String message = "The long URL '" + longUrl
                + "' already has a short URL assigned: '"
                + existingShortUrl + "'";
        super(
                HttpStatus.CONFLICT,
                message);
    }
}