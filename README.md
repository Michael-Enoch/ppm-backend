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

1. Start infrastructure:

```bash
docker compose up -d postgres redis zookeeper kafka minio
```

2. Run the app:

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

## Postman

Import: `postman/Enterprise-PPM-Suite.postman_collection.json`
