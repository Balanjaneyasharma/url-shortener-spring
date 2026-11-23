package dev.balusharma.urlshortener.application.service;

import dev.balusharma.urlshortener.application.exception.DuplicateShortCodeException;
import dev.balusharma.urlshortener.application.exception.ShortCodeGenerationFailedException;
import dev.balusharma.urlshortener.application.exception.UrlAlreadyShortenedException;
import dev.balusharma.urlshortener.application.exception.UrlNotFoundException;
import dev.balusharma.urlshortener.infrastructure.cache.repository.ClickCountCacheRepository;
import dev.balusharma.urlshortener.infrastructure.cache.repository.ShortUrlCacheRepository;
import dev.balusharma.urlshortener.infrastructure.persistence.repository.ShortUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service()
public class ShortUrlService {

    @Autowired
    private ShortUrlRepository shortUrlRepository;
    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private HashGenerator hashGenerator;
    @Autowired
    private ShortCodeGenerator shortCodeGenerator;
    @Autowired
    private ShortUrlCacheRepository shortUrlCacheRepository;
    @Autowired
    private ClickCountCacheRepository clickCountCacheRepository;

    private static final int MAX_RETRIES = 3;

    public String getLongUrlByShortUrl(
            String shortUrl
    ) {
        String longUrl = shortUrlCacheRepository
                .getLongUrl(shortUrl)
                .orElseGet(() -> {
                    String url = shortUrlRepository
                            .getLongUrlByShortUrl(shortUrl)
                            .orElseThrow(() -> new UrlNotFoundException(shortUrl));

                    shortUrlCacheRepository.save(shortUrl, url);

                    return url;
                });

        clickCountCacheRepository.increment(shortUrl);

        return longUrl;
    }

    public String createShortUrl(
            String longUrl
    ) {
        String hashedLongUrl = this.hashGenerator.generateHash(longUrl);
        Integer userId = this.currentUserProvider.getCurrentUser().userId();
        Optional<String> existingShortUrl = this.shortUrlRepository.getShortUrlByHashedLongUrl(hashedLongUrl, userId);

        if(existingShortUrl.isPresent()) {
            throw new UrlAlreadyShortenedException(longUrl, existingShortUrl.get());
        }

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {

            String shortUrl = shortCodeGenerator.generate();

            try {
                 return this.shortUrlRepository.createShortUrl(
                        longUrl,
                        shortUrl,
                        hashedLongUrl,
                        userId
                ).get();
            }
            catch (DuplicateShortCodeException ex) {
                if (attempt == MAX_RETRIES) {
                    throw new ShortCodeGenerationFailedException(
                            "Failed after " + MAX_RETRIES + " attempts"
                    );
                }
            }
        }

        throw new ShortCodeGenerationFailedException("Unexpected failure");
    }

    public Optional<String> updateLongUrl(
            String updatedLongUrl,
            String shortUrl
    ) {
        Integer userId = this.currentUserProvider.getCurrentUser().userId();
        String hashedLongURl = this.hashGenerator.generateHash(updatedLongUrl);
        Integer result = this.shortUrlRepository.updateLongUrl(
                    updatedLongUrl,
                    hashedLongURl,
                    shortUrl,
                    userId
            ).get();

        if(result == 0) {
            throw new UrlNotFoundException(shortUrl);
        }

        return Optional.of(shortUrl);
    }
}
