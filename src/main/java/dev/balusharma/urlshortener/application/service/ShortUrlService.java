package dev.balusharma.urlshortener.application.service;

import dev.balusharma.urlshortener.application.exception.ShortCodeGenerationFailedException;
import dev.balusharma.urlshortener.application.exception.UrlAlreadyShortenedException;
import dev.balusharma.urlshortener.application.exception.UrlNotFoundException;
import dev.balusharma.urlshortener.infrastructure.persistence.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service()
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final CurrentUserProvider currentUserProvider;
    private final HashGenerator hashGenerator;
    private  final ShortCodeGenerator shortCodeGenerator;

    private static final int MAX_RETRIES = 3;

    public ShortUrlService(
            ShortUrlRepository shortUrlRepository,
            CurrentUserProvider currentUserProvider,
            HashGenerator hashGenerator,
            ShortCodeGenerator shortCodeGenerator
    ) {
        this.shortUrlRepository = shortUrlRepository;
        this.currentUserProvider = currentUserProvider;
        this.hashGenerator = hashGenerator;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    public String getLongUrlByShortUrl(
            String shortUrl
    ) {
        return this.shortUrlRepository
                .getLongUrlByShortUrl(shortUrl)
                .orElseThrow(() -> new UrlNotFoundException(shortUrl));
    }

    public String createShortUrl(
            String longUrl
    ) {
        String hashedLongUrl = this.hashGenerator.generateHash(longUrl);

        Optional<String> existingShortUrl = this.shortUrlRepository.getShortUrlByHashedLongUrl(hashedLongUrl);

        if(existingShortUrl.isPresent()) {
            throw new UrlAlreadyShortenedException(longUrl, existingShortUrl.get());
        }

        Integer userId = this.currentUserProvider.getCurrentUser().userId();

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
            catch (Exception ex) {
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
        return this.shortUrlRepository.updateLongUrl(
                    updatedLongUrl,
                    shortUrl,
                    userId
            );
    }
}
