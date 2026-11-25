
# URL Shortener - Spring Boot

A URL shortening REST API built using Java and Spring Boot, featuring Google OAuth2 authentication, JWT-based authorization, PostgreSQL persistence, Redis caching, and asynchronous click-count processing.

## Features

### URL Management
- Generate unique 8-character short URLs.
- Generate SHA-256 hashes for long URLs.
- Prevent duplicate URL creation for the same user.
- Update existing long URLs.
- Redirect short URLs to their original destinations.
- Validate long URLs and custom short codes.

### Authentication & Authorization
- Google OAuth2 / OpenID Connect authentication.
- JWT-based access token authentication.
- Secure refresh token generation and rotation.
- Hashed refresh token storage in PostgreSQL.
- Refresh token revocation during logout.
- User-specific URL management.

### Caching & Performance
- Redis-backed short URL caching.
- Configurable cache expiration.
- Redis-based click-count tracking.
- Asynchronous click-count processing.
- Batch-based click-count persistence.
- Idempotent batch processing to prevent duplicate database updates.

### Background Processing
- Scheduled click-count processing.
- Atomic Redis batch claiming using Lua scripts.
- Transactional PostgreSQL batch processing.
- Scheduled cleanup of processed batch records.

### Database
- PostgreSQL persistence using Spring JDBC.
- Flyway database migrations.
- SHA-256-based URL deduplication.
- Indexed user and refresh-token lookups.

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.1.2 |
| Web | Spring Web MVC |
| Security | Spring Security, OAuth2, JWT |
| Database | PostgreSQL |
| Data Access | Spring JDBC |
| Migrations | Flyway |
| Cache | Redis 7 |
| Infrastructure | Docker Compose |
| Build Tool | Maven |
| Testing | JUnit 5, Spring Boot Test |

## Architecture

The application follows a layered architecture.

```text
dev.balusharma.urlshortener
│
├── application
│   ├── context
│   ├── dto
│   ├── exception
│   └── service
│
├── domain
│   └── model
│
├── infrastructure
│   ├── cache
│   │   ├── repository
│   │   └── scripts
│   ├── configuration
│   ├── generator
│   ├── model
│   ├── persistence
│   │   ├── constants
│   │   ├── queries
│   │   └── repository
│   ├── security
│   └── worker
│
└── presentation
    ├── controller
    ├── dto
    ├── exception
    └── validation
```

### Layer Responsibilities

- **Presentation:** REST controllers, request/response DTOs, validation, and exception handling.
- **Application:** Application services, use-case orchestration, and application-level exceptions.
- **Domain:** Core domain models.
- **Infrastructure:** Database access, Redis integration, security configuration, token handling, generators, and background workers.

## Application Workflows

### 1. Short URL Creation

1. Receive a long URL through the REST API.
2. Validate the URL.
3. Generate its SHA-256 hash.
4. Check whether the URL already exists for the authenticated user.
5. Generate a random 8-character short code.
6. Persist the URL and associated user information in PostgreSQL.
7. Return the generated short code.

### 2. Short URL Redirection

1. Receive a short URL through the redirect endpoint.
2. Check Redis for the corresponding long URL.
3. If the URL is not cached, retrieve it from PostgreSQL and populate Redis.
4. Increment its click count in Redis.
5. Return an HTTP 302 redirect to the original URL.

### 3. Click Count Processing

Click counts are maintained in Redis and periodically persisted to PostgreSQL.

1. The redirect flow increments click counts in an active Redis hash.
2. A scheduled worker periodically claims the accumulated counts.
3. Lua scripts perform atomic batch operations in Redis.
4. The processing service passes the batch to the persistence layer.
5. PostgreSQL records the batch ID to ensure idempotent processing.
6. URL click counts are updated transactionally.
7. Successfully processed batches are acknowledged and removed from Redis.
8. Old batch records are periodically cleaned up.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/{shortUrl}` | Redirect to original URL |
| POST | `/api/url-short` | Create a short URL |
| PUT | `/api/url-short` | Update an existing short URL |
| POST | `/auth/refresh` | Refresh access and refresh tokens |
| POST | `/auth/logout` | Revoke refresh token |
| GET | `/oauth2/authorization/google` | Initiate Google OAuth2 login |

Protected endpoints require a valid JWT bearer token.

### Create Short URL

**POST** `/api/url-short`

Request:

```json
{
  "longUrl": "https://example.com/some/long/path"
}
```

### Update Short URL

**PUT** `/api/url-short`

Request:

```json
{
  "shortUrl": "AbCd1234",
  "longUrl": "https://example.com/updated-path"
}
```

### Refresh Token

**POST** `/auth/refresh`

Request:

```json
{
  "refreshToken": "<refresh-token>"
}
```

### Logout

**POST** `/auth/logout`

Request:

```json
{
  "refreshToken": "<refresh-token>"
}
```

## Prerequisites

Make sure the following are installed:

- Java 25
- Maven
- PostgreSQL
- Docker Desktop with Docker Compose

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Balanjaneyasharma/url-shortener-spring.git

cd url-shortener-spring
```

### 2. Start Redis

Redis is configured through Docker Compose.

```bash
docker compose up -d redis
```

Verify the Redis container:

```bash
docker exec -it url-shortener-redis redis-cli ping
```

Expected response:

```text
PONG
```

### 3. Configure Environment Variables

Configure the following environment variables before starting the application.

| Variable | Description |
|---|---|
| `DB_URL` | PostgreSQL JDBC connection URL |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `GOOGLE_CLIENT_ID` | Google OAuth2 client ID |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret |
| `JWT_SECRET` | Secret used for signing JWTs (minimum 32 characters) |
| `JWT_ISSUER` | JWT issuer |
| `JWT_AUDIENCE` | JWT audience |
| `JWT_ACCESS_TOKEN_EXPIRY_MINUTES` | Access token expiration in minutes |
| `JWT_REFRESH_TOKEN_EXPIRY_DAYS` | Refresh token expiration in days |

The Google OAuth2 callback URI is:

```text
/oauth/google/callback
```

Configure the corresponding redirect URI in your Google OAuth client.

### 4. Database Configuration

The application uses PostgreSQL and Flyway for database schema management.

Database migrations are located at:

```text
src/main/resources/db/migration
```

The migrations create the initial URL schema and progressively introduce URL hashing, user authentication, click counts, and batch tracking.

For an existing database whose schema already corresponds to migration V4, the repository includes a `baseline` profile to establish the Flyway baseline before applying subsequent migrations.

Use this profile only when initializing an existing database with the corresponding schema. Fresh databases should use the normal Flyway migration flow.

### 5. Run the Application

Start the Spring Boot application using Maven:

```bash
mvn spring-boot:run
```

The application starts using the configured database, Redis, OAuth2, and JWT settings.

## Running Tests

Execute the test suite using:

```bash
mvn test
```

## Redis Configuration

Redis connection settings are configured in `application.yml`.

The current configuration uses:

- Host: `localhost`
- Port: `6379`
- Connection timeout: `2 seconds`

The short URL cache has a configurable TTL, currently set to one hour.

The click-count worker runs every 10 seconds by default, with processed batch records retained for seven days.

## Project Repository

[GitHub - url-shortener-spring](https://github.com/Balanjaneyasharma/url-shortener-spring)
