package dev.balusharma.urlshortener.infrastructure.persistence.repository;

import dev.balusharma.urlshortener.domain.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> rowMapper = (rs, _) ->
            new User(
                    rs.getInt("Id"),
                    rs.getString("Email"),
                    rs.getString("GoogleId"),
                    rs.getString("Name"),
                    rs.getTimestamp("CreatedAt").toInstant()
            );

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findByGoogleId(String googleId) {
        return jdbcTemplate.query(
                """
                SELECT "Id", "Email", "GoogleId", "Name", "CreatedAt"
                FROM "users"
                WHERE "GoogleId" = ?
                """,
                rowMapper,
                googleId
        ).stream().findFirst();
    }

    public User create(
            String email,
            String googleId,
            String name
    ) {
        Instant now = Instant.now();

        Integer id = jdbcTemplate.queryForObject(
                """
                INSERT INTO "users"
                    ("Email", "GoogleId", "Name", "CreatedAt")
                VALUES (?, ?, ?, ?)
                RETURNING "Id"
                """,
                Integer.class,
                email,
                googleId,
                name,
                Timestamp.from(now)
        );

        return new User(id, email, googleId, name, now);
    }

    public User findOrCreate(
            String email,
            String googleId,
            String name
    ) {
        return findByGoogleId(googleId)
                .orElseGet(() -> create(email, googleId, name));
    }

    public Optional<User> findById(Integer id) {
        return jdbcTemplate.query(
                """
                SELECT "Id", "Email", "GoogleId", "Name", "CreatedAt"
                FROM "users"
                WHERE "Id" = ?
                """,
                rowMapper,
                id
        ).stream().findFirst();
    }
}