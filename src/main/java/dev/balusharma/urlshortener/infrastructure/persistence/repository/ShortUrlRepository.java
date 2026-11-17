package dev.balusharma.urlshortener.infrastructure.persistence.repository;

import dev.balusharma.urlshortener.application.exception.DuplicateShortCodeException;
import dev.balusharma.urlshortener.infrastructure.persistence.constants.ShortUrlDbConstraints;
import dev.balusharma.urlshortener.infrastructure.persistence.sql.SqlFileLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.postgresql.util.PSQLException;

import java.util.Optional;

@Repository()
public class ShortUrlRepository {

    private final NamedParameterJdbcTemplate  jdbcTemplate;
    private final SqlFileLoader sqlFileLoader;

    public ShortUrlRepository(
            NamedParameterJdbcTemplate jdbcTemplate,
            SqlFileLoader sqlFileLoader
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlFileLoader = sqlFileLoader;
    }

    public Optional<String> getLongUrlByShortUrl(
            String shortUrl
    ) {
        String selectLongUrlSql = this.sqlFileLoader.load(
                "sql/queries/short-url/select-long-url.sql"
        );
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("short-url", shortUrl);
        try {
            String longUrl = this.jdbcTemplate.queryForObject(
                    selectLongUrlSql,
                    parameters,
                    String.class
            );
            return Optional.ofNullable(longUrl);
        }
        catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }

    }

    public Optional<String> createShortUrl(
            String longUrl,
            String shortUrl,
            String hashedLongUrl,
            Integer userId
    ) {
        String insertShortUrlSql = this.sqlFileLoader.load(
                "sql/queries/short-url/insert-short-url.sql"
        );
        MapSqlParameterSource insertParameters = new MapSqlParameterSource()
                .addValue("longUrl", longUrl)
                .addValue("short-url", shortUrl)
                .addValue("hashedLongUrl", hashedLongUrl)
                .addValue("userId", userId);

        try {
            jdbcTemplate.update(
                    insertShortUrlSql,
                    insertParameters
            );

            return Optional.of(shortUrl);
        }
        catch (DataAccessException ex) {
            if (ex.getCause() instanceof PSQLException pgEx
                    && "23505".equals(pgEx.getSQLState())
                    && ShortUrlDbConstraints.DUPLICATE_SHORT_CODE.equals(
                    pgEx.getServerErrorMessage().getConstraint())) {

                throw new DuplicateShortCodeException(shortUrl);
            }

            throw ex;

        }

    }

    public Optional<String> updateLongUrl(
            String updatedLongUrl,
            String shortUrl,
            Integer userId
    ) {
        String updateLongUrlSql = this.sqlFileLoader.load(
                "sql/queries/short-url/update-long-url.sql"
        );
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("updatedLongUrl", updatedLongUrl)
                .addValue("short-url", shortUrl)
                .addValue("userId", userId);
        try {
            this.jdbcTemplate.update(
                    updateLongUrlSql,
                    parameters
            );
            return Optional.ofNullable(shortUrl);
        }
        catch(Exception ex) {
            return Optional.of("");
        }

    }

    public Optional<String> getShortUrlByHashedLongUrl(
            String hashedLongUrl
    ) {
        String selectShortUrlSql = this.sqlFileLoader.load(
                "sql/queries/short-url/select-short-url.sql"
        );
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("hashedLongUrl", hashedLongUrl);
        try {
            String shortUrl = this.jdbcTemplate.queryForObject(
                    selectShortUrlSql,
                    parameters,
                    String.class
            );
            return Optional.ofNullable(shortUrl);
        }
        catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

}
