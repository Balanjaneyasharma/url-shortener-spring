package dev.balusharma.urlshortener.application.service;

import dev.balusharma.urlshortener.infrastructure.configuration.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Service()
public class JwtService {

    private static final String NAME_IDENTIFIER =
            "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier";

    private static final String EMAIL =
            "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress";

    private static final String NAME =
            "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name";

    @Autowired
    private JwtEncoder jwtEncoder;
    @Autowired
    private JwtProperties jwtProperties;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateAccessToken(
            int userId,
            String email,
            String name
    ) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .audience(List.of(jwtProperties.audience()))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(
                        jwtProperties.accessTokenExpiryMinutes() * 60
                ))
                .claim(NAME_IDENTIFIER, String.valueOf(userId))
                .claim(EMAIL, email)
                .claim(NAME, name == null ? "" : name)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    public String generateRefreshToken() {
        byte[] randomBytes = new byte[64];

        secureRandom.nextBytes(randomBytes);

        return Base64.getEncoder()
                .encodeToString(randomBytes);
    }

    public String hashToken(String token) {
        try {
            byte[] hash = MessageDigest
                    .getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder()
                    .encodeToString(hash);

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to hash refresh token.",
                    exception
            );
        }
    }
}
