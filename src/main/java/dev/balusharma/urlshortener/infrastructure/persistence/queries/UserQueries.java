package dev.balusharma.urlshortener.infrastructure.persistence.queries;

public final  class UserQueries {

    private UserQueries() {}

    public static  final  String insertUser = """
            INSERT INTO "users"
            ("Email", "GoogleId", "Name", "CreatedAt")
            VALUES (?, ?, ?, ?)
            RETURNING "Id";
    """;

    public static final String selectUserByGoogleId = """
            SELECT "Id", "Email", "GoogleId", "Name", "CreatedAt"
            FROM "users"
            WHERE "GoogleId" = ?;
    """;
}
