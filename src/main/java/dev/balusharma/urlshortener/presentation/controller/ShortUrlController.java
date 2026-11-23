package dev.balusharma.urlshortener.presentation.controller;

import dev.balusharma.urlshortener.application.service.ShortUrlService;
import dev.balusharma.urlshortener.presentation.dto.request.CreateUrlShortRequest;
import dev.balusharma.urlshortener.presentation.dto.request.UpdateUrlRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/url-short")
public class ShortUrlController {

    @Autowired
    private ShortUrlService shortUrlService;

    @PostMapping
    public ResponseEntity<String> createShortUrl(
          @Valid @RequestBody CreateUrlShortRequest shortUrlRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(shortUrlService.createShortUrl(shortUrlRequest.longUrl()));
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
