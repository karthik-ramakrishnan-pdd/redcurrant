# BLU Reports Service

## Project

### Structure

The project is designed using the multi-module approach and has the following modules:
- 'application'
- 'domain'
- 'infrastructure'
- 'launcher'

Each module encapsulates logic specific to the hexagonal architecture layers.

### Design

The application uses as design pattern the hexagonal architecture pattern.

#### 1. Domain

The domain layer is the core of the application and is isolated from both the application and the infrastructure layers.
This layer contains the data, ports and services used to implement the business logic.

To allow the outside to interact with the domain, the hexagon provides business interfaces (ports) divided into two categories:
- The API - which gathers the interfaces for everything that needs to query the domain.
- The SPI - which gathers the interfaces required by the domain to retrieve information from third parties.

Since the domain layer is completely decoupled from application and infrastructure layers, it is also tested independently.

#### 2. Application
Through the application layer other programs interact with Report Service.
It contains REST controllers which are responsible for orchestrating the execution of domain logic.

#### 3. Infrastructure
The infrastructure layer contains logic needed to interact with the database.
- The adapters implement the infrastructure-dependent interfaces from the domain layer.
- The repositories represent the data access services responsible for CRUD operations on the database.

#### 4. Launcher
The launcher layer is where the application is started.
Additionally, it contains configuration classes where the beans are manually declared.

## Local development
One of the following can be used for the build:
- build with tests

```mvn clean install```

- build without tests

```mvn clean install -DskipTests=true```

Run using Maven

- with **default** Spring profile

```
mvn spring-boot:run
```

- with desired Spring profile

```
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Run using IntelliJ

- with **default** Spring profile

```Edit Configurations -> Add New Configuration -> Spring Boot -> Apply -> Run```

- with desired Spring profile

```Edit Configurations -> Add New Configuration -> Spring Boot -> Active profiles: local -> Apply -> Run```

## Authentication
The authentication is done through the IDM Service.

## Database migration
Flyway is used to migrate database scripts. For local dev, use `db.flyway.clean=true` flag to wipe the database.

## Environments

The environments, also known as Spring profiles in our case, are as follows:

| Profile   | Property file                  | Description                                        |
|-----------|--------------------------------|----------------------------------------------------|
| `default` | *application.properties*       | The default profile defined by Spring Boot         |
| `local`   | *application-local.properties* | The profile used for local development environment |

## Swagger UI

```
http://localhost:12211/reports.html
```