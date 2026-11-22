ALTER TABLE "ShortUrlTable"
    ADD COLUMN "HashedLongUrl" text;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

UPDATE "ShortUrlTable"
SET "HashedLongUrl" = encode(digest("LongUrl", 'sha256'), 'hex')
WHERE "HashedLongUrl" IS NULL;

ALTER TABLE "ShortUrlTable"
    ALTER COLUMN "HashedLongUrl" SET NOT NULL;
