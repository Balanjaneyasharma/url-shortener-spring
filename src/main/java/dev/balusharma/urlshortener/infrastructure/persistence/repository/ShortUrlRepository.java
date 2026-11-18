package dev.balusharma.urlshortener.infrastructure.persistence.repository;

import dev.balusharma.urlshortener.application.exception.DuplicateShortCodeException;
import dev.balusharma.urlshortener.infrastructure.persistence.constants.ShortUrlDbConstraints;
import dev.balusharma.urlshortener.infrastructure.persistence.queries.ShortUrlQueries;
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

    public ShortUrlRepository(
            NamedParameterJdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<String> getLongUrlByShortUrl(
            String shortUrl,
            Integer userId
    ) {
        String selectLongUrlSql = ShortUrlQueries.selectLongUrlByShortUrl;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("shortUrl", shortUrl)
                .addValue("userId", userId);
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
        String insertShortUrlSql = ShortUrlQueries.insertShortUrl;
        MapSqlParameterSource insertParameters = new MapSqlParameterSource()
                .addValue("longUrl", longUrl)
                .addValue("shortUrl", shortUrl)
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
            String hashedLongURl,
            String shortUrl,
            Integer userId
    ) {
        String updateLongUrlSql = ShortUrlQueries.updateLongUrl;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("longUrl", updatedLongUrl)
                .addValue("hashedLongUrl", hashedLongURl)
                .addValue("shortUrl", shortUrl)
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
            String hashedLongUrl,
            Integer userId
    ) {
        String selectShortUrlSql = ShortUrlQueries.selectShortUrlByHashedLongUrl;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("hashedLongUrl", hashedLongUrl)
                .addValue("userId", userId);
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
