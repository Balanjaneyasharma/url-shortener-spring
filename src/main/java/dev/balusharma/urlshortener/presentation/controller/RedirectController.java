package dev.balusharma.urlshortener.presentation.controller;

import dev.balusharma.urlshortener.application.service.ShortUrlService;
import dev.balusharma.urlshortener.presentation.validation.ValidShortUrl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@Validated
public class RedirectController {

    private final ShortUrlService shortUrlService;

    public RedirectController(
            ShortUrlService shortUrlService
    ) {
        this.shortUrlService =shortUrlService;
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(
            @PathVariable @ValidShortUrl String shortUrl) {

        String longUrl = shortUrlService.getLongUrlByShortUrl(shortUrl);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }
}