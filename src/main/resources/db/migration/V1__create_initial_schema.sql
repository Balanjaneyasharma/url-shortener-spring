-- Reconstructs the original URL table that predates the EF migrations
-- currently present in the .NET repository.

CREATE SEQUENCE "ShortUrlTable_Id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE "ShortUrlTable"
(
    "Id" integer NOT NULL DEFAULT nextval('"ShortUrlTable_Id_seq"'::regclass),
    "CreatedAt" timestamp without time zone NOT NULL DEFAULT now(),
    "LongUrl" text NOT NULL,
    "ShortUrl" character varying NOT NULL,
    "UpdatedAt" timestamp without time zone NOT NULL DEFAULT now(),
    CONSTRAINT "ShortUrlTable_pkey" PRIMARY KEY ("Id"),
    CONSTRAINT "ShortUrlTable_ShortUrl_key" UNIQUE ("ShortUrl")
);

ALTER SEQUENCE "ShortUrlTable_Id_seq"
    OWNED BY "ShortUrlTable"."Id";
