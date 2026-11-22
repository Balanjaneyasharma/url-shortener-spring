ALTER TABLE "ShortUrlTable"
    ADD COLUMN "UserId" integer NOT NULL DEFAULT 0;

ALTER TABLE "ShortUrlTable"
    ADD CONSTRAINT "FK_ShortUrlTable_users_UserId"
    FOREIGN KEY ("UserId") REFERENCES users ("Id") ON DELETE CASCADE;

CREATE INDEX "IX_ShortUrlTable_UserId"
    ON "ShortUrlTable" ("UserId");
