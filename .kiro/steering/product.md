# FileManager Service

A production-ready RESTful microservice for file storage and management in containerized environments. It provides CRUD operations for files — upload, download, view, list, and delete — storing file data and metadata in PostgreSQL.

## Core Capabilities

- File upload (multipart), download, inline view, and delete
- Paginated file listing with HATEOAS links
- File metadata storage (name, content type, size)
- OpenAPI 3.1 documentation via springdoc
- Actuator health and info endpoints

## Constraints

- Max file size: 1MB per file, 1MB per request
- Files are stored as binary blobs (`oid`) in PostgreSQL — no external storage backend currently
- Spring Security is present as a dependency but currently disabled
- Spring Cloud Config and Eureka client are present but commented out
