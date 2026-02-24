CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE organizations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(60) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE app_users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(190) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(140) NOT NULL,
    active BOOLEAN NOT NULL,
    organization_id BIGINT NOT NULL REFERENCES organizations(id),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL REFERENCES organizations(id),
    created_by BIGINT NOT NULL REFERENCES app_users(id),
    name VARCHAR(180) NOT NULL,
    description VARCHAR(2000),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    assignee_id BIGINT REFERENCES app_users(id),
    title VARCHAR(180) NOT NULL,
    description VARCHAR(2000),
    status VARCHAR(30) NOT NULL,
    priority VARCHAR(30) NOT NULL,
    order_index INTEGER NOT NULL,
    due_date DATE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE kpis (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL REFERENCES organizations(id),
    name VARCHAR(150) NOT NULL,
    description VARCHAR(1500),
    unit VARCHAR(40) NOT NULL,
    target_value NUMERIC(19,4),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE kpi_readings (
    id BIGSERIAL PRIMARY KEY,
    kpi_id BIGINT NOT NULL REFERENCES kpis(id) ON DELETE CASCADE,
    value NUMERIC(19,4) NOT NULL,
    observed_at TIMESTAMPTZ NOT NULL,
    source VARCHAR(120),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE kpi_summaries (
    id BIGSERIAL PRIMARY KEY,
    kpi_id BIGINT NOT NULL UNIQUE REFERENCES kpis(id) ON DELETE CASCADE,
    reading_count BIGINT NOT NULL,
    sum_value NUMERIC(19,4) NOT NULL,
    min_value NUMERIC(19,4),
    max_value NUMERIC(19,4),
    avg_value NUMERIC(19,4),
    last_value NUMERIC(19,4),
    last_observed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    token VARCHAR(120) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN NOT NULL,
    replaced_by_token VARCHAR(120),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE report_jobs (
    id BIGSERIAL PRIMARY KEY,
    requested_by BIGINT NOT NULL REFERENCES app_users(id),
    format VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    request_payload VARCHAR(4000),
    object_key VARCHAR(255),
    download_url VARCHAR(1000),
    error_message VARCHAR(2000),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    actor_email VARCHAR(190),
    action VARCHAR(120) NOT NULL,
    resource VARCHAR(200) NOT NULL,
    resource_id VARCHAR(120),
    method VARCHAR(10) NOT NULL,
    path VARCHAR(255) NOT NULL,
    payload VARCHAR(4000),
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_projects_org ON projects(organization_id);
CREATE INDEX idx_tasks_project_order ON tasks(project_id, order_index);
CREATE INDEX idx_kpi_readings_kpi_observed ON kpi_readings(kpi_id, observed_at DESC);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_report_jobs_status ON report_jobs(status);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at DESC);
