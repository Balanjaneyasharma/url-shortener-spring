package dev.balusharma.urlshortener.infrastructure.persistence.repository;

import dev.balusharma.urlshortener.domain.model.User;
import dev.balusharma.urlshortener.infrastructure.persistence.queries.UserQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<User> rowMapper = (rs, _) ->
            new User(
                    rs.getInt("Id"),
                    rs.getString("Email"),
                    rs.getString("GoogleId"),
                    rs.getString("Name"),
                    rs.getTimestamp("CreatedAt").toInstant()
            );

    public Optional<User> findByGoogleId(String googleId) {
        return jdbcTemplate.query(
                UserQueries.selectUserByGoogleId,
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
                UserQueries.insertUser,
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