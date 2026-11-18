package dev.balusharma.urlshortener.presentation.controller;

import dev.balusharma.urlshortener.application.service.ShortUrlService;
import dev.balusharma.urlshortener.presentation.dto.request.CreateUrlShortRequest;
import dev.balusharma.urlshortener.presentation.dto.request.UpdateUrlRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/url-short")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    public ShortUrlController(
            ShortUrlService shortUrlService
    ) {
        this.shortUrlService = shortUrlService;
    }

    @PostMapping
    public ResponseEntity<String> createShortUrl(
          @Valid @RequestBody CreateUrlShortRequest shortUrlRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .body(shortUrlService.createShortUrl(shortUrlRequest.longUrl()));
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> getActualUrlByShortUrl(
            @PathVariable String shortUrl
    ) {
        String longUrl = this.shortUrlService.getLongUrlByShortUrl(shortUrl);
        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .location(URI.create(longUrl))
                .build();
    }

    @PutMapping
    public ResponseEntity<String> updateUrl(
            @Valid @RequestBody UpdateUrlRequest updateUrlRequest
    ) {
        String updatedUrl = shortUrlService.updateLongUrl(
                updateUrlRequest.longUrl(),
                updateUrlRequest.shortUrl()
        ).get();

        return ResponseEntity.ok(updatedUrl);
    }

}
