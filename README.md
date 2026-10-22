# Employee Management API

A Spring Boot REST API for managing employee records. The project uses PostgreSQL and Flyway, validates incoming requests, protects employee operations with JWT bearer authentication, and provides OpenAPI/Swagger and Spring Boot Actuator endpoints.

## Features

- Employee create, read, update, and delete operations
- Paginated employee listing with optional text search and status filter
- Sorting by supported employee fields
- Request validation and centralized exception handling
- Admin login with a JWT access token; employee APIs require the `ADMIN` role
- PostgreSQL persistence with Flyway schema migrations
- Swagger UI and OpenAPI documentation
- Actuator health, info, and logger management endpoints
- Optional PMD/CPD and SpotBugs/FindSecBugs analysis profile

## Technology

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC, Spring Data JPA, Spring Security OAuth2 Resource Server
- PostgreSQL and Flyway
- springdoc OpenAPI
- Maven Wrapper

## Prerequisites

- JDK 21
- PostgreSQL
- Git (optional, for cloning)

## Local configuration

The default `local` profile is enabled in `src/main/resources/application.yaml`. Keep local credentials in the ignored root-level `application-local.yaml`; do not commit it. A configuration file can look like this (replace the example values locally):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/employee_management
    username: employee_app
    password: replace-with-your-local-database-password

app:
  jwt:
    secret: replace-with-a-random-secret-at-least-32-bytes-long
  auth:
    admin:
      username: admin
      password: replace-with-your-local-admin-password
```

The application also supports environment variables:

| Variable | Purpose | Default |
| --- | --- | --- |
| `DB_URL` | JDBC connection URL | `jdbc:postgresql://localhost:5432/employee_management` |
| `DB_USERNAME` | PostgreSQL username | `employee_app` |
| `DB_PASSWORD` | PostgreSQL password | None; configure it locally |
| `JWT_SECRET` | HMAC signing secret (minimum 32 bytes) | None; configure it locally |
| `JWT_EXPIRATION` | JWT lifetime in milliseconds | `3600000` (1 hour) |
| `ADMIN_USERNAME` | Configured admin username | `admin` |
| `ADMIN_PASSWORD` | Configured admin password | Empty; configure it locally |

Create a PostgreSQL database and a user with permissions to connect and create/alter objects in that database. On startup, Flyway applies the migrations in `src/main/resources/db/migration`; Hibernate validates the resulting schema.

## Run the application

From the project root, use the Maven Wrapper:

```powershell
./mvnw.cmd spring-boot:run
```

Or build and run the packaged application:

```powershell
./mvnw.cmd clean package
java -jar target/employee-management-api-0.0.1-SNAPSHOT.jar
```

The API listens on `http://localhost:8080` by default.

## API documentation

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

In Swagger UI, call the login endpoint, copy `accessToken`, select **Authorize**, and enter the token as a Bearer token. Employee operations are restricted to the configured admin, whose JWT includes the `ADMIN` role.

## Authentication and request examples

### Login

`POST /api/v1/auth/login` (public)

```json
{
  "username": "admin",
  "password": "your-local-admin-password"
}
```

Example response (`200 OK`):

