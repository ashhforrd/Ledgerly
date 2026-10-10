# Ledgerly

Ledgerly is a financial operations application for freelancers and small
businesses. It helps users record income and expenses, import bank statements,
manage invoices, and understand business cash flow.

Ledgerly is intentionally not a bank or a full accounting suite. Financial
correctness, traceability, and useful information take priority over decorative
features.

## Interface preview

![Ledgerly financial overview](<screenshots/Screenshot 2026-10-10 at 12.25.53.png>)

<details>
<summary>View the complete product interface</summary>
<br />

<table>
  <tr>
    <td width="50%">
      <strong>Transactions</strong><br /><br />
      <img src="screenshots/Screenshot%202026-10-10%20at%2012.25.59.png" alt="Ledgerly transaction ledger" />
    </td>
    <td width="50%">
      <strong>Invoices</strong><br /><br />
      <img src="screenshots/Screenshot%202026-10-10%20at%2012.26.08.png" alt="Ledgerly invoice management" />
    </td>
  </tr>
  <tr>
    <td width="50%">
      <strong>Customers</strong><br /><br />
      <img src="screenshots/Screenshot%202026-10-10%20at%2012.26.14.png" alt="Ledgerly customer management" />
    </td>
    <td width="50%">
      <strong>Categories</strong><br /><br />
      <img src="screenshots/Screenshot%202026-10-10%20at%2012.26.18.png" alt="Ledgerly financial categories" />
    </td>
  </tr>
  <tr>
    <td width="50%">
      <strong>Statement import</strong><br /><br />
      <img src="screenshots/Screenshot%202026-10-10%20at%2012.26.29.png" alt="Ledgerly CSV statement import" />
    </td>
    <td width="50%">
      <strong>Categorization rules</strong><br /><br />
      <img src="screenshots/Screenshot%202026-10-10%20at%2012.26.34.png" alt="Ledgerly categorization rules" />
    </td>
  </tr>
</table>
</details>

## Product capabilities

- Isolated business workspaces with token-based authentication.
- Income and expense ledger with categories, filtering, and voiding.
- CSV statement preview, validation, duplicate detection, and confirmation.
- Customers and draft, sent, paid, overdue, and void invoice states.
- Rule-based categorization and backend-calculated financial reports.
- A dark, responsive operations interface built with shadcn/ui.

For portfolio demonstrations, the local Docker profile creates a synthetic
`Northstar Studio` workspace after any user registers. The seed is idempotent
and populates every product area; it never runs unless
`LEDGERLY_DEMO_SEED_ENABLED=true`.

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

Register any account to receive the local demo workspace. A fixed credential is
deliberately not stored in the repository. See the [demo guide](docs/DEMO.md)
and use [`docs/sample-statement.csv`](docs/sample-statement.csv) for the import
walkthrough.

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
npm --prefix frontend run lint
npm --prefix frontend run build
```

Run the complete production-shaped local stack:

```bash
docker compose up -d --build
curl http://localhost:8080/actuator/health
node scripts/smoke-test.mjs
```

Responses include an `X-Request-ID` header for log correlation. Health,
liveness, readiness, application info, and metrics are exposed through Spring
Boot Actuator.

## Interface principles

- Synthetic data is restricted to the explicitly enabled local demo workspace.
- Every metric states its period, unit, and data source.
- Empty states explain what is missing instead of implying a zero balance.
- Color always has a semantic meaning and is never the only status indicator.
- Destructive and irreversible actions require explicit confirmation.
- The server calculates financial totals; the browser never supplies trusted
  totals.
