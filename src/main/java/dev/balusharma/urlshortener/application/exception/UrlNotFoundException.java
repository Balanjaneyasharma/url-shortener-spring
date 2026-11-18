package dev.balusharma.urlshortener.application.exception;

import org.springframework.http.HttpStatus;

public class UrlNotFoundException extends ApplicationException {

    public UrlNotFoundException(String shortUrl) {
        super(
                HttpStatus.NOT_FOUND,
                "Short URL not found: " + shortUrl
        );
    }
}
