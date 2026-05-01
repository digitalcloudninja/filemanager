# FileManager Service — Requirements

These requirements address known gaps in the current implementation. Reference this file when implementing fixes or new features.

---

## REQ-01 — External File Storage Backend

**Problem:** File bytes are stored as `oid` blobs in PostgreSQL, which doesn't scale and bloats the database.

1. File binary data shall be stored in an external object storage backend (S3-compatible minimum)
2. The database shall store only file metadata: `id`, `name`, `content_type`, `size`, `storage_key`, `created_at`
3. A `StorageService` interface shall abstract the backend so implementations can be swapped (local filesystem for dev, S3 for prod)
4. A local filesystem implementation shall be provided for development and testing
5. The `storage_key` (the path/key in the backend) shall be stored in the database and used to retrieve or delete the file
6. Deleting a file record shall also delete the corresponding object from storage

---

## REQ-02 — Authentication & Authorization

**Problem:** No authentication exists; any caller can upload, download, or delete files.

1. All API endpoints shall require a valid bearer token (JWT)
2. Spring Security shall be enabled and configured
3. Token validation shall be stateless (no server-side session)
4. A read scope shall be required for `GET` endpoints
5. A write scope shall be required for `PUT` and `DELETE` endpoints
6. Unauthenticated requests shall return `401 Unauthorized` with a `ProblemDetail` body
7. Insufficient scope shall return `403 Forbidden` with a `ProblemDetail` body

---

## REQ-03 — CORS Hardening

**Problem:** CORS is configured to allow all origins (`*`), which is unsafe.

1. Allowed origins shall be configurable via environment variable (`CORS_ALLOWED_ORIGINS`)
2. The default value shall be empty (deny all cross-origin requests) unless explicitly set
3. Wildcard `*` shall not be permitted when credentials are involved

---

## REQ-04 — Service Layer

**Problem:** Business logic lives in the controller, making it hard to extend and test.

1. A `FileService` class shall be introduced between the controller and repository
2. The controller shall delegate all business logic to `FileService`
3. File validation (empty check, size, content type allowlist) shall live in `FileService`
4. Storage coordination (save metadata + write to storage backend) shall be handled in `FileService` as a single logical operation

---

## REQ-05 — Input Validation

**Problem:** File type and content are not validated beyond checking for an empty file.

1. Uploaded files shall be validated against a configurable allowlist of permitted MIME types
2. Files exceeding the configured size limit shall be rejected with `400 Bad Request` before being read into memory
3. File names shall be sanitized to prevent path traversal (e.g., stripping `../`)
4. Rejected uploads shall return a `ProblemDetail` response with a clear reason

---

## REQ-06 — Database Schema Fixes

**Problem:** The Flyway DDL and JPA column mapping are inconsistent; `created_at` is missing.

1. A new Flyway migration shall align the schema with the JPA entity mappings
2. The `type` column shall be renamed to `content_type`
3. A `storage_key` column (`varchar(512)`) shall be added
4. A `created_at` column (`timestamp with time zone`, non-null, default `now()`) shall be added
5. The `data` (`oid`) column shall be removed once REQ-01 is implemented

---

## REQ-07 — Test Coverage

**Problem:** The test suite has no assertions and provides zero coverage.

1. `FileController` shall have unit tests covering all endpoints using `MockMvc`
2. Happy path and error path (400, 404, 500) shall each have at least one test
3. `FileModelAssembler` shall have a unit test verifying HATEOAS links are correctly set
4. `FileControllerAdvice` shall have unit tests for each exception handler
5. An integration test shall verify the full upload → list → download → delete lifecycle against a test database (Testcontainers PostgreSQL)

---

## REQ-08 — Configuration Cleanup

**Problem:** Commented-out dependencies and mismatched port in Dockerfile cause confusion and potential startup failures.

1. `bootstrap.yml` shall set `DISCOVERY_ENABLED` default to `false` since the Eureka dependency is not active
2. The `Dockerfile` `EXPOSE` directive shall match the configured server port (`8082`)
3. Commented-out dependencies in `build.gradle` shall either be removed or documented with a note explaining when they would be re-enabled
