# FileManager Service — Design

This document describes the target architecture after implementing the requirements in `requirements.md`. It covers component structure, class design, data model, and key decisions.

---

## Architecture Overview

```
HTTP Request
     │
     ▼
FileController          ← REST layer, HTTP concerns only
     │
     ▼
FileService             ← Business logic, validation, orchestration
     │         │
     ▼         ▼
FileRepository    StorageService (interface)
(metadata)             │              │
                       ▼              ▼
                 LocalStorage    S3Storage
                 (dev/test)      (production)
```

The controller handles HTTP in/out. The service owns all business logic. The repository owns metadata persistence. `StorageService` owns binary data I/O.

---

## Package Structure (target)

```
ninja.digitalcloud.cloud.filemanager/
├── controller/
│   ├── FileController.java
│   └── FileControllerAdvice.java
├── service/
│   ├── FileService.java
│   └── StorageService.java          ← interface
├── storage/
│   ├── LocalStorageService.java     ← dev profile
│   └── S3StorageService.java        ← prod profile
├── model/
│   └── File.java                    ← moved from repository/
├── repository/
│   ├── FileRepository.java
│   └── FileModelAssembler.java
├── exception/
│   ├── BadRequestException.java
│   └── FileNotFoundException.java
└── security/
    ├── CorsConfig.java
    └── SecurityConfig.java          ← new
```

The `File` entity moves from `repository/` to `model/` to separate domain from persistence concerns.

---

## Component Design

### FileController

- Depends on `FileService` only — no direct repository or storage access
- Remains responsible for: request mapping, multipart parsing, response wrapping with `EntityModel`/`PagedModel`, OpenAPI annotations
- Remove `@ResponseStatus` annotations where `ResponseEntity` is already returned (they are ignored and misleading)

### FileService

New class. Owns:
- Empty file check
- File name sanitization (strip path traversal characters)
- MIME type allowlist validation
- Coordinating save: write to `StorageService` first, then persist metadata to `FileRepository`
- Coordinating delete: delete from `FileRepository`, then delete from `StorageService`
- Retrieving file bytes via `StorageService` for download/view

```java
public class FileService {
    File upload(MultipartFile file);
    File findById(UUID id);
    byte[] getBytes(UUID id);
    Page<File> findAll(Pageable pageable);
    void delete(UUID id);
}
```

### StorageService

Interface with two implementations selected by Spring profile.

```java
public interface StorageService {
    String store(String filename, byte[] data);   // returns storage key
    byte[] retrieve(String storageKey);
    void delete(String storageKey);
}
```

- `LocalStorageService` — writes to a configurable local directory (`STORAGE_PATH`, default `./storage`). Storage key is the relative file path.
- `S3StorageService` — uses AWS SDK v2. Storage key is the S3 object key. Bucket and region are configurable via `STORAGE_S3_BUCKET` and `AWS_REGION`.

Profile activation:
- `local` profile → `LocalStorageService`
- `prod` profile → `S3StorageService`

### File (entity)

Moves to `model/` package. The `data` field (`byte[]`) is removed. A `storageKey` field is added.

```java
@Entity
@Table(schema = "FILEMANAGER", name = "FILES")
public class File extends RepresentationModel<File> {
    UUID id;
    String name;
    String contentType;       // maps to CONTENT_TYPE column
    Long size;
    String storageKey;        // maps to STORAGE_KEY column
    OffsetDateTime createdAt; // maps to CREATED_AT column
}
```

### SecurityConfig (new)

- Enables Spring Security (uncomment dependency in `build.gradle`)
- Configures stateless JWT bearer token authentication
- `GET` endpoints require `filemanager:read` scope
- `PUT` and `DELETE` endpoints require `filemanager:write` scope
- Actuator endpoints (`/filemanager/health`, `/filemanager/info`) remain public
- Delegates to an external authorization server (issuer URI configurable via `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI`)

### CorsConfig

- Replace hardcoded `*` with value injected from `CORS_ALLOWED_ORIGINS` environment variable
- Default to empty string (no origins allowed) if not set
- Parse as comma-separated list to support multiple origins

---

## Data Model

### Target Schema (Flyway migration V1_1)

```sql
ALTER TABLE filemanager.files
    RENAME COLUMN type TO content_type;

ALTER TABLE filemanager.files
    ADD COLUMN storage_key  VARCHAR(512),
    ADD COLUMN created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now();

ALTER TABLE filemanager.files
    DROP COLUMN data;
```

> Note: `data` column removal is a destructive migration. Any existing stored files will be lost. This is acceptable during initial development; a data migration strategy is required if applied to a live system.

### Final table shape

| Column         | Type                         | Notes                        |
|----------------|------------------------------|------------------------------|
| `id`           | `uuid`                       | PK, default `random_uuid()`  |
| `name`         | `varchar(255)`               |                              |
| `content_type` | `varchar(255)`               | renamed from `type`          |
| `size`         | `bigint`                     |                              |
| `storage_key`  | `varchar(512)`               | path or object key           |
| `created_at`   | `timestamp with time zone`   | default `now()`              |

---

## Configuration

New keys to add to `application.yml`:

```yaml
filemanager:
  storage:
    path: ${STORAGE_PATH:./storage}           # local profile
    s3-bucket: ${STORAGE_S3_BUCKET:}          # prod profile
  allowed-mime-types:
    - text/plain
    - text/csv
    - application/pdf
    - image/jpeg
    - image/png
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS:}

spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI:}
```

---

## Error Handling

No structural changes to `FileControllerAdvice`. Add one new exception:

- `StorageException extends RuntimeException` — thrown by `StorageService` implementations on I/O failure, mapped to `500 Internal Server Error`

---

## Testing Strategy

| Test class | Type | Tool |
|---|---|---|
| `FileControllerTest` | Unit | `MockMvc`, `@WebMvcTest` |
| `FileServiceTest` | Unit | JUnit 5, Mockito |
| `LocalStorageServiceTest` | Unit | JUnit 5, temp directory |
| `FileModelAssemblerTest` | Unit | JUnit 5 |
| `FileControllerAdviceTest` | Unit | `MockMvc` |
| `FileIntegrationTest` | Integration | `@SpringBootTest`, Testcontainers PostgreSQL |

Integration test covers the full lifecycle: upload → list → download → delete.

---

## Decisions & Trade-offs

| Decision | Rationale |
|---|---|
| `StorageService` interface over direct S3 SDK calls in service | Keeps local dev simple, makes storage backend swappable without changing business logic |
| Profile-based storage selection | Avoids runtime conditionals; Spring handles wiring |
| JWT resource server over API keys | Stateless, standard, composable with existing identity providers |
| Remove `data` column via migration | Keeping it alongside `storage_key` would be ambiguous; clean break is safer at this stage |
| `File` entity moved to `model/` | Separates domain model from persistence infrastructure; `repository/` should contain only Spring Data interfaces and assemblers |
