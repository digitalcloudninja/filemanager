<a name="readme-top"></a>

<!-- PROJECT SHIELDS -->
[![GitHub last commit](https://img.shields.io/github/last-commit/digitalcloudninja/filemanager.svg?style=for-the-badge)](https://github.com/digitalcloudninja/filemanager/commits/main)
[![GitHub Tag](https://img.shields.io/github/v/tag/digitalcloudninja/filemanager.svg?style=for-the-badge)](https://github.com/digitalcloudninja/filemanager/tags)
[![GitHub forks](https://img.shields.io/github/forks/digitalcloudninja/filemanager.svg?style=for-the-badge)](https://github.com/digitalcloudninja/filemanager/network/members)
[![GitHub stars](https://img.shields.io/github/stars/digitalcloudninja/filemanager.svg?style=for-the-badge)](https://github.com/digitalcloudninja/filemanager/stargazers)
[![License](https://img.shields.io/github/license/digitalcloudninja/filemanager.svg?style=for-the-badge)](https://github.com/digitalcloudninja/filemanager/blob/main/LICENSE)

<!-- TECHNOLOGY BADGES -->
[![Java JDK 22](https://img.shields.io/badge/java-F80000?style=for-the-badge&logo=oracle&logoColor=white)](https://www.oracle.com/java/)
[![Gradle 8.8](https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://docs.gradle.org/current/userguide/userguide.html)
[![Spring](https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/)
[![Docker](https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![OpenAPI](https://img.shields.io/badge/openapi-6DB33F?style=for-the-badge&logo=openapiinitiative&logoColor=white)](https://github.com/digitalcloudninja/filemanager/blob/main/index.json)

<br />
<div align="center">
  <a href="https://github.com/digitalcloudninja/filemanager">
    <img src="https://avatars.githubusercontent.com/u/174159620?v=4" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">FileManager Service</h3>

  <p align="center">
    A Spring RESTful FileManager for Container Applications
    <br />
    <a href="https://github.com/digitalcloudninja/filemanager"><strong>Explore the Documentation »</strong></a>
    <br />
    <br />
    <a href="https://github.com/digitalcloudninja/filemanager/issues">Report Bug</a>
    ·
    <a href="https://github.com/digitalcloudninja/filemanager/issues">Request Feature</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-project">About The Project</a></li>
    <li><a href="#features">Features</a></li>
    <li><a href="#built-with">Built With</a></li>
    <li><a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#api-documentation">API Documentation</a></li>
    <li><a href="#deployment">Deployment</a></li>
    <li><a href="#testing">Testing</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

## About The Project

FileManager is a robust, production-ready RESTful service designed specifically for containerized environments. Built with Spring Boot and modern Java practices, it provides a comprehensive API for file operations, management, and storage within distributed systems.

### Key Highlights

- **Container-Native Design**: Optimized for Docker and Kubernetes deployments
- **RESTful API**: Clean, intuitive endpoints following REST best practices
- **Production-Ready**: Built with Spring Boot for enterprise-grade reliability
- **OpenAPI Documentation**: Fully documented API with interactive Swagger UI
- **CI/CD Ready**: GitHub Actions workflows for automated testing and deployment

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Features

- ✅ **File Upload & Download**: Secure file upload and retrieval with validation
- ✅ **File Management**: List, search, rename, and delete file operations
- ✅ **Metadata Handling**: Store and retrieve file metadata and attributes
- ✅ **Storage Backends**: Flexible storage configuration (local, cloud, distributed)
- ✅ **Security**: Built-in security features for safe file operations
- ✅ **Multi-format Support**: Handle various file types and formats
- ✅ **Streaming Support**: Efficient handling of large files with streaming
- ✅ **RESTful Design**: Standard HTTP methods and status codes
- ✅ **Error Handling**: Comprehensive error responses with detailed messages
- ✅ **Health Checks**: Built-in health and readiness endpoints for orchestration

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Getting Started

Follow these instructions to get the FileManager service running on your local machine.

### Prerequisites

Before you begin, ensure you have the following installed:

* **Java Development Kit (JDK) 22**
  ```bash
  java -version
  # Should output: java version "22.x.x"
  ```

* **Docker** (for containerized deployment)
  ```bash
  docker --version
  # Should output: Docker version 20.x.x or higher
  ```

* **Docker Compose** (optional, for orchestration)
  ```bash
  docker compose version
  # Should output: Docker Compose version 2.x.x
  ```

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/digitalcloudninja/filemanager.git
   cd filemanager
   ```

2. **Build the project using Gradle**
   ```bash
   # Unix/Linux/macOS
   ./gradlew build
   
   # Windows
   gradlew.bat build
   ```

3. **Run the application locally**
   ```bash
   # Using Gradle
   ./gradlew bootRun
   
   # Or run the built JAR
   java -jar build/libs/filemanager-*.jar
   ```

4. **Using Docker Compose (Recommended)**
   ```bash
   docker compose up -d
   ```

The service will start on `http://localhost:8080` by default.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Usage

### Quick Start Example

Once the service is running, you can interact with it using any HTTP client:

**Upload a file:**
```bash
curl -X POST http://localhost:8080/api/files \
  -F "file=@/path/to/your/file.txt" \
  -F "metadata={\"description\":\"Sample file\"}"
```

**List all files:**
```bash
curl http://localhost:8080/api/files
```

**Download a file:**
```bash
curl http://localhost:8080/api/files/{fileId} -o downloaded-file.txt
```

**Delete a file:**
```bash
curl -X DELETE http://localhost:8080/api/files/{fileId}
```

### Using Postman

Pre-configured Postman collections are available in the `postman/` directory:

1. Import the collection from `postman/filemanager-collection.json`
2. Configure the environment variables
3. Start making requests

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## API Documentation

### OpenAPI Specification

The complete API specification is available in OpenAPI 3.0 format:
- **Specification File**: [`index.json`](index.json)
- **Swagger UI**: `http://localhost:8080/swagger-ui.html` (when running)
- **API Docs**: `http://localhost:8080/v3/api-docs`

### Main Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/files` | List all files |
| GET | `/api/files/{id}` | Get file by ID |
| POST | `/api/files` | Upload new file |
| PUT | `/api/files/{id}` | Update file metadata |
| DELETE | `/api/files/{id}` | Delete file |
| GET | `/api/files/{id}/download` | Download file |
| GET | `/actuator/health` | Health check endpoint |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Deployment

### Docker Deployment

**Build the Docker image:**
```bash
docker build -t filemanager:latest .
```

**Run the container:**
```bash
docker run -d \
  -p 8080:8080 \
  -v /path/to/storage:/app/storage \
  -e SPRING_PROFILES_ACTIVE=prod \
  --name filemanager \
  filemanager:latest
```

### Docker Compose

The included `compose.yml` provides a complete deployment stack:

```bash
docker compose up -d
```

### Kubernetes Deployment

Example Kubernetes deployment:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: filemanager
spec:
  replicas: 3
  selector:
    matchLabels:
      app: filemanager
  template:
    metadata:
      labels:
        app: filemanager
    spec:
      containers:
      - name: filemanager
        image: filemanager:latest
        ports:
        - containerPort: 8080
        volumeMounts:
        - name: storage
          mountPath: /app/storage
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Testing

### Running Tests

Execute the test suite:
```bash
./gradlew test
```

### Test Coverage

Generate a test coverage report:
```bash
./gradlew jacocoTestReport
```

View the report at `build/reports/jacoco/test/html/index.html`

### Integration Tests

Run integration tests:
```bash
./gradlew integrationTest
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Server Configuration
server.port=8080

# File Upload Settings
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Storage Configuration
filemanager.storage.location=/app/storage

# Logging
logging.level.com.digitalcloudninja.filemanager=DEBUG
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Server port | `8080` |
| `STORAGE_LOCATION` | File storage path | `/app/storage` |
| `MAX_FILE_SIZE` | Maximum file size | `10MB` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Roadmap

- [x] Basic file operations (CRUD)
- [x] Docker containerization
- [x] OpenAPI documentation
- [x] CI/CD pipeline with GitHub Actions
- [ ] Cloud storage integration (S3, Azure Blob)
- [ ] File versioning support
- [ ] Advanced search and filtering
- [ ] File preview generation
- [ ] Rate limiting and quota management
- [ ] Multi-tenancy support

See the [open issues](https://github.com/digitalcloudninja/filemanager/issues) for a full list of proposed features and known issues.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java coding conventions
- Write unit tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting PR

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## License

Distributed under the Apache License 2.0. See [`LICENSE`](LICENSE) for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contact

**Digital Cloud Ninja** - [@digitalcloudninja](https://github.com/digitalcloudninja)

Project Link: [https://github.com/digitalcloudninja/filemanager](https://github.com/digitalcloudninja/filemanager)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Acknowledgments

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Gradle](https://gradle.org/)
* [Docker](https://www.docker.com/)
* [OpenAPI Initiative](https://www.openapis.org/)
* [Choose an Open Source License](https://choosealicense.com)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- MARKDOWN LINKS & IMAGES -->
[Java-badge]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.oracle.com/java/
[Spring-badge]: https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[Spring-url]: https://spring.io/
[Gradle-badge]: https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white
[Gradle-url]: https://gradle.org/
[Docker-badge]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[Docker-url]: https://www.docker.com/
[OpenAPI-badge]: https://img.shields.io/badge/OpenAPI-6BA539?style=for-the-badge&logo=openapiinitiative&logoColor=white
[OpenAPI-url]: https://www.openapis.org/

**Made with ❤️ by Digital Cloud Ninja**