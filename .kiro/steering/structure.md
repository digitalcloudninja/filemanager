# Project Structure

## Root Layout

```
filemanager/
├── src/
│   ├── main/
│   │   ├── java/ninja/digitalcloud/cloud/filemanager/
│   │   └── resources/
│   └── test/
│       └── java/ninja/digitalcloud/cloud/filemanager/
├── postman/                  # Postman collections for local dev and API docs
├── build.gradle              # Gradle build config
├── compose.yml               # Docker Compose (PostgreSQL only)
└── Dockerfile                # Builds the service image
```

## Source Package Layout

Base package: `ninja.digitalcloud.cloud.filemanager`

| Package       | Purpose |
|---------------|---------|
| `controller`  | REST controllers and `@ControllerAdvice` for exception handling |
| `repository`  | JPA entity (`File`), `JpaRepository`, and HATEOAS `RepresentationModelAssembler` |
| `exception`   | Custom runtime exceptions (`BadRequestException`, `FileNotFoundException`) |
| `security`    | CORS configuration (`CorsConfig`) |

## Key Files

| File | Role |
|------|------|
| `Main.java` | Spring Boot entry point |
| `FileController.java` | All REST endpoints (`/filemanager/*`) |
| `FileControllerAdvice.java` | Global exception → `ProblemDetail` mapping |
| `File.java` | JPA entity + HATEOAS `RepresentationModel` |
| `FileRepository.java` | Spring Data JPA repository |
| `FileModelAssembler.java` | Converts `File` entity to `EntityModel<File>` with HATEOAS links |
| `application.yml` | Main runtime config (port, datasource, multipart limits, springdoc) |
| `bootstrap.yml` | Spring Cloud / Eureka config (mostly disabled) |
| `db/migration/V1_0__initial_ddl.sql` | Flyway baseline migration — creates `filemanager.files` table |

## Conventions

- **Entity placement**: JPA entities live in the `repository` package alongside the repository interface and model assembler.
- **Exception handling**: All exceptions extend `RuntimeException`. Mapping to HTTP responses is done exclusively in `FileControllerAdvice` using `ProblemDetail`.
- **HATEOAS**: Every entity response is wrapped in `EntityModel<File>` via `FileModelAssembler`. List responses use `PagedModel` via `PagedResourcesAssembler`.
- **OpenAPI annotations**: All controller methods use `@Operation`, `@ApiResponses`, and `@ApiResponse` with inline JSON examples in `@Schema`.
- **Logging**: Use `LoggerFactory.getLogger(ClassName.class)` — SLF4J, not `@Slf4j`.
- **Constructor injection**: Use `@Autowired` on constructors, not field injection.
- **Database migrations**: All schema changes go through Flyway scripts in `src/main/resources/db/migration/`. Follow the naming pattern `V{major}_{minor}__{description}.sql`.
- **Lombok**: Entities use `@Data`, `@EqualsAndHashCode(callSuper = true)`, and `@ToString.Exclude` on `byte[]` fields to avoid memory issues.
