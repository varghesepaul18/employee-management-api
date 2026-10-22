# Employee Management API: Requirements and Design

## Purpose

Build a small, explainable REST API for the Codenzic assessment. The project demonstrates layered Spring Boot design, PostgreSQL persistence, validation, exception handling, and JWT-based access control.

## Scope

### Included

- Create, list, retrieve, replace, and delete employees.
- Validate API input and return consistent error responses.
- Persist employees and application users in PostgreSQL using Flyway migrations.
- Authenticate a username and password through a login endpoint and issue a signed, expiring JWT.
- Require the `ADMIN` role for employee endpoints.
- Document the API with Swagger/OpenAPI and expose only Actuator health and info.
- Provide setup instructions, sample requests and responses, and a Postman collection.

### Not included in the first version

- User registration, password reset, refresh tokens, pagination, search, or status filters.
- A separate OAuth2 Authorization Server or Spring Boot Admin dashboard.

## Architecture

Use the existing base package `com.codenzic.employeemanagement` and group code by feature:

- `employee`: controller, service interface and implementation, repository, entity, request/response DTOs, mapper.
- `auth`: controller, authentication service, user entity, repository, and login/token DTOs.
- `security`: security filter-chain configuration, user-details service, and JWT issuing/verification support.
- `config`: OpenAPI and Actuator configuration.
- `exception`: shared error response, global exception handler, and domain exceptions.

Controllers handle HTTP input/output, services implement use cases, and repositories handle persistence. Use constructor injection and keep persistence entities inside the application. Return DTOs from API endpoints. Add interfaces only at useful boundaries, such as the employee service.

## Employee API

All routes use the `/api/v1` prefix. Employee routes require a valid JWT with the `ADMIN` role.

| Method | Path | Behavior | Success response |
| --- | --- | --- | --- |
| `POST` | `/api/v1/employees` | Create an employee | `201 Created`, employee response |
| `GET` | `/api/v1/employees` | List all employees | `200 OK`, array of employee responses |
| `GET` | `/api/v1/employees/{id}` | Retrieve one employee | `200 OK`, employee response |
| `PUT` | `/api/v1/employees/{id}` | Replace all editable employee fields | `200 OK`, updated employee response |
| `DELETE` | `/api/v1/employees/{id}` | Delete an employee | `204 No Content` |

The employee model contains `id`, `firstName`, `lastName`, `email`, `department`, `jobTitle`, and `status`. Status values are `ACTIVE` and `INACTIVE`. The create and update request DTOs contain all editable fields; they do not accept an ID. Require non-blank names, department, and job title; require a syntactically valid email; and require a valid status. Email must be unique. A missing employee returns `404`; a duplicate email returns `409`; invalid input returns `400` with field-level details.

## Authentication and authorization

- `POST /api/v1/auth/login` accepts a username and password.
- Look up the account in PostgreSQL and verify the password using BCrypt. Never store or compare plaintext passwords.
- On success, issue a signed JWT containing the username as subject, the role, issuer, issue time, and expiry. Return the token, `Bearer` token type, and expiry duration.
- **Default:** use an access-token lifetime of 15 minutes. The signing secret is supplied through an environment variable, must not be committed, and must be long enough for the selected signing algorithm.
- Spring Security OAuth2 Resource Server validates the JWT signature and time claims on protected requests and populates the security context. The login endpoint is a custom token-issuing endpoint; this project is not a full OAuth2 Authorization Server.
- Require `ROLE_ADMIN` for all employee routes. Missing, malformed, invalid, or expired tokens return `401`; authenticated users without the required role receive `403`.
- Seed the development admin from environment-provided username and password. Hash the password before saving it. Do not add a public registration endpoint.

## Persistence and configuration

- PostgreSQL is the development database. The schema contains `employees` and `app_users` tables. Usernames and employee emails have unique constraints; user records store a password hash and role.
- Use Flyway scripts under the default `db/migration` classpath location to create and evolve the schema. Hibernate validates the schema rather than generating it.
- Read the JDBC URL, database username/password, admin bootstrap username/password, and JWT signing secret from environment variables. Keep local secrets outside Git and document the required variable names and example non-secret values in the README.

## Documentation and operations

- Swagger/OpenAPI describes request validation, success/error responses, and Bearer-token authentication. Keep the Swagger UI and OpenAPI document accessible for assessment demonstrations; secured employee calls still require an admin token.
- Expose only `/actuator/health` and `/actuator/info`. Keep health details hidden and info limited to non-sensitive application metadata.
- Do not expose credentials, signing secrets, password hashes, or operational details in API responses or logs.

## Error response contract

Use one JSON error shape for application errors with a timestamp, HTTP status, stable error name, readable message, request path, and optional field validation details. Handle validation failures, missing resources, duplicate email, and authentication failures without returning stack traces or internal implementation details.

## Acceptance checklist

- A configured PostgreSQL instance starts with Flyway migrations applied and Hibernate schema validation succeeds.
- Valid employee CRUD requests work; invalid input, duplicate email, and unknown IDs produce the documented error responses.
- Valid seeded admin credentials produce a JWT; invalid credentials are rejected.
- Employee routes reject missing/invalid/expired JWTs and reject a valid non-admin identity.
- Swagger documents the API and can send a Bearer token; only health and info are exposed through Actuator.
- A fresh checkout can be configured and run by following the README without committing secrets.
