# Redcurrant Platform

## Overview

**Redcurrant Platform** is a multi-module Spring Boot application implementing the **Hexagonal Architecture**. It
provides a clean separation between business logic, infrastructure, and application logic, allowing extensibility,
scalability, and easier testing.

---

## Project Structure

The project uses a **multi-module** approach with the following layers:

```
redcurrant-platform/
├── application/      # REST Controllers and orchestration logic
├── domain/           # Business logic, APIs, and SPIs
├── infrastructure/   # Database and external system interactions
└── launcher/         # Main entry point and Spring configurations
```

### Design

The application follows the **Hexagonal Architecture**:

- **Domain**: Core business logic, ports (APIs, SPIs), and services.
- **Application**: REST Controllers exposing endpoints for interaction.
- **Infrastructure**: Adapters and repositories for interacting with the database.
- **Launcher**: Starts the application and declares configuration beans.

---

## Prerequisites

1. **Java**: 21+
2. **Maven**: 3.9.9
3. **MySQL**: 8.4.0 (local setup)
4. **Solace**: Messaging broker setup

---

## Local Development

### Build

To build the application:

- **With tests**:
  ```sh
  mvn clean install
  ```

- **Without tests**:
  ```sh
  mvn clean install -DskipTests=true
  ```

### Run

#### Using Maven

- **Default Profile**:
  ```sh
  mvn spring-boot:run
  ```

- **Custom Profile** (e.g., `local`):
  ```sh
  mvn spring-boot:run -Dspring-boot.run.profiles=local
  ```

#### Using IntelliJ IDEA

- **Default Profile**:
  ```
  Edit Configurations -> Add New Configuration -> Spring Boot -> Apply -> Run
  ```

- **Custom Profile**:
  ```
  Edit Configurations -> Add New Configuration -> Spring Boot -> Active profiles: local -> Apply -> Run
  ```

---

## Authentication

Authentication is handled via the **IDM Service**.

---

## Environments

The application supports multiple environments using **Spring profiles**:

| Profile   | Property File                | Description                   |
|-----------|------------------------------|-------------------------------|
| `default` | application.properties       | Default Spring Boot profile   |
| `local`   | application-local.properties | Local development environment |

---

## Swagger UI

Access Swagger UI for API documentation at:

```
http://localhost:15511/redcurrant-platform.html
```

---

## Logging

The application uses **Logback** for centralized logging. Configuration files can be found under:

```
src/main/resources/logback-spring.xml
```

---

## Testing

Run tests using:

```sh
mvn test
```

**Testing Framework**: JUnit 5, Mockito.
