package dev.balusharma.urlshortener.infrastructure.persistence.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Repository
public class RefreshTokenRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void create(
            Integer userId,
            String hashedToken,
            Instant expiresAt
    ) {
        jdbcTemplate.update(
                """
                INSERT INTO "refresh_tokens"
                    ("UserId", "HashedToken", "ExpiresAt",
                     "IsRevoked", "CreatedAt")
                VALUES (?, ?, ?, false, ?)
                """,
                userId,
                hashedToken,
                Timestamp.from(expiresAt),
                Timestamp.from(Instant.now())
        );
    }

    public Optional<Integer> consume(String hashedToken) {

        // A single conditional UPDATE ensures only one request
        // can successfully consume a refresh token.
        return jdbcTemplate.query(
                """
                UPDATE "refresh_tokens"
                SET "IsRevoked" = true
                WHERE "HashedToken" = ?
                  AND "IsRevoked" = false
                  AND "ExpiresAt" > CURRENT_TIMESTAMP
                RETURNING "UserId"
                """,
                (rs, _) -> rs.getInt("UserId"),
                hashedToken
        ).stream().findFirst();
    }

    public void revoke(String hashedToken) {
        jdbcTemplate.update(
                """
                UPDATE "refresh_tokens"
                SET "IsRevoked" = true
                WHERE "HashedToken" = ?
                  AND "IsRevoked" = false
                """,
                hashedToken
        );
    }

    public void revokeAllForUser(Integer userId) {
        jdbcTemplate.update(
                """
                UPDATE "refresh_tokens"
                SET "IsRevoked" = true
                WHERE "UserId" = ?
                  AND "IsRevoked" = false
                """,
                userId
        );
    }
}