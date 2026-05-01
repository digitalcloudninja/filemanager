# FileManager Service — Tasks

Implementation tasks derived from `requirements.md` and `design.md`. Work through them in order — later tasks depend on earlier ones.

---

## Phase 1 — Configuration Cleanup (REQ-08)

- [ ] 1. Fix `bootstrap.yml` — change `DISCOVERY_ENABLED` default from `true` to `false`
- [ ] 2. Fix `Dockerfile` — change `EXPOSE 8080` to `EXPOSE 8082` to match server port
- [ ] 3. Clean `build.gradle` — remove commented-out dependencies that are not planned for reactivation; add inline comments on any that are intentionally deferred (e.g. Spring Security, Spring Cloud)

---

## Phase 2 — Database Schema (REQ-06)

- [ ] 4. Create Flyway migration `V1_1__schema_fixes.sql`:
  - [ ] 4.1 Rename column `type` → `content_type`
  - [ ] 4.2 Add column `storage_key VARCHAR(512)`
  - [ ] 4.3 Add column `created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()`
  - [ ] 4.4 Drop column `data`

---

## Phase 3 — Entity & Package Restructure (REQ-01, REQ-04)

- [ ] 5. Create `model/` package and move `File.java` from `repository/` to `model/`; update all import references
- [ ] 6. Update `File` entity:
  - [ ] 6.1 Remove `data` field (`byte[]`, `@Lob`)
  - [ ] 6.2 Add `storageKey` field mapped to `STORAGE_KEY` column
  - [ ] 6.3 Add `createdAt` field (`OffsetDateTime`) mapped to `CREATED_AT` column, `updatable = false`
- [ ] 7. Update `FileModelAssembler` — fix import for `File` after package move; verify HATEOAS links still resolve

---

## Phase 4 — Storage Abstraction (REQ-01)

- [ ] 8. Create `service/StorageService.java` interface with `store`, `retrieve`, and `delete` methods
- [ ] 9. Create `storage/LocalStorageService.java`:
  - [ ] 9.1 Annotate with `@Service @Profile("local")`
  - [ ] 9.2 Read storage root from `filemanager.storage.path` (default `./storage`)
  - [ ] 9.3 Implement `store()` — write bytes to `{root}/{uuid}-{filename}`, return relative path as storage key
  - [ ] 9.4 Implement `retrieve()` — read file by storage key, throw `StorageException` if not found
  - [ ] 9.5 Implement `delete()` — delete file by storage key, throw `StorageException` on failure
- [ ] 10. Create `exception/StorageException.java` extending `RuntimeException`
- [ ] 11. Add `StorageException` handler to `FileControllerAdvice` — map to `500 Internal Server Error` with `ProblemDetail`
- [ ] 12. Add `filemanager.storage.path` config property to `application.yml`

---

## Phase 5 — Service Layer (REQ-04, REQ-05)

- [ ] 13. Create `service/FileService.java`:
  - [ ] 13.1 Constructor-inject `FileRepository`, `StorageService`, and allowed MIME types config
  - [ ] 13.2 Implement `upload(MultipartFile)` — validate not empty, sanitize filename, validate MIME type, store bytes, persist metadata
  - [ ] 13.3 Implement `findById(UUID)` — delegate to repository, throw `FileNotFoundException` if absent
  - [ ] 13.4 Implement `getBytes(UUID)` — find entity, call `storageService.retrieve(storageKey)`
  - [ ] 13.5 Implement `findAll(Pageable)` — delegate to repository
  - [ ] 13.6 Implement `delete(UUID)` — find entity, delete from repository, call `storageService.delete(storageKey)`
- [ ] 14. Add `filemanager.allowed-mime-types` list to `application.yml`

---

## Phase 6 — Controller Refactor (REQ-04)

- [ ] 15. Refactor `FileController`:
  - [ ] 15.1 Replace `FileRepository` constructor dependency with `FileService`
  - [ ] 15.2 Delegate `getAllFiles` → `fileService.findAll(pageable)`
  - [ ] 15.3 Delegate `uploadFile` → `fileService.upload(file)`
  - [ ] 15.4 Delegate `downloadFile` → `fileService.findById(id)` + `fileService.getBytes(id)`
  - [ ] 15.5 Delegate `getFile` → `fileService.findById(id)` + `fileService.getBytes(id)`
  - [ ] 15.6 Delegate `deleteFile` → `fileService.delete(id)`
  - [ ] 15.7 Remove `@ResponseStatus` annotations on methods that return `ResponseEntity`
  - [ ] 15.8 Remove direct `IOException` catch block

