# Demo guide

## Start

```bash
cp .env.example .env
docker compose up --build
```

Open `http://localhost:3000`, register any account, and sign in. In the local
Docker environment Ledgerly creates one idempotent, synthetic workspace named
`Northstar Studio` for that account.

The workspace includes traceable demo records for every product area:

- posted income and expense transactions;
- income and expense categories;
- customers, draft/sent/paid invoices, and a recorded payment;
- a confirmed statement import with duplicate detection;
- a categorization rule;
- a current-month financial report calculated by the backend.

The seed is enabled only when `LEDGERLY_DEMO_SEED_ENABLED=true`. Set it to
`false` outside local or portfolio-demo environments.

## Suggested walkthrough

1. Show the overview and explain that totals come from posted ledger transactions.
2. Open Transactions and filter the seeded income and expenses.
3. Open Statement import and show `northstar-october-statement.csv` in the history.
4. Upload [`sample-statement.csv`](sample-statement.csv), review its rows, and confirm it.
5. Show the seeded customers and draft, sent, and paid invoice states.
6. Open Rules and apply the cloud-hosting categorization rule.

## Quick verification

With the Docker stack running:

```bash
node scripts/smoke-test.mjs
```

The script creates an isolated QA user, verifies that the seed is
idempotent, and checks that every core module returns populated data.