```json
{
  "accessToken": "<signed-jwt>",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

Send the returned token in subsequent protected requests:

```http
Authorization: Bearer <signed-jwt>
```

### Create an employee

`POST /api/v1/employees/create` (ADMIN)

```json
{
  "firstName": "Asha",
  "lastName": "Nair",
  "email": "asha.nair@example.com",
  "department": "Engineering",
  "jobTitle": "Software Engineer",
  "status": "ACTIVE"
}
```

Example response (`201 Created`):

```json
{
  "id": 1,
  "firstName": "Asha",
  "lastName": "Nair",
  "email": "asha.nair@example.com",
  "department": "Engineering",
  "jobTitle": "Software Engineer",
  "status": "ACTIVE"
}
```

### List, search, filter, and sort employees

`GET /api/v1/employees/all` (ADMIN)

Available query parameters:

| Parameter | Description | Default / allowed values |
| --- | --- | --- |
| `page` | Zero-based page number | `0` or greater |
| `size` | Number of rows per page | `10`, from 1 to 100 |
| `search` | Matches name, email, department, or job title | Optional; maximum 100 characters |
| `status` | Filter by employee status | `ACTIVE` or `INACTIVE` |
| `sortBy` | Sort field | `LAST_NAME`, `ID`, `FIRST_NAME`, `EMAIL`, `DEPARTMENT`, `JOB_TITLE`, or `STATUS` |
| `direction` | Sort direction | `ASC` or `DESC` |

Example: `GET /api/v1/employees/all?page=0&size=10&search=engineering&status=ACTIVE&sortBy=LAST_NAME&direction=ASC`

Example response:

```json
{
  "content": [
    {
      "id": 1,
      "firstName": "Asha",
      "lastName": "Nair",
      "email": "asha.nair@example.com",
      "department": "Engineering",
      "jobTitle": "Software Engineer",
      "status": "ACTIVE"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Other employee operations (ADMIN)

| Method | Endpoint | Description | Success response |
| --- | --- | --- | --- |
| `GET` | `/api/v1/employees/{id}` | Retrieve one employee | `200 OK` with employee response |
| `PUT` | `/api/v1/employees/{id}` | Replace employee details; send the full request body | `200 OK` with updated employee response |
| `DELETE` | `/api/v1/employees/{id}` | Delete an employee | `204 No Content` |

Employee request fields are required. Email must be valid; names, department, and job title are limited to 100 characters; email is limited to 255 characters. Status must be `ACTIVE` or `INACTIVE`. Email values must be unique.

### Error responses

API errors use a consistent response body and do not include stack traces or framework exception details. For example, an unsupported employee status returns `400 Bad Request`:

```json
{
  "timestamp": "2026-10-22T14:03:53.404Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid value for 'status'. Allowed values: ACTIVE, INACTIVE.",
  "path": "/api/v1/employees/create"
}
```

Common error statuses include `400` for invalid input, `401` for missing or invalid authentication, `403` for insufficient permissions, `404` for missing resources or endpoints, `409` for duplicate email, and `500` for unexpected server errors. Unexpected errors are logged server-side; internal exception details are not returned to the client.

## Actuator

Only the following Actuator endpoints are exposed over HTTP:

| Endpoint | Access |
| --- | --- |
| `GET /actuator/health` | Public |
| `GET /actuator/info` | Public |
| `/actuator/loggers` | Requires an authenticated ADMIN token |

Examples include `GET /actuator/loggers`, `GET /actuator/loggers/com.codenzic.employeemanagement`, and `POST /actuator/loggers/com.codenzic.employeemanagement` with `{"configuredLevel":"DEBUG"}`. Logger changes are runtime-only and revert when the application restarts. Other Actuator endpoints are not exposed.

## Postman

Import [`postman/employee-management-api.postman_collection.json`](postman/employee-management-api.postman_collection.json) into Postman. The collection includes login, employee CRUD/search/filter requests, and Actuator examples. Set the collection's base URL to `http://localhost:8080` if it is not already set. Run login first and use its JWT as the Bearer token for protected calls.

## Tests and optional static analysis

Run the test suite:

```powershell
./mvnw.cmd test
```

Run the optional PMD, CPD, SpotBugs, and FindSecBugs reports:

```powershell
./mvnw.cmd -Pcode-analysis verify -DskipTests
```

Reports are generated under `target/reports/` and `target/spotbugs/` depending on the plugin output. The analysis profile is opt-in and does not run as part of a normal build.

## Design notes

- The API uses DTOs at its HTTP boundary and keeps JPA entities inside the persistence/business layers.
- Employee routes are guarded with method-level `ADMIN` authorization.
- Authentication compares the username and password with values supplied by external configuration; it does not currently persist application users. The `app_users` table is retained by the initial migration but is not used by this configured-admin login flow.
- JWTs are signed using HMAC-SHA256 and validated by Spring Security's OAuth2 Resource Server support. This is a custom token-issuing login endpoint with Resource Server JWT validation, not a full OAuth2 Authorization Server implementation.
- Do not commit `application-local.yaml`, database credentials, admin credentials, or the JWT secret.
