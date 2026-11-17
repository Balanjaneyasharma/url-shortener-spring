package dev.balusharma.urlshortener.application.exception;

public class UrlAlreadyShortenedException extends RuntimeException {

    public UrlAlreadyShortenedException(String longUrl, String existingShortUrl) {
        super("The long URL '" + longUrl
                + "' already has a short URL assigned: '"
                + existingShortUrl + "'");
    }
}