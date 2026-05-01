# Tech Stack

## Language & Runtime
- Java 21 (toolchain), Eclipse Temurin 22 JDK in Docker
- Spring Boot 3.3.1
- Spring Cloud 2023.0.2 (partially enabled)

## Frameworks & Libraries
- **Spring Data JPA** — repository layer with `JpaRepository`
- **Spring Data REST** — REST exposure of repositories
- **Spring HATEOAS** — hypermedia links via `RepresentationModel` and `RepresentationModelAssembler`
- **Spring Validation** — bean validation (`jakarta.validation`)
- **Spring Actuator** — health/info endpoints at `/filemanager/health` and `/filemanager/info`
- **Springdoc OpenAPI 2.5.0** — API docs at `/v1/api/filemanager/api-docs` (Swagger UI disabled)
- **Flyway** — database migrations (`flyway-database-postgresql`)
- **Lombok** — `@Data`, `@EqualsAndHashCode`, `@ToString` on entities
- **PostgreSQL** — primary database (JDBC driver + Flyway dialect)

## Build System
- **Gradle 8** with Groovy DSL (`build.gradle`)
- Publishes to GitHub Packages (requires `USERNAME` and `PKG_TOKEN` env vars)

## Common Commands

```bash
# Build (compile + test)
./gradlew build

# Run locally
./gradlew bootRun

# Run tests only
./gradlew test

# Build Docker image (requires prior ./gradlew build)
docker build -t filemanager:latest .

# Start PostgreSQL dependency
docker compose up -d
```

## Runtime Configuration

Key environment variables (with defaults):

| Variable            | Default         | Description              |
|---------------------|-----------------|--------------------------|
| `DATABASE_HOST`     | `localhost`     | PostgreSQL host          |
| `DATABASE_PORT`     | `5432`          | PostgreSQL port          |
| `DATABASE_NAME`     | `postgres`      | Database name            |
| `DATABASE_USERNAME` | `postgres`      | DB username              |
| `DATABASE_PASSWORD` | `ChangeMeNow!`  | DB password              |
| `DISCOVERY_ENABLED` | `true`          | Eureka discovery toggle  |
| `DOCKER_ENABLED`    | `true`          | Docker Compose toggle    |

## Server
- Port: `8082`
- Context path: `/v1/api`
- Base management path: `/filemanager`
