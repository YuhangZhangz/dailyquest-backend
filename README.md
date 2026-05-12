# DailyQuest Backend

DailyQuest Backend is a Spring Boot REST API for a gamified daily-task product. It provides user registration, JWT-based authentication, user profile lookup, per-user daily task management, XP rewards, level progression, and daily streak tracking.

Documentation baseline: 2026-05-12.

## Table of Contents

- [System Scope](#system-scope)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Local Development](#local-development)
- [Authentication](#authentication)
- [API Reference](#api-reference)
- [Domain Rules](#domain-rules)
- [Error Handling](#error-handling)
- [Testing](#testing)
- [Operational Notes](#operational-notes)
- [README Maintenance Guide](#readme-maintenance-guide)

## System Scope

Current backend capabilities:

- Register and log in application users.
- Issue signed JWT access tokens.
- Resolve the current user profile from the security context.
- Create, list, fetch, update, delete, and complete daily tasks.
- Enforce task ownership by authenticated user.
- Award XP when a task is completed.
- Recalculate user level from total XP.
- Maintain daily streak state from completion dates.
- Return structured JSON errors for validation, duplicate user, invalid credentials, task not found, completed-task, and invalid JSON scenarios.

Current backend boundaries:

- PostgreSQL is the configured persistence store.
- Schema creation/update is currently handled by Hibernate `ddl-auto=update`.
- The repository does not currently include a dedicated migration tool, OpenAPI specification, CI workflow, Docker Compose file, or separate production/test Spring profiles.

## Technology Stack

| Area | Technology |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 4.0.6 |
| Build | Maven Wrapper |
| Web | Spring Web |
| Security | Spring Security, BCrypt, JWT Bearer tokens |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Validation | Jakarta Bean Validation |
| JWT Library | JJWT 0.12.6 |
| Test Framework | Spring Boot Test, JUnit 5 |

## Architecture

The codebase follows a conventional layered Spring architecture:

```text
HTTP request
  -> Controller
  -> Service
  -> Repository
  -> PostgreSQL

JWT request
  -> JwtAuthenticationFilter
  -> JwtService
  -> AppUserRepository
  -> SecurityContext
```

Layer responsibilities:

| Layer | Responsibility | Main Package |
| --- | --- | --- |
| Controller | REST endpoints and request validation entry points | `controller` |
| Service | Business rules, authentication flow, XP, level, streak, task ownership | `service` |
| Repository | Database access through Spring Data JPA | `repository` |
| Model | JPA entities and domain enums | `model` |
| DTO | Request and response contracts | `dto` |
| Config | Security filter chain, JWT filter, password encoder | `config` |
| Exception | API error mapping and response shape | `exception` |

## Project Structure

```text
.
├── api-test.http
├── mvnw
├── mvnw.cmd
├── pom.xml
├── src
│   ├── main
│   │   ├── java/com/example/dailyquest
│   │   │   ├── DailyquestApplication.java
│   │   │   ├── config
│   │   │   ├── controller
│   │   │   ├── dto
│   │   │   ├── exception
│   │   │   ├── model
│   │   │   ├── repository
│   │   │   └── service
│   │   └── resources/application.properties
│   └── test/java/com/example/dailyquest
└── target
```

## Configuration

Default configuration is stored in `src/main/resources/application.properties`.

| Property | Environment Variable | Current Default | Purpose |
| --- | --- | --- | --- |
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/dailyquest` | PostgreSQL JDBC URL |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | `postgres` | Database password |
| `spring.jpa.hibernate.ddl-auto` | `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Hibernate schema behavior |
| `spring.jpa.show-sql` | `SPRING_JPA_SHOW_SQL` | `true` | SQL logging |
| `jwt.secret` | `JWT_SECRET` | development secret | JWT signing secret |
| `jwt.expiration-ms` | `JWT_EXPIRATION_MS` | `86400000` | Token lifetime in milliseconds |

Production recommendations:

- Override all secrets and credentials outside source control.
- Use a long random `JWT_SECRET`; 32 bytes is a practical minimum, 64 bytes is preferred.
- Replace `ddl-auto=update` with a migration tool such as Flyway or Liquibase before production rollout.
- Set `spring.jpa.show-sql=false` outside local development.
- Add environment-specific Spring profiles when deployment environments diverge.

## Local Development

### Prerequisites

- JDK 17+
- PostgreSQL running locally or in Docker
- PowerShell, Bash, or another shell capable of running the Maven Wrapper

### Start PostgreSQL with Docker

```bash
docker run --name dailyquest-postgres \
  -e POSTGRES_DB=dailyquest \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:16
```

If PostgreSQL is already installed locally, create the database manually:

```bash
createdb -U postgres dailyquest
```

### Run the API

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
./mvnw spring-boot:run
```

The API starts on Spring Boot's default port unless overridden:

```text
http://localhost:8080
```

### Useful Maven Commands

| Command | Purpose |
| --- | --- |
| `.\mvnw.cmd spring-boot:run` | Run locally on Windows |
| `./mvnw spring-boot:run` | Run locally on macOS/Linux |
| `.\mvnw.cmd test` | Run tests on Windows |
| `./mvnw test` | Run tests on macOS/Linux |
| `.\mvnw.cmd clean package` | Build a deployable JAR on Windows |
| `./mvnw clean package` | Build a deployable JAR on macOS/Linux |

### HTTP Request Collection

`api-test.http` contains local request examples that can be run from IDE REST Client plugins. Login first and replace stale Bearer tokens with the latest token returned by `/auth/login`.

## Authentication

Authentication uses JWT Bearer tokens.

Header format:

```http
Authorization: Bearer <token>
```

Token behavior:

- Tokens are generated on successful registration and login.
- The JWT subject is the user email.
- The JWT includes a `userId` claim.
- Tokens are validated by `JwtAuthenticationFilter`.
- The authenticated `AppUser` is stored as the Spring Security principal.

Security configuration:

| Route Pattern | Current Security Rule | Business Contract |
| --- | --- | --- |
| `/auth/register` | Public | Public |
| `/auth/login` | Public | Public |
| `/auth/me` | Matched by `/auth/**` public rule | Requires a valid Bearer token to resolve the current user |
| `/daily-tasks/**` | Authenticated | Requires a valid Bearer token |
| Other routes | Public | No documented API contract |

Maintenance note: `/auth/me` depends on an authenticated principal even though the current matcher permits `/auth/**`. For production hardening, split public auth endpoints from authenticated profile endpoints in `SecurityConfig`.

## API Reference

Base URL for local development:

```text
http://localhost:8080
```

### Auth Endpoints

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| `POST` | `/auth/register` | Public | Create a user and return a JWT |
| `POST` | `/auth/login` | Public | Authenticate with email/password and return a JWT |
| `GET` | `/auth/me` | Bearer token | Return the current user profile |

Register request:

```json
{
  "username": "yuhang",
  "email": "yuhang@test.com",
  "password": "123456"
}
```

Login request:

```json
{
  "email": "yuhang@test.com",
  "password": "123456"
}
```

Auth response:

```json
{
  "userId": 1,
  "username": "yuhang",
  "email": "yuhang@test.com",
  "token": "eyJhbGciOi..."
}
```

User profile response:

```json
{
  "id": 1,
  "username": "yuhang",
  "email": "yuhang@test.com",
  "totalXp": 0,
  "level": 1,
  "dailyStreak": 0
}
```

### Daily Task Endpoints

All daily task endpoints require:

```http
Authorization: Bearer <token>
```

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/daily-tasks` | List tasks owned by the current user |
| `GET` | `/daily-tasks/{id}` | Fetch one task owned by the current user |
| `POST` | `/daily-tasks` | Create a task for the current user |
| `PUT` | `/daily-tasks/{id}` | Update an active task owned by the current user |
| `DELETE` | `/daily-tasks/{id}` | Delete a task owned by the current user |
| `PATCH` | `/daily-tasks/{id}/complete` | Complete an active task and award XP |

Create/update task request:

```json
{
  "title": "Push Day",
  "description": "Complete today workout",
  "difficulty": "T3"
}
```

Daily task response:

```json
{
  "id": 1,
  "title": "Push Day",
  "description": "Complete today workout",
  "difficulty": "T3",
  "baseXp": 20,
  "active": true,
  "createdAt": "2026-05-12T18:30:00"
}
```

## Domain Rules

### Difficulty and XP

| Difficulty | Label | Base XP |
| --- | --- | --- |
| `T1` | Easy | 5 |
| `T2` | Normal | 10 |
| `T3` | Hard | 20 |
| `T4` | Elite | 40 |
| `BOSS` | Boss | 100 |

### User Rules

- `username` must be unique.
- `email` must be unique and valid.
- `password` must be at least 6 characters.
- Passwords are stored as BCrypt hashes.
- New users start with `totalXp = 0`, `level = 1`, and `dailyStreak = 0`.

### Task Rules

- Every task belongs to exactly one user.
- Users can only access their own tasks.
- New tasks are active by default.
- `baseXp` is derived from `difficulty`.
- Updating a completed task returns a bad request.
- Completing a completed task returns a bad request.
- Completing a task sets `active = false`.
- Deleting a task removes it from persistence.

### Progression Rules

- Completing a task adds its `baseXp` to the current user's `totalXp`.
- Level is recalculated as `(totalXp / 100) + 1`.
- First completion sets `dailyStreak = 1`.
- Another completion on the same day keeps the current streak.
- A completion on the next calendar day increments the streak by 1.
- A completion after a missed day resets the streak to 1.

## Error Handling

Errors use a structured JSON response:

```json
{
  "timestamp": "2026-05-12T18:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Title is required",
  "path": "/daily-tasks"
}
```

Mapped application errors:

| Scenario | Status | Source |
| --- | --- | --- |
| Validation failure | `400 Bad Request` | `MethodArgumentNotValidException` |
| Invalid JSON body | `400 Bad Request` | `HttpMessageNotReadableException` |
| Task already completed | `400 Bad Request` | `TaskAlreadyCompletedException` |
| Invalid credentials | `401 Unauthorized` | `InvalidCredentialsException` |
| Task not found or not owned by user | `404 Not Found` | `DailyTaskNotFoundException` |
| Duplicate username or email | `409 Conflict` | `DuplicateUserException` |

## Testing

The repository currently includes a Spring context-load test:

```text
src/test/java/com/example/dailyquest/DailyquestApplicationTests.java
```

Run tests:

```powershell
.\mvnw.cmd test
```

Current test environment note:

- The test loads the Spring application context.
- No dedicated test profile or in-memory database is configured.
- Ensure PostgreSQL is available with the configured datasource, or add a test profile before expecting isolated test runs.

Suggested next test coverage:

- Auth registration and login success cases.
- Duplicate username/email conflict cases.
- Invalid credential handling.
- Task ownership enforcement.
- Task completion XP, level, and streak calculations.
- Completed-task update/complete rejection.
- Validation error response shape.

## Operational Notes

### Database

`AppUser` explicitly maps to table `app_users`. `DailyTask` uses the default JPA entity table naming strategy.

Important persistence behavior:

- User passwords are persisted as hashes in `passwordHash`.
- `DailyTask.user` is a lazy `ManyToOne` association.
- Task lookup methods include `userId` to enforce ownership at the repository query level.

### Security

Current implementation:

- BCrypt password hashing is configured through `PasswordEncoder`.
- JWT validation is performed once per request by `JwtAuthenticationFilter`.
- Authenticated requests currently use an empty authority list.
- CSRF is disabled, which is normal for stateless token APIs but should be paired with explicit stateless session configuration in production.

Production hardening checklist:

- Externalize `jwt.secret` and database credentials.
- Add `SessionCreationPolicy.STATELESS`.
- Restrict `/auth/me` to authenticated users.
- Define CORS policy for frontend origins.
- Add rate limiting for login/register endpoints.
- Add request/response logging with sensitive-field masking.
- Add health checks and metrics, for example Spring Boot Actuator.
- Add database migrations and rollback procedures.
- Add CI checks for tests, formatting, dependency vulnerabilities, and build packaging.

### Dependency Notes

The current Maven model includes:

- Spring Boot parent `4.0.6`.
- Java `17`.
- PostgreSQL runtime driver.
- JJWT API, implementation, and Jackson runtime modules.

Maintenance note: the PostgreSQL dependency appears twice in `pom.xml`. Maven can still resolve the project, but keeping dependencies unique makes future upgrades cleaner.

## README Maintenance Guide

Use this section as the update contract when the backend changes.

| If you change... | Update this README section |
| --- | --- |
| Controller mappings | `API Reference` |
| Request/response DTOs | `API Reference` examples and tables |
| Security matchers or JWT behavior | `Authentication` and `Operational Notes` |
| XP, level, streak, or difficulty logic | `Domain Rules` |
| Database entities or relationships | `Architecture`, `Project Structure`, and `Operational Notes` |
| `application.properties` | `Configuration` |
| Build plugins or dependencies | `Technology Stack` and `Dependency Notes` |
| Test strategy | `Testing` |
| Deployment or runtime requirements | `Local Development` and `Operational Notes` |

Recommended documentation workflow:

1. Change code.
2. Update the matching README sections from the table above.
3. Run tests or document why tests could not be run.
4. Refresh `api-test.http` examples when endpoint contracts change.
5. Keep sample tokens out of committed documentation.
