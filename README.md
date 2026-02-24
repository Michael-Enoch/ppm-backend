# Enterprise Project & Performance Management Suite - Backend

Spring Boot backend for secure project management, task orchestration, KPI ingestion, reporting, and analytics streaming.

## Stack

- Java 21 (LTS)
- Spring Boot 3.x
- Spring Security + JWT
- PostgreSQL + Flyway
- Redis cache
- Kafka + Kafka Streams
- WebSocket STOMP events
- MinIO object storage for reports
- OpenAPI (springdoc)
- MapStruct

## Key Features

- Auth endpoints: login, refresh token rotation, logout/revoke
- Auth profile endpoint: `GET /api/auth/me`
- RBAC roles: `ADMIN`, `ORG_MANAGER`, `PM`, `MEMBER`, `VIEWER`
- CRUD APIs for users, roles, organizations, projects, tasks, KPIs, readings
- Dashboard API: `GET /api/dashboard` with cached aggregates
- Kanban ordering API: `POST /api/projects/{id}/tasks/order`
- Async report jobs with downloadable links via object storage
- Real-time task events to WebSocket and Kafka
- KPI ingestion pipeline with Kafka Streams rolling aggregates + summary materialization
- Flyway schema + seeded sample users
- Actuator + Prometheus metrics
- Request validation + audit logging on write endpoints
- Redis-backed rate limiting for KPI ingestion endpoint

## Seeded Users

All seeded users use password: `AdminPass123!`

- `admin@example.com` (ADMIN, ORG_MANAGER, PM)
- `manager@example.com` (ORG_MANAGER)
- `pm@example.com` (PM)
- `member@example.com` (MEMBER)
- `viewer@example.com` (VIEWER)

## Required Endpoints Implemented

- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `GET /api/auth/me`
- `GET /api/projects`
- `POST /api/projects`
- `GET /api/projects/{id}`
- `POST /api/projects/{id}/tasks`
- `PATCH /api/projects/{id}/tasks/{taskId}`
- `POST /api/projects/{id}/tasks/order`
- `GET /api/kpis`
- `POST /api/kpis`
- `POST /api/kpis/{id}/readings`
- `POST /api/reports`
- `GET /api/reports/{id}/download`

## Local Run

1. Start infrastructure (skip `postgres` if you provide your own database):

```bash
docker compose up -d redis zookeeper kafka minio
```

If you still rely on the containerized Postgres, run `docker compose up -d postgres redis zookeeper kafka minio` instead, otherwise configure `DB_URL`, `DB_USER`, and `DB_PASS` to point at your external database before starting the app.

2. Run the clean launcher before starting the app:

```powershell
.\scripts\run-clean.ps1
```

The script stops whichever PID is holding `8080`, double-checks the port, and launches `mvn spring-boot:run` in the background while streaming its stdout/stderr to `mvn-run.log` / `mvn-run.err`. Tail those logs with `Get-Content .\mvn-run.log -Wait` and switch to `.\scripts\run-clean.ps1 -MavenCommand .\mvnw` if you prefer the wrapper.

3. Run the app:

```bash
mvn spring-boot:run
```

Or run everything together:

```bash
docker compose up --build
```

## API Docs

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## CORS

Allowed frontend origins are configurable:

- `CORS_ORIGIN_1` (default `http://localhost:3000`)
- `CORS_ORIGIN_2` (default `http://127.0.0.1:3000`)
- `CORS_ORIGIN_3` (default `http://localhost:5173`)
- `CORS_ORIGIN_4` (default `http://127.0.0.1:5173`)

## Render Deploy Notes

For Render Web Services, set these environment variables:

- `SPRING_DATASOURCE_URL` or `JDBC_DATABASE_URL` (must be JDBC format, e.g. `jdbc:postgresql://...`)
- `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` (or `DB_USER` / `DB_PASS`)
- `DB_SSL_MODE=require` when your Render Postgres requires SSL
- `PORT` is auto-provided by Render and already wired in config

If you use Render's non-JDBC `DATABASE_URL` value (starts with `postgres://`), convert it to JDBC format (`jdbc:postgresql://...`) before setting it.

## Postman

Import: `postman/Enterprise-PPM-Suite.postman_collection.json`
