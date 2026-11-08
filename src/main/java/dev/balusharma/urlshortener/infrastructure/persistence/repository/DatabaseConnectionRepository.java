package dev.balusharma.urlshortener.infrastructure.persistence.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DatabaseConnectionRepository {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseConnectionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer testConnection() {
        return jdbcTemplate.queryForObject("SELECT 1", Integer.class);
    }
}