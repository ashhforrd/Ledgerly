# Ledgerly

Ledgerly is a financial operations application for freelancers and small
businesses. It helps users record income and expenses, import bank statements,
manage invoices, and understand business cash flow.

Ledgerly is intentionally not a bank or a full accounting suite. Financial
correctness, traceability, and useful information take priority over decorative
features.

## Architecture

Ledgerly starts as a modular monolith with explicit domain boundaries.

```text
ledgerly
├── identity    authentication, users, and access control
├── workspace   businesses, memberships, and tenant isolation
├── ledger      transactions, categories, and categorization rules
├── imports     statement parsing, validation, and duplicate detection
├── invoicing   customers, invoices, and payments
├── reporting   financial read models and reports
├── audit       immutable records of significant actions
├── outbox      reliable asynchronous processing
└── shared      small cross-module infrastructure primitives
```

PostgreSQL is the source of truth. Redis Streams will only be introduced for
asynchronous work after transactional outbox records have been committed.

## Technology

### Backend

- Java 21
- Spring Boot 4
- Spring Data JPA
- PostgreSQL 17
- Flyway
- Redis
- Gradle
- JUnit

### Frontend

- React 19
- TypeScript
- Vite
- Tailwind CSS
- shadcn/ui

## Local development

Run the complete application:

```bash
cp .env.example .env
docker compose up --build
```

Open the web application at `http://localhost:3000`. The API is also available
directly at `http://localhost:8080`, while requests from the web application to
`/api` and `/actuator` are proxied through Nginx.

To stop the application:

```bash
docker compose down
```

### Run services individually

Start PostgreSQL and Redis:

```bash
cp .env.example .env
docker compose up -d
```

Run the API:

```bash
./gradlew bootRun
```

Run the web application in a second terminal:

```bash
cd frontend
npm install
npm run dev
```

Verify the API health:

```bash
curl http://localhost:8080/actuator/health
```

Run verification:

```bash
./gradlew test
cd frontend && npm run build
```

Stop local infrastructure:

```bash
docker compose down
```

Use `docker compose down --volumes` only when the local database and Redis data
should intentionally be deleted.

## Delivery milestones

1. Identity, workspaces, and tenant isolation.
2. Income, expenses, categories, filtering, and audit metadata.
3. Statement import with preview, validation, and duplicate detection.
4. Customers, invoices, payments, and overdue detection.
5. Financial reports and rule-based categorization.
6. Complete the operations-focused web interface.
7. Reliability tests, observability, documentation, and demo packaging.

Milestones 1–7 are implemented. See [API reference](docs/API.md) and
[demo checklist](docs/DEMO.md) for the supported workflows.

## Verification

Run backend tests and the frontend production build:

```bash
./gradlew test
cd frontend && npm run build
```

Run the complete production-shaped local stack:

```bash
docker compose up --build
curl http://localhost:8080/actuator/health
```

Responses include an `X-Request-ID` header for log correlation. Health,
liveness, readiness, application info, and metrics are exposed through Spring
Boot Actuator.

## Interface principles

- Never display fabricated financial data.
- Every metric states its period, unit, and data source.
- Empty states explain what is missing instead of implying a zero balance.
- Color always has a semantic meaning and is never the only status indicator.
- Destructive and irreversible actions require explicit confirmation.
- The server calculates financial totals; the browser never supplies trusted
  totals.
