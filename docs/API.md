# Ledgerly API

All business endpoints require `Authorization: Bearer <token>`. Resources are scoped by `workspaceId`; a resource from another workspace is returned as not found.

## Core endpoints

| Area | Endpoints |
| --- | --- |
| Identity | `POST /api/v1/auth/register`, `POST /login`, `GET /me`, `POST /logout` |
| Workspaces | `POST/GET /api/v1/workspaces` |
| Categories | `POST/GET /workspaces/{id}/categories`, rename and archive endpoints |
| Transactions | `POST/GET /workspaces/{id}/transactions`, update and void endpoints |
| Imports | `GET /workspaces/{id}/imports`, `POST /workspaces/{id}/imports/preview`, `POST /imports/{importId}/confirm` |
| Customers | `POST/GET /workspaces/{id}/customers`, archive endpoint |
| Invoices | `POST/GET /workspaces/{id}/invoices`, send, payment, and void endpoints |
| Reports | `GET /workspaces/{id}/reports/financial-summary?from=YYYY-MM-DD&to=YYYY-MM-DD` |
| Rules | CRUD-style rule endpoints and `POST /categorization-rules/apply` |

## Statement CSV

The importer expects a header followed by `date,description,signed amount`:

```csv
date,description,amount
2026-10-01,Client payment,2500000.00
2026-10-02,Cloud hosting,-350000.00
```

Positive values become income; negative values become expenses. Preview persists validation results but creates no ledger transactions until confirmation.

## Operational endpoints

- `/actuator/health`
- `/actuator/health/liveness`
- `/actuator/health/readiness`
- `/actuator/info`
- `/actuator/metrics`

Every response includes `X-Request-ID`. Send the same header from a client to correlate a request with application logs.