---

## Phase 7 — Security (REQ-02, REQ-03)

- [ ] 16. Uncomment `spring-boot-starter-security` and `spring-security-test` in `build.gradle`
- [ ] 17. Create `security/SecurityConfig.java`:
  - [ ] 17.1 Stateless session (`SessionCreationPolicy.STATELESS`)
  - [ ] 17.2 Permit actuator and API docs endpoints without auth
  - [ ] 17.3 Require `SCOPE_filemanager:read` for `GET /filemanager/**`
  - [ ] 17.4 Require `SCOPE_filemanager:write` for `PUT` and `DELETE /filemanager/**`
  - [ ] 17.5 Configure JWT resource server
  - [ ] 17.6 Return `ProblemDetail` for 401 and 403 via custom entry point and access denied handler
- [ ] 18. Add `spring.security.oauth2.resourceserver.jwt.issuer-uri` to `application.yml`
- [ ] 19. Harden `CorsConfig` — inject `CORS_ALLOWED_ORIGINS`, parse as comma-separated list, default to empty; add config key to `application.yml`

---

## Phase 8 — Tests (REQ-07)

- [ ] 20. Add Testcontainers dependencies to `build.gradle` (`spring-boot-testcontainers`, `testcontainers:junit-jupiter`, `testcontainers:postgresql`)
- [ ] 21. Write `FileModelAssemblerTest` — verify `self` and `delete` HATEOAS links on `toModel()`
- [ ] 22. Write `FileControllerAdviceTest` — one test per handler verifying HTTP status and `ProblemDetail` body
- [ ] 23. Write `FileServiceTest` with Mockito:
  - [ ] 23.1 Upload: happy path
  - [ ] 23.2 Upload: empty file → `BadRequestException`
  - [ ] 23.3 Upload: invalid MIME type → `BadRequestException`
  - [ ] 23.4 Upload: path traversal filename is sanitized
  - [ ] 23.5 FindById: found
  - [ ] 23.6 FindById: not found → `FileNotFoundException`
  - [ ] 23.7 Delete: happy path
  - [ ] 23.8 Delete: not found → `FileNotFoundException`
- [ ] 24. Write `LocalStorageServiceTest` with `@TempDir`:
  - [ ] 24.1 Store and retrieve round-trip
  - [ ] 24.2 Retrieve non-existent key → `StorageException`
  - [ ] 24.3 Delete removes file
- [ ] 25. Write `FileControllerTest` with `@WebMvcTest`:
  - [ ] 25.1 `GET /filemanager/list` — 200 paged response
  - [ ] 25.2 `PUT /filemanager/upload` — 200 entity model
  - [ ] 25.3 `PUT /filemanager/upload` — 400 on empty file
  - [ ] 25.4 `GET /filemanager/{id}` — 200
  - [ ] 25.5 `GET /filemanager/{id}` — 404 on unknown id
  - [ ] 25.6 `GET /filemanager/download/{id}` — 200 octet-stream
  - [ ] 25.7 `GET /filemanager/download/{id}` — 404 on unknown id
  - [ ] 25.8 `DELETE /filemanager/{id}` — 200
  - [ ] 25.9 `DELETE /filemanager/{id}` — 404 on unknown id
- [ ] 26. Write `FileIntegrationTest` with `@SpringBootTest` + Testcontainers PostgreSQL:
  - [ ] 26.1 Upload → assert 201/200 and entity returned
  - [ ] 26.2 List → assert 1 result
  - [ ] 26.3 Download → assert bytes match uploaded content
  - [ ] 26.4 Delete → assert 200
  - [ ] 26.5 List → assert 0 results

---

## Phase 9 — S3 Storage (REQ-01, deferred)

- [ ] 27. Add AWS SDK v2 `s3` dependency to `build.gradle`
- [ ] 28. Create `storage/S3StorageService.java`:
  - [ ] 28.1 Annotate with `@Service @Profile("prod")`
  - [ ] 28.2 Implement `store()` — `PutObjectRequest` with UUID-prefixed key
  - [ ] 28.3 Implement `retrieve()` — `GetObjectRequest`, return bytes
  - [ ] 28.4 Implement `delete()` — `DeleteObjectRequest`
  - [ ] 28.5 Wrap SDK exceptions in `StorageException`
- [ ] 29. Add `filemanager.storage.s3-bucket` config to `application.yml`
- [ ] 30. Write `S3StorageServiceTest` using Testcontainers LocalStack
