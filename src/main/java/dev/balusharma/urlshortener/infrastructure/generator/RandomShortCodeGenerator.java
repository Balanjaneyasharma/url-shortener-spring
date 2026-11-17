package dev.balusharma.urlshortener.infrastructure.generator;

import dev.balusharma.urlshortener.application.service.ShortCodeGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component()
public class RandomShortCodeGenerator implements ShortCodeGenerator {

    private static final String CHARS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int CODE_LENGTH = 8;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String generate() {

        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARS.length());
            code.append(CHARS.charAt(index));
        }

        return code.toString();
    }
}