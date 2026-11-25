package dev.balusharma.urlshortener.infrastructure.persistence.queries;

public final class ClicksCountBatchQueries {

    private ClicksCountBatchQueries() {}

    public static final String insertBatch = """
            INSERT INTO "ClickCountBatch" (
                        "BatchId"
            )
            VALUES (:batchId)
            ON CONFLICT ("BatchId") DO NOTHING
    """;
}
