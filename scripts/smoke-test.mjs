#!/usr/bin/env node

import { randomUUID } from "node:crypto";

const apiBase = process.env.LEDGERLY_API_URL ?? "http://localhost:3000/api/v1";

function assert(condition, message) {
  if (!condition) throw new Error(message);
}

async function request(path, token, options = {}) {
  const headers = new Headers(options.headers);
  if (!(options.body instanceof FormData)) headers.set("content-type", "application/json");
  if (token) headers.set("authorization", `Bearer ${token}`);

  const response = await fetch(`${apiBase}${path}`, { ...options, headers });
  const body = await response.json().catch(() => null);
  if (!response.ok) {
    throw new Error(`${options.method ?? "GET"} ${path} returned ${response.status}: ${body?.detail ?? "no response body"}`);
  }
  return body;
}

function jakartaPeriod() {
  const parts = new Intl.DateTimeFormat("en", {
    timeZone: "Asia/Jakarta",
    year: "numeric",
    month: "2-digit",
  }).formatToParts(new Date());
  const year = Number(parts.find((part) => part.type === "year").value);
  const month = Number(parts.find((part) => part.type === "month").value);
  const lastDay = new Date(Date.UTC(year, month, 0)).getUTCDate();
  return {
    from: `${year}-${String(month).padStart(2, "0")}-01`,
    to: `${year}-${String(month).padStart(2, "0")}-${lastDay}`,
  };
}

async function main() {
  const id = randomUUID();
  const auth = await request("/auth/register", null, {
    method: "POST",
    body: JSON.stringify({
      email: `qa-${id}@example.test`,
      password: `Qa!${id}aA1`,
      displayName: "Ledgerly QA",
    }),
  });

  const token = auth.accessToken;
  assert(token, "Registration did not return an access token.");

  const firstSeed = await request("/demo/seed", token, { method: "POST" });
  const secondSeed = await request("/demo/seed", token, { method: "POST" });
  assert(firstSeed.created === true, "The first demo seed was not created.");
  assert(secondSeed.created === false, "The demo seed is not idempotent.");

  const workspaceId = firstSeed.workspaceId;
  const period = jakartaPeriod();
  const [workspaces, transactions, customers, invoices, categories, imports, rules, report] =
    await Promise.all([
      request("/workspaces", token),
      request(`/workspaces/${workspaceId}/transactions`, token),
      request(`/workspaces/${workspaceId}/customers`, token),
      request(`/workspaces/${workspaceId}/invoices`, token),
      request(`/workspaces/${workspaceId}/categories`, token),
      request(`/workspaces/${workspaceId}/imports`, token),
      request(`/workspaces/${workspaceId}/categorization-rules`, token),
      request(`/workspaces/${workspaceId}/reports/financial-summary?from=${period.from}&to=${period.to}`, token),
    ]);

  assert(workspaces.some((workspace) => workspace.name === "Northstar Studio"), "Demo workspace is missing.");
  assert(transactions.totalItems >= 6, "Expected seeded transactions.");
  assert(customers.length >= 2, "Expected seeded customers.");
  assert(invoices.length >= 3, "Expected seeded invoices.");
  assert(categories.length >= 5, "Expected seeded categories.");
  assert(imports.some((item) => item.status === "CONFIRMED"), "Expected a confirmed statement import.");
  assert(rules.length >= 1, "Expected a categorization rule.");
  assert(Number(report.income) > 0 && Number(report.expenses) > 0, "Expected a populated financial report.");

  console.log("Ledgerly smoke test passed.");
  console.table({
    transactions: transactions.totalItems,
    customers: customers.length,
    invoices: invoices.length,
    categories: categories.length,
    statements: imports.length,
    rules: rules.length,
  });
}

main().catch((error) => {
  console.error(`Ledgerly smoke test failed: ${error.message}`);
  process.exitCode = 1;
});
