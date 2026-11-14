package dev.balusharma.urlshortener.application.service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import dev.balusharma.urlshortener.infrastructure.configuration.JwtProperties;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

public class JwtService {
    private static final String NAME_IDENTIFIER =
            "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier";

    private static final String EMAIL =
            "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress";

    private static final String NAME =
            "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name";

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        byte[] secretBytes = jwtProperties.secret()
                .getBytes(StandardCharsets.UTF_8);

        if (secretBytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must contain at least 32 UTF-8 bytes for HS256."
            );
        }

        SecretKey secretKey = new SecretKeySpec(
                secretBytes,
                "HmacSHA256"
        );

        this.jwtEncoder = new NimbusJwtEncoder(
                new ImmutableSecret<SecurityContext>(secretKey)
        );
    }

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
