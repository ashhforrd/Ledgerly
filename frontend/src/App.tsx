import { useCallback, useEffect, useState } from "react";
import type { ComponentProps, FormEvent, ReactNode } from "react";
import {
  Archive,
  ArrowDownLeft,
  ArrowUpRight,
  CircleDollarSign,
  FileText,
  FileUp,
  LayoutDashboard,
  Loader2,
  LogOut,
  Plus,
  ReceiptText,
  RefreshCw,
  Send,
  Settings2,
  Tags,
  Users,
  WalletCards,
} from "lucide-react";
import { toast } from "sonner";
import { BrandMark } from "@/components/brand-mark";
import { DatePicker } from "@/components/date-picker";
import { FormSelect } from "@/components/form-select";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { Toaster } from "@/components/ui/sonner";
import { api, ApiError, money } from "@/lib/api";
import type {
  Category,
  Customer,
  Invoice,
  Report,
  Rule,
  Transaction,
  TransactionPage,
  Workspace,
} from "@/lib/api";

type Section =
  | "overview"
  | "transactions"
  | "invoices"
  | "customers"
  | "categories"
  | "imports"
  | "rules";
type ImportPreview = {
  id: string;
  fileName: string;
  status: string;
  totalRows: number;
  validRows: number;
  duplicateRows: number;
};

const today = new Date().toISOString().slice(0, 10);
const monthStart = `${today.slice(0, 7)}-01`;
const currencyOptions = ["IDR", "USD", "SGD", "AUD", "EUR", "GBP", "JPY"].map(
  (value) => ({ value, label: value }),
);
const timezoneOptions = [
  ["Asia/Jakarta", "Jakarta (UTC+7)"],
  ["Asia/Singapore", "Singapore (UTC+8)"],
  ["Asia/Tokyo", "Tokyo (UTC+9)"],
  ["Australia/Sydney", "Sydney"],
  ["UTC", "UTC"],
  ["Europe/London", "London"],
  ["America/New_York", "New York"],
].map(([value, label]) => ({ value, label }));

const navItems: { id: Section; label: string; icon: typeof LayoutDashboard }[] =
  [
    { id: "overview", label: "Overview", icon: LayoutDashboard },
    { id: "transactions", label: "Transactions", icon: WalletCards },
    { id: "invoices", label: "Invoices", icon: ReceiptText },
    { id: "customers", label: "Customers", icon: Users },
    { id: "categories", label: "Categories", icon: Tags },
    { id: "imports", label: "Statement import", icon: FileUp },
    { id: "rules", label: "Rules", icon: Settings2 },
  ];

function message(error: unknown) {
  return error instanceof Error
    ? error.message
    : "The request could not be completed.";
}

function App() {
  const [authenticated, setAuthenticated] = useState(
    Boolean(localStorage.getItem("ledgerly.accessToken")),
  );
  const [loading, setLoading] = useState(authenticated);
  const [workspaces, setWorkspaces] = useState<Workspace[]>([]);
  const [workspaceId, setWorkspaceId] = useState(
    localStorage.getItem("ledgerly.workspaceId") ?? "",
  );
  const [section, setSection] = useState<Section>("overview");

  const load = useCallback(async () => {
    setLoading(true);
    try {
      let seededWorkspaceId = "";
      try {
        const seeded = await api<{ workspaceId: string; created: boolean }>(
          "/demo/seed",
          { method: "POST" },
        );
        if (seeded.created) seededWorkspaceId = seeded.workspaceId;
      } catch (error) {
        if (!(error instanceof ApiError && error.status === 404)) throw error;
      }

      const values = await api<Workspace[]>("/workspaces");
      setWorkspaces(values);
      setWorkspaceId((current) => {
        const next = values.some(
          (workspace) => workspace.id === seededWorkspaceId,
        )
          ? seededWorkspaceId
          : values.some((workspace) => workspace.id === current)
            ? current
            : (values[0]?.id ?? "");
        if (next) localStorage.setItem("ledgerly.workspaceId", next);
        return next;
      });
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        localStorage.removeItem("ledgerly.accessToken");
        setAuthenticated(false);
      } else toast.error(message(error));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (authenticated) void load();
  }, [authenticated, load]);

  const logout = async () => {
    await api("/auth/logout", { method: "POST" }).catch(() => undefined);
    localStorage.removeItem("ledgerly.accessToken");
    localStorage.removeItem("ledgerly.workspaceId");
    setAuthenticated(false);
    setWorkspaces([]);
  };

  if (!authenticated)
    return (
      <>
        <AuthScreen onAuthenticated={() => setAuthenticated(true)} />
        <Toaster theme="dark" richColors />
      </>
    );
  if (loading) return <AppLoading />;
  const workspace = workspaces.find((item) => item.id === workspaceId);
  if (!workspace)
    return (
      <>
        <WorkspaceSetup onCreated={load} />
        <Toaster theme="dark" richColors />
      </>
    );

  return (
    <div className="min-h-screen bg-background lg:grid lg:grid-cols-[260px_minmax(0,1fr)]">
      <aside className="border-b bg-sidebar lg:sticky lg:top-0 lg:h-screen lg:border-r lg:border-b-0">
        <div className="flex h-20 items-center gap-3 px-5">
          <BrandMark />
          <div>
            <p className="font-bold tracking-tight">Ledgerly</p>
            <p className="text-xs text-muted-foreground">Business finance</p>
          </div>
        </div>
        <Separator />
        <div className="space-y-2 px-4 pt-4">
          <p className="px-1 text-[11px] font-semibold uppercase tracking-[.16em] text-muted-foreground">
            Workspace
          </p>
          <FormSelect
            value={workspaceId}
            onValueChange={(value) => {
              setWorkspaceId(value);
              localStorage.setItem("ledgerly.workspaceId", value);
            }}
            options={workspaces.map((item) => ({
              value: item.id,
              label: item.name,
            }))}
          />
          <p className="px-1 text-xs text-muted-foreground">
            {workspace.defaultCurrency} · {workspace.role}
          </p>
        </div>
        <nav className="grid grid-cols-2 gap-1 p-3 sm:grid-cols-4 lg:block lg:p-4">
          {navItems.map(({ id, label, icon: Icon }) => (
            <button
              type="button"
              key={id}
              className={`nav-item ${section === id ? "active" : ""}`}
              onClick={() => setSection(id)}
            >
              <Icon className="size-4" />
              <span>{label}</span>
            </button>
          ))}
        </nav>
      </aside>

      <div className="min-w-0">
        <header className="sticky top-0 z-30 flex h-20 items-center gap-4 border-b bg-background/85 px-5 backdrop-blur-xl md:px-8">
          <div className="flex min-w-0 flex-1 items-center gap-3">
            {(() => {
              const activeItem = navItems.find((item) => item.id === section);
              const Icon = activeItem?.icon;
              return (
                <>
                  {Icon && (
                    <div className="grid size-9 place-items-center rounded-lg border bg-card">
                      <Icon className="size-4 text-muted-foreground" />
                    </div>
                  )}
                  <h1 className="truncate text-lg font-semibold tracking-tight">
                    {activeItem?.label}
                  </h1>
                </>
              );
            })()}
          </div>
          <Button
            type="button"
            variant="ghost"
            size="icon"
            onClick={logout}
            aria-label="Sign out"
          >
            <LogOut />
          </Button>
        </header>
        <main className="mx-auto w-full max-w-[1440px] p-5 md:p-8 lg:p-10">
          {section === "overview" && <Overview workspace={workspace} />}
          {section === "transactions" && <Transactions workspace={workspace} />}
          {section === "invoices" && <Invoices workspace={workspace} />}
          {section === "customers" && <Customers workspace={workspace} />}
          {section === "categories" && <Categories workspace={workspace} />}
          {section === "imports" && <Imports workspace={workspace} />}
          {section === "rules" && <Rules workspace={workspace} />}
        </main>
      </div>
      <Toaster theme="dark" richColors />
    </div>
  );
}

function AuthScreen({ onAuthenticated }: { onAuthenticated: () => void }) {
  const [register, setRegister] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError("");
    setLoading(true);
    const form = event.currentTarget;
    try {
      const result = await api<{ accessToken: string }>(
        `/auth/${register ? "register" : "login"}`,
        {
          method: "POST",
          body: JSON.stringify(Object.fromEntries(new FormData(form))),
        },
      );
      localStorage.setItem("ledgerly.accessToken", result.accessToken);
      onAuthenticated();
    } catch (caught) {
      setError(message(caught));
    } finally {
      setLoading(false);
    }
  };
  return (
    <main className="auth-grid relative grid min-h-screen overflow-hidden lg:grid-cols-[1.1fr_.9fr]">
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_20%_20%,rgba(37,99,235,.18),transparent_35%),radial-gradient(circle_at_80%_80%,rgba(59,130,246,.08),transparent_30%)]" />
      <section className="relative hidden flex-col justify-between border-r p-12 lg:flex">
        <div className="flex items-center gap-3">
          <BrandMark />
          <span className="text-xl font-bold">Ledgerly</span>
        </div>
        <div className="max-w-xl">
          <Badge className="mb-5 bg-blue-500/10 text-blue-300 hover:bg-blue-500/10">
            Financial operations, clearly organized
          </Badge>
          <h1 className="text-5xl font-bold leading-[1.08] tracking-[-.035em]">
            Know where the business stands.
          </h1>
          <p className="mt-5 max-w-lg text-lg leading-8 text-muted-foreground">
            Track cash movement, issue invoices, import statements, and keep
            reporting decisions grounded in real ledger data.
          </p>
          <div className="mt-10 grid gap-4 sm:grid-cols-3">
            {[
              ["Transactions", "A reliable ledger"],
              ["Invoices", "Receivables in view"],
              ["Reports", "Decision-ready totals"],
            ].map(([title, copy]) => (
              <div
                key={title}
                className="rounded-xl border bg-card/60 p-4 backdrop-blur"
              >
                <p className="font-semibold">{title}</p>
                <p className="mt-1 text-sm text-muted-foreground">{copy}</p>
              </div>
            ))}
          </div>
        </div>
        <p className="text-xs text-muted-foreground">
          Built for focused financial work.
        </p>
      </section>
      <section className="relative grid place-items-center p-6 md:p-12">
        <Card className="w-full max-w-md border-white/10 bg-card/90 shadow-2xl shadow-black/30 backdrop-blur-xl">
          <CardHeader className="space-y-3 pb-2">
            <div className="mb-2 flex items-center gap-3 lg:hidden">
              <BrandMark className="size-9" />
              <span className="font-bold">Ledgerly</span>
            </div>
            <CardTitle className="text-2xl">
              {register ? "Create your account" : "Welcome back"}
            </CardTitle>
            <p className="text-sm text-muted-foreground">
              {register
                ? "Start with a secure business workspace."
                : "Sign in to continue to your workspace."}
            </p>
          </CardHeader>
          <CardContent className="pt-5">
            <form className="space-y-4" onSubmit={submit}>
              {register && (
                <Field
                  name="displayName"
                  label="Display name"
                  autoComplete="name"
                  required
                />
              )}
              <Field
                name="email"
                label="Email address"
                type="email"
                autoComplete="email"
                required
              />
              <Field
                name="password"
                label="Password"
                type="password"
                minLength={12}
                autoComplete={register ? "new-password" : "current-password"}
                required
              />
              {register && (
                <p className="text-xs text-muted-foreground">
                  Use at least 12 characters.
                </p>
              )}
              {error && (
                <p className="error" role="alert">
                  {error}
                </p>
              )}
              <Button
                type="submit"
                className="h-10 w-full bg-blue-500 hover:bg-blue-400"
                disabled={loading}
              >
                {loading && <Loader2 className="animate-spin" />}
                {register ? "Create account" : "Sign in"}
              </Button>
              <Button
                type="button"
                variant="ghost"
                className="h-10 w-full"
                onClick={() => {
                  setRegister(!register);
                  setError("");
                }}
              >
                {register
                  ? "Already have an account? Sign in"
                  : "New to Ledgerly? Create account"}
              </Button>
            </form>
          </CardContent>
        </Card>
      </section>
    </main>
  );
}

function WorkspaceSetup({ onCreated }: { onCreated: () => Promise<void> }) {
  const [loading, setLoading] = useState(false);
  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    const form = event.currentTarget;
    try {
      await api("/workspaces", {
        method: "POST",
        body: JSON.stringify(Object.fromEntries(new FormData(form))),
      });
      toast.success("Workspace created");
      await onCreated();
    } catch (error) {
      toast.error(message(error));
    } finally {
      setLoading(false);
    }
  };
  return (
    <main className="auth-grid grid min-h-screen place-items-center p-6">
      <Card className="w-full max-w-xl shadow-2xl shadow-black/20">
        <CardHeader>
          <div className="mb-5 flex items-center gap-3">
            <BrandMark />
            <strong>Ledgerly</strong>
          </div>
          <CardTitle className="text-2xl">Set up your workspace</CardTitle>
          <p className="text-sm text-muted-foreground">
            These settings define how amounts and dates appear across reports.
          </p>
        </CardHeader>
        <CardContent>
          <form className="space-y-5" onSubmit={submit}>
            <Field name="name" label="Business name" required />
            <div className="grid gap-4 sm:grid-cols-2">
              <FormSelect
                name="defaultCurrency"
                label="Currency"
                options={currencyOptions}
                defaultValue="IDR"
                required
              />
              <FormSelect
                name="timezone"
                label="Timezone"
                options={timezoneOptions}
                defaultValue="Asia/Jakarta"
                required
              />
            </div>
            <Button
              type="submit"
              className="h-10 w-full bg-blue-500 hover:bg-blue-400"
              disabled={loading}
            >
              {loading && <Loader2 className="animate-spin" />}Create workspace
            </Button>
          </form>
        </CardContent>
      </Card>
    </main>
  );
}

function AppLoading() {
  return (
    <div className="grid min-h-screen lg:grid-cols-[260px_1fr]">
      <aside className="border-r bg-sidebar p-5">
        <Skeleton className="h-10 w-36" />
        <div className="mt-14 space-y-3">
          {Array.from({ length: 7 }).map((_, index) => (
            <Skeleton key={index} className="h-10 w-full" />
          ))}
        </div>
      </aside>
      <main className="p-10">
        <Skeleton className="h-8 w-56" />
        <div className="mt-8 grid gap-4 md:grid-cols-3">
          {Array.from({ length: 3 }).map((_, index) => (
            <Skeleton key={index} className="h-32" />
          ))}
        </div>
      </main>
    </div>
  );
}

function Field({
  label,
  required = false,
  ...input
}: ComponentProps<typeof Input> & { label: string }) {
  return (
    <div className="space-y-2">
      <Label htmlFor={input.name}>{label}</Label>
      <Input id={input.name} className="h-10" required={required} {...input} />
    </div>
  );
}

function Heading({
  title,
  description,
  action,
}: {
  title: string;
  description: string;
  action?: ReactNode;
}) {
  return (
    <header className="mb-7 flex flex-col justify-between gap-4 sm:flex-row sm:items-start">
      <div>
        <h1 className="text-2xl font-bold tracking-tight md:text-3xl">
          {title}
        </h1>
        <p className="mt-2 text-sm text-muted-foreground">{description}</p>
      </div>
      {action}
    </header>
  );
}

function Empty({
  icon: Icon = FileText,
  title,
  copy,
}: {
  icon?: typeof FileText;
  title: string;
  copy: string;
}) {
  return (
    <div className="grid place-items-center rounded-xl border border-dashed bg-muted/10 px-6 py-14 text-center">
      <div className="grid size-11 place-items-center rounded-xl bg-muted">
        <Icon className="size-5 text-muted-foreground" />
      </div>
      <p className="mt-4 font-semibold">{title}</p>
      <p className="mt-1 max-w-sm text-sm text-muted-foreground">{copy}</p>
    </div>
  );
}

function Metric({
  label,
  value,
  icon: Icon,
  tone,
}: {
  label: string;
  value: string;
  icon: typeof CircleDollarSign;
  tone: "blue" | "green" | "amber";
}) {
  const tones = {
    blue: "bg-blue-500/10 text-blue-300",
    green: "bg-emerald-500/10 text-emerald-300",
    amber: "bg-amber-500/10 text-amber-300",
  };
  return (
    <Card className="py-0">
      <CardContent className="flex items-center justify-between px-5 py-4 md:px-6">
        <div>
          <p className="text-sm text-muted-foreground">{label}</p>
          <p className="mt-1.5 text-2xl font-bold tabular-nums tracking-tight">
            {value}
          </p>
        </div>
        <div
          className={`grid size-10 place-items-center rounded-xl ${tones[tone]}`}
        >
          <Icon className="size-5" />
        </div>
      </CardContent>
    </Card>
  );
}

function StatusBadge({ status }: { status: string }) {
  const style =
    status === "PAID" || status === "POSTED" || status === "CONFIRMED"
      ? "border-emerald-400/20 bg-emerald-400/10 text-emerald-300"
      : status === "VOID" || status === "VOIDED"
        ? "border-red-400/20 bg-red-400/10 text-red-300"
        : status === "SENT"
          ? "border-blue-400/20 bg-blue-400/10 text-blue-300"
          : "";
  return (
    <Badge variant="outline" className={style}>
      {status}
    </Badge>
  );
}

function Overview({ workspace }: { workspace: Workspace }) {
  const [report, setReport] = useState<Report | null>(null);
  const [loading, setLoading] = useState(true);
  const load = useCallback(async () => {
    setLoading(true);
    try {
      setReport(
        await api(
          `/workspaces/${workspace.id}/reports/financial-summary?from=${monthStart}&to=${today}`,
        ),
      );
    } catch (error) {
      toast.error(message(error));
    } finally {
      setLoading(false);
    }
  }, [workspace.id]);
  useEffect(() => {
    void load();
  }, [load]);
  return (
    <>
      <Heading
        title="Financial overview"
        description={`${monthStart} through ${today} · posted transactions only`}
        action={
          <Button
            type="button"
            variant="outline"
            onClick={load}
            disabled={loading}
          >
            <RefreshCw className={loading ? "animate-spin" : ""} />
            Refresh
          </Button>
        }
      />
      {loading ? (
        <div className="grid gap-4 md:grid-cols-3">
          {Array.from({ length: 3 }).map((_, index) => (
            <Skeleton key={index} className="h-32" />
          ))}
        </div>
      ) : (
        report && (
          <>
            <div className="grid gap-4 md:grid-cols-3">
              <Metric
                label="Income"
                value={money(report.income, report.currency)}
                icon={ArrowDownLeft}
                tone="green"
              />
              <Metric
                label="Expenses"
                value={money(report.expenses, report.currency)}
                icon={ArrowUpRight}
                tone="amber"
              />
              <Metric
                label="Net cash flow"
                value={money(report.netCashFlow, report.currency)}
                icon={CircleDollarSign}
                tone="blue"
              />
            </div>
            <Card className="mt-6 overflow-hidden">
              <CardHeader>
                <div className="flex items-center justify-between">
                  <CardTitle>Category totals</CardTitle>
                  <Badge variant="secondary">
                    {report.transactionCount} transactions
                  </Badge>
                </div>
              </CardHeader>
              <CardContent className="p-0">
                {report.categories.length === 0 ? (
                  <div className="p-6">
                    <Empty
                      title="No activity this period"
                      copy="Posted transactions will appear here as category totals."
                    />
                  </div>
                ) : (
                  <div className="overflow-x-auto">
                    <table className="data-table">
                      <thead>
                        <tr>
                          <th>Category</th>
                          <th>Type</th>
                          <th className="text-right">Total</th>
                        </tr>
                      </thead>
                      <tbody>
                        {report.categories.map((item) => (
                          <tr key={`${item.type}-${item.category}`}>
                            <td className="font-medium">{item.category}</td>
                            <td>
                              <StatusBadge status={item.type} />
                            </td>
                            <td className="text-right font-medium tabular-nums">
                              {money(item.amount, report.currency)}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </CardContent>
            </Card>
          </>
        )
      )}
    </>
  );
}

function Transactions({ workspace }: { workspace: Workspace }) {
  const [data, setData] = useState<TransactionPage | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [voidTarget, setVoidTarget] = useState<Transaction | null>(null);
  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [transactions, categoryValues] = await Promise.all([
        api<TransactionPage>(
          `/workspaces/${workspace.id}/transactions?size=100`,
        ),
        api<Category[]>(`/workspaces/${workspace.id}/categories`),
      ]);
      setData(transactions);
      setCategories(categoryValues);
    } catch (error) {
      toast.error(message(error));
    } finally {
      setLoading(false);
    }
  }, [workspace.id]);
  useEffect(() => {
    void load();
  }, [load]);
  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const form = event.currentTarget;
    const raw = Object.fromEntries(new FormData(form));
    setSaving(true);
    try {
      await api(`/workspaces/${workspace.id}/transactions`, {
        method: "POST",
        body: JSON.stringify({
          ...raw,
          amount: Number(raw.amount),
          categoryId:
            raw.categoryId === "uncategorized" ? null : raw.categoryId,
        }),
      });
      toast.success("Transaction recorded");
      form.reset();
      await load();
    } catch (error) {
      toast.error(message(error));
    } finally {
      setSaving(false);
    }
  };
  return (
    <>
      <Heading
        title="Transactions"
        description="Record real cash movement. Voided entries remain visible for auditability."
      />
      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="text-base">New transaction</CardTitle>
        </CardHeader>
        <CardContent>
          <form className="form-grid" onSubmit={submit}>
            <FormSelect
              name="type"
              label="Type"
              defaultValue="INCOME"
              options={[
                { value: "INCOME", label: "Income" },
                { value: "EXPENSE", label: "Expense" },
              ]}
              required
            />
            <Field name="description" label="Description" required />
            <Field
              name="amount"
              label="Amount"
              type="number"
              min="0.0001"
              step="0.0001"
              required
            />
            <DatePicker
              name="occurredOn"
              label="Date"
              defaultValue={today}
              required
            />
            <FormSelect
              name="categoryId"
              label="Category"
              defaultValue="uncategorized"
              options={[
                { value: "uncategorized", label: "Uncategorized" },
                ...categories.map((item) => ({
                  value: item.id,
                  label: `${item.type} · ${item.name}`,
                })),
              ]}
            />
            <Button
              type="submit"
              className="h-10 bg-blue-500 hover:bg-blue-400"
              disabled={saving}
            >
              {saving ? <Loader2 className="animate-spin" /> : <Plus />}Add
              transaction
            </Button>
          </form>
        </CardContent>
      </Card>
      <Card className="overflow-hidden">
        <CardContent className="p-0">
          {loading ? (
            <div className="p-6">
              <Skeleton className="h-56" />
            </div>
          ) : !data?.items.length ? (
            <div className="p-6">
              <Empty
                icon={WalletCards}
                title="No transactions yet"
                copy="Add the first income or expense to start the ledger."
              />
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Description</th>
                    <th>Category</th>
                    <th>Status</th>
                    <th className="text-right">Amount</th>
                    <th />
                  </tr>
                </thead>
                <tbody>
                  {data.items.map((item) => (
                    <tr key={item.id}>
                      <td className="whitespace-nowrap text-muted-foreground">
                        {item.occurredOn}
                      </td>
                      <td className="font-medium">{item.description}</td>
                      <td>{item.categoryName ?? "Uncategorized"}</td>
                      <td>
                        <StatusBadge status={item.status} />
                      </td>
                      <td
                        className={`text-right font-semibold tabular-nums ${item.type === "EXPENSE" ? "text-amber-300" : "text-emerald-300"}`}
                      >
                        {item.type === "EXPENSE" ? "−" : "+"}
                        {money(item.amount, item.currency)}
                      </td>
                      <td className="text-right">
                        {item.status === "POSTED" && (
                          <Button
                            type="button"
                            size="sm"
                            variant="ghost"
                            onClick={() => setVoidTarget(item)}
                          >
                            Void
                          </Button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>
      <VoidTransactionDialog
        workspace={workspace}
        transaction={voidTarget}
        onOpenChange={(open) => {
          if (!open) setVoidTarget(null);
        }}
        onDone={load}
      />
    </>
  );
}

function VoidTransactionDialog({
  workspace,
  transaction,
  onOpenChange,
  onDone,
}: {
  workspace: Workspace;
  transaction: Transaction | null;
  onOpenChange: (open: boolean) => void;
  onDone: () => Promise<void>;
}) {
  const [loading, setLoading] = useState(false);
  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!transaction) return;
    setLoading(true);
    try {
      const raw = Object.fromEntries(new FormData(event.currentTarget));
      await api(
        `/workspaces/${workspace.id}/transactions/${transaction.id}/void`,
        { method: "POST", body: JSON.stringify(raw) },
      );
      toast.success("Transaction voided");
      onOpenChange(false);
      await onDone();
    } catch (error) {
      toast.error(message(error));
    } finally {
      setLoading(false);
    }
  };
  return (
    <Dialog open={Boolean(transaction)} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Void transaction</DialogTitle>
          <DialogDescription>
            This keeps the record in the ledger and excludes it from posted
            totals.
          </DialogDescription>
        </DialogHeader>
        <form id="void-transaction" onSubmit={submit}>
          <Field name="reason" label="Reason" minLength={3} required />
        </form>
        <DialogFooter>
          <Button
            type="button"
            variant="outline"
            onClick={() => onOpenChange(false)}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            form="void-transaction"
            variant="destructive"
            disabled={loading}
          >
            {loading && <Loader2 className="animate-spin" />}Void transaction
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

function Customers({ workspace }: { workspace: Workspace }) {
  const [items, setItems] = useState<Customer[]>([]),
    [loading, setLoading] = useState(true),
    [saving, setSaving] = useState(false),
    [archiveTarget, setArchiveTarget] = useState<Customer | null>(null);
  const load = useCallback(async () => {
    setLoading(true);
    try {
      setItems(await api(`/workspaces/${workspace.id}/customers`));
    } catch (error) {
      toast.error(message(error));
    } finally {
      setLoading(false);
    }
  }, [workspace.id]);
  useEffect(() => {
    void load();
  }, [load]);
  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const form = event.currentTarget;
    setSaving(true);
    try {
      await api(`/workspaces/${workspace.id}/customers`, {
        method: "POST",
        body: JSON.stringify(Object.fromEntries(new FormData(form))),
      });
      toast.success("Customer created");
      form.reset();
      await load();
    } catch (error) {
      toast.error(message(error));
    } finally {
      setSaving(false);
    }
  };
  const archive = async () => {
    if (!archiveTarget) return;
    setSaving(true);
    try {
      await api(
        `/workspaces/${workspace.id}/customers/${archiveTarget.id}/archive`,
        { method: "POST" },
      );
      toast.success("Customer archived");
      setArchiveTarget(null);
      await load();
    } catch (error) {
      toast.error(message(error));
    } finally {
      setSaving(false);
    }
  };
  return (
    <>
      <Heading
        title="Customers"
        description="Billing identities used to issue invoices and track receivables."
      />
      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="text-base">New customer</CardTitle>
        </CardHeader>
        <CardContent>
          <form className="form-grid" onSubmit={submit}>
            <Field name="name" label="Name" required />
            <Field name="email" label="Email" type="email" />
            <Field name="phone" label="Phone" />
            <Field name="billingAddress" label="Billing address" />
            <Button
              type="submit"
              className="h-10 bg-blue-500 hover:bg-blue-400"
              disabled={saving}
            >
              {saving ? <Loader2 className="animate-spin" /> : <Plus />}Add
              customer
            </Button>
          </form>
        </CardContent>
      </Card>
      <Card className="overflow-hidden">
        <CardContent className="p-0">
          {loading ? (
            <div className="p-6">
              <Skeleton className="h-44" />
            </div>
          ) : items.length === 0 ? (
            <div className="p-6">
              <Empty
                icon={Users}
                title="No customers"
                copy="Create a customer before issuing the first invoice."
              />
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th />
                  </tr>
                </thead>
                <tbody>
                  {items.map((item) => (
                    <tr key={item.id}>
                      <td className="font-medium">{item.name}</td>
                      <td>{item.email ?? "—"}</td>
                      <td>{item.phone ?? "—"}</td>
                      <td className="text-right">
                        <Button
                          type="button"
                          size="sm"
                          variant="ghost"
                          onClick={() => setArchiveTarget(item)}
                        >
                          <Archive />
                          Archive
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>
      <Dialog
        open={Boolean(archiveTarget)}
        onOpenChange={(open) => {
          if (!open) setArchiveTarget(null);
        }}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Archive customer?</DialogTitle>
            <DialogDescription>
              {archiveTarget?.name} will no longer appear as an active billing
              customer. Existing invoices remain unchanged.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => setArchiveTarget(null)}
            >
              Cancel
            </Button>
            <Button
              type="button"
              variant="destructive"
              onClick={archive}
              disabled={saving}
            >
              {saving && <Loader2 className="animate-spin" />}Archive
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}

function Invoices({ workspace }: { workspace: Workspace }) {
  const [items, setItems] = useState<Invoice[]>([]),
    [customers, setCustomers] = useState<Customer[]>([]),
    [loading, setLoading] = useState(true),
    [saving, setSaving] = useState(false),
    [payTarget, setPayTarget] = useState<Invoice | null>(null),
    [voidTarget, setVoidTarget] = useState<Invoice | null>(null);
  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [invoiceValues, customerValues] = await Promise.all([
        api<Invoice[]>(`/workspaces/${workspace.id}/invoices`),
        api<Customer[]>(`/workspaces/${workspace.id}/customers`),
      ]);
      setItems(invoiceValues);
      setCustomers(customerValues);
    } catch (error) {
      toast.error(message(error));
    } finally {
      setLoading(false);
    }
  }, [workspace.id]);
  useEffect(() => {
    void load();
  }, [load]);
  const create = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const form = event.currentTarget;
    const raw = Object.fromEntries(new FormData(form));
    setSaving(true);
    try {
      await api(`/workspaces/${workspace.id}/invoices`, {
        method: "POST",
        body: JSON.stringify({
          customerId: raw.customerId,
          invoiceNumber: raw.invoiceNumber,
          issueDate: raw.issueDate,
          dueDate: raw.dueDate,
          notes: raw.notes || null,
          lines: [
            {
              description: raw.description,
              quantity: Number(raw.quantity),
              unitPrice: Number(raw.unitPrice),
            },
          ],
        }),
      });
      toast.success("Invoice draft created");
      form.reset();
      await load();
    } catch (error) {
      toast.error(message(error));
    } finally {
      setSaving(false);
    }
  };
  const send = async (id: string) => {
    setSaving(true);
    try {
      await api(`/workspaces/${workspace.id}/invoices/${id}/send`, {
        method: "POST",
      });
      toast.success("Invoice marked as sent");
      await load();
    } catch (error) {
      toast.error(message(error));
    } finally {
      setSaving(false);
    }
  };
  const voidInvoice = async () => {
    if (!voidTarget) return;
    setSaving(true);
    try {
      await api(`/workspaces/${workspace.id}/invoices/${voidTarget.id}/void`, {
        method: "POST",
      });
      toast.success("Invoice voided");
      setVoidTarget(null);
      await load();
    } catch (error) {
      toast.error(message(error));
    } finally {
      setSaving(false);
    }
  };
  return (
    <>
      <Heading
        title="Invoices"
        description="Create receivables, mark delivery, and record actual payments."
      />
      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="text-base">New invoice</CardTitle>
        </CardHeader>
        <CardContent>
          {customers.length === 0 ? (
            <Empty
              icon={Users}
              title="A customer is required"
              copy="Create a customer first, then return here to issue an invoice."
            />
          ) : (
            <form className="form-grid" onSubmit={create}>
              <FormSelect
                name="customerId"
                label="Customer"
                placeholder="Choose customer"
                options={customers.map((item) => ({
                  value: item.id,
                  label: item.name,
                }))}
                required
              />
              <Field name="invoiceNumber" label="Invoice number" required />
              <DatePicker
                name="issueDate"
                label="Issue date"
                defaultValue={today}
                required
              />
              <DatePicker name="dueDate" label="Due date" required />
              <Field name="description" label="Line description" required />
              <Field
                name="quantity"
                label="Quantity"
                type="number"
                step="0.0001"
                min="0.0001"
                required
              />
              <Field
                name="unitPrice"
                label="Unit price"
                type="number"
                step="0.0001"
                min="0"
                required
              />
              <Field name="notes" label="Notes (optional)" />
              <Button
                type="submit"
                className="h-10 bg-blue-500 hover:bg-blue-400"
                disabled={saving}
              >
                {saving ? <Loader2 className="animate-spin" /> : <Plus />}Create
                draft
              </Button>
            </form>
          )}
        </CardContent>
      </Card>
      <Card className="overflow-hidden">
        <CardContent className="p-0">
          {loading ? (
            <div className="p-6">
              <Skeleton className="h-52" />
            </div>
          ) : items.length === 0 ? (
            <div className="p-6">
              <Empty
                icon={ReceiptText}
                title="No invoices"
                copy="Draft invoices will appear here with their payment status."
              />
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Invoice</th>
                    <th>Customer</th>
                    <th>Due date</th>
                    <th>Status</th>
                    <th className="text-right">Outstanding</th>
                    <th />
                  </tr>
                </thead>
                <tbody>
                  {items.map((item) => (
                    <tr key={item.id}>
                      <td className="font-medium">{item.invoiceNumber}</td>
                      <td>{item.customerName}</td>
                      <td>
                        {item.dueDate}
                        {item.overdue && (
                          <span className="ml-2 text-xs text-red-300">
                            Overdue
                          </span>
                        )}
                      </td>
                      <td>
                        <StatusBadge status={item.status} />
                      </td>
                      <td className="text-right font-semibold tabular-nums">
                        {money(item.outstanding, item.currency)}
                      </td>
                      <td>
                        <div className="flex justify-end gap-1">
                          {item.status === "DRAFT" && (
                            <Button
                              type="button"
                              size="sm"
                              variant="outline"
                              disabled={saving}
                              onClick={() => send(item.id)}
                            >
                              <Send />
                              Send
                            </Button>
                          )}
                          {item.status === "SENT" && (
                            <Button
                              type="button"
                              size="sm"
                              variant="outline"
                              onClick={() => setPayTarget(item)}
                            >
                              Record payment
                            </Button>
                          )}
                          {!["PAID", "VOIDED"].includes(item.status) && (
                            <Button
                              type="button"
                              size="sm"
                              variant="ghost"
                              onClick={() => setVoidTarget(item)}
                            >
                              Void
                            </Button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>
      <PaymentDialog
        workspace={workspace}
        invoice={payTarget}
        onOpenChange={(open) => {
          if (!open) setPayTarget(null);
        }}
        onDone={load}
      />
      <Dialog
        open={Boolean(voidTarget)}
        onOpenChange={(open) => {
          if (!open) setVoidTarget(null);
        }}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Void invoice?</DialogTitle>
            <DialogDescription>
              {voidTarget?.invoiceNumber} will remain visible for audit history
              but cannot receive payments.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => setVoidTarget(null)}
            >
              Cancel
            </Button>
            <Button
              type="button"
              variant="destructive"
              onClick={voidInvoice}
              disabled={saving}
            >
              {saving && <Loader2 className="animate-spin" />}Void invoice
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}

function PaymentDialog({
  workspace,
  invoice,
  onOpenChange,
  onDone,
}: {
  workspace: Workspace;
  invoice: Invoice | null;
  onOpenChange: (open: boolean) => void;
  onDone: () => Promise<void>;
}) {
  const [loading, setLoading] = useState(false);
  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!invoice) return;
    const raw = Object.fromEntries(new FormData(event.currentTarget));
    setLoading(true);
    try {
      await api(`/workspaces/${workspace.id}/invoices/${invoice.id}/payments`, {
        method: "POST",
        body: JSON.stringify({
          amount: Number(raw.amount),
          paidOn: raw.paidOn,
          reference: raw.reference || null,
        }),
      });
      toast.success("Payment recorded");
      onOpenChange(false);
      await onDone();
    } catch (error) {
      toast.error(message(error));
    } finally {
      setLoading(false);
    }
  };
  return (
    <Dialog open={Boolean(invoice)} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Record payment</DialogTitle>
          <DialogDescription>
            Outstanding balance:{" "}
            {invoice && money(invoice.outstanding, invoice.currency)}
          </DialogDescription>
        </DialogHeader>
        <form id="record-payment" className="space-y-4" onSubmit={submit}>
          <Field
            name="amount"
            label="Amount"
            type="number"
            min="0.0001"
            max={invoice?.outstanding}
            step="0.0001"
            required
          />
          <DatePicker
            name="paidOn"
            label="Payment date"
            defaultValue={today}
            required
          />
          <Field name="reference" label="Reference (optional)" />
        </form>
        <DialogFooter>
          <Button
            type="button"
            variant="outline"
            onClick={() => onOpenChange(false)}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            form="record-payment"
            className="bg-blue-500 hover:bg-blue-400"
            disabled={loading}
          >
            {loading && <Loader2 className="animate-spin" />}Record payment
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

function Categories({ workspace }: { workspace: Workspace }) {
  const [items, setItems] = useState<Category[]>([]),
    [loading, setLoading] = useState(true),
    [saving, setSaving] = useState(false);
  const load = useCallback(async () => {
    setLoading(true);
    try {
      setItems(
        await api(
          `/workspaces/${workspace.id}/categories?includeArchived=true`,
        ),
      );
    } catch (error) {
      toast.error(message(error));
    } finally {
      setLoading(false);
    }
  }, [workspace.id]);
  useEffect(() => {
    void load();
  }, [load]);
  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const form = event.currentTarget;
    setSaving(true);
    try {
      await api(`/workspaces/${workspace.id}/categories`, {
        method: "POST",
        body: JSON.stringify(Object.fromEntries(new FormData(form))),
      });
      toast.success("Category created");
      form.reset();
      await load();
    } catch (error) {
      toast.error(message(error));
    } finally {
      setSaving(false);
    }
  };
  const archive = async (id: string) => {
    setSaving(true);
    try {
      await api(`/workspaces/${workspace.id}/categories/${id}/archive`, {
        method: "POST",
      });
      toast.success("Category archived");
      await load();
    } catch (error) {
      toast.error(message(error));
    } finally {
      setSaving(false);
    }
  };
  return (
    <>
      <Heading
        title="Categories"
        description="Maintain the reporting structure used by transactions and rules."
      />
      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="text-base">New category</CardTitle>
        </CardHeader>
        <CardContent>
          <form className="form-grid" onSubmit={submit}>
            <Field name="name" label="Name" required />
            <FormSelect
              name="type"
              label="Type"
              defaultValue="INCOME"
              options={[
                { value: "INCOME", label: "Income" },
                { value: "EXPENSE", label: "Expense" },
              ]}
              required
            />
            <Button
              type="submit"
              className="h-10 bg-blue-500 hover:bg-blue-400"
              disabled={saving}
            >
              {saving ? <Loader2 className="animate-spin" /> : <Plus />}Add
              category
            </Button>
          </form>
        </CardContent>
      </Card>
      <Card className="overflow-hidden">
        <CardContent className="p-0">
          {loading ? (
            <div className="p-6">
              <Skeleton className="h-40" />
            </div>
          ) : items.length === 0 ? (
            <div className="p-6">
              <Empty
                icon={Tags}
                title="No categories"
                copy="Create income and expense categories for accurate reporting."
              />
            </div>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Type</th>
                  <th>Status</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {items.map((item) => (
                  <tr key={item.id}>
                    <td className="font-medium">{item.name}</td>
                    <td>{item.type}</td>
                    <td>
                      <Badge variant={item.active ? "secondary" : "outline"}>
                        {item.active ? "Active" : "Archived"}
                      </Badge>
                    </td>
                    <td className="text-right">
                      {item.active && (
                        <Button
                          type="button"
                          size="sm"
                          variant="ghost"
                          disabled={saving}
                          onClick={() => archive(item.id)}
                        >
                          <Archive />
                          Archive
                        </Button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </CardContent>
      </Card>
    </>
  );
}

function Imports({ workspace }: { workspace: Workspace }) {
  const [preview, setPreview] = useState<ImportPreview | null>(null),
    [loading, setLoading] = useState(false);
  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    try {
      setPreview(
        await api(`/workspaces/${workspace.id}/imports/preview`, {
          method: "POST",
          body: new FormData(event.currentTarget),
        }),
      );
      toast.success("Statement preview ready");
    } catch (error) {
      toast.error(message(error));
    } finally {
      setLoading(false);
    }
  };
  const confirm = async () => {
    if (!preview) return;
    setLoading(true);
    try {
      setPreview(
        await api(`/workspaces/${workspace.id}/imports/${preview.id}/confirm`, {
          method: "POST",
        }),
      );
      toast.success("Statement imported into the ledger");
    } catch (error) {
      toast.error(message(error));
    } finally {
      setLoading(false);
    }
  };
  return (
    <>
      <Heading
        title="Statement import"
        description="Validate CSV rows before they create ledger transactions."
      />
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Upload CSV statement</CardTitle>
        </CardHeader>
        <CardContent>
          <form
            className="flex flex-col gap-4 sm:flex-row sm:items-end"
            onSubmit={submit}
          >
            <div className="flex-1">
              <Field
                name="file"
                label="CSV file"
                type="file"
                accept=".csv,text/csv"
                required
              />
            </div>
            <Button
              type="submit"
              className="h-10 bg-blue-500 hover:bg-blue-400"
              disabled={loading}
            >
              {loading ? <Loader2 className="animate-spin" /> : <FileUp />}
              Preview statement
            </Button>
          </form>
          {preview && (
            <div className="mt-6 rounded-xl border bg-muted/15 p-5">
              <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
                <div>
                  <div className="flex items-center gap-2">
                    <FileText className="size-4 text-blue-300" />
                    <p className="font-medium">{preview.fileName}</p>
                  </div>
                  <p className="mt-2 text-sm text-muted-foreground">
                    {preview.validRows} valid · {preview.duplicateRows}{" "}
                    duplicates · {preview.totalRows} total rows
                  </p>
                </div>
                <div className="flex items-center gap-3">
                  <StatusBadge status={preview.status} />
                  <Button
                    type="button"
                    onClick={confirm}
                    disabled={loading || preview.status === "CONFIRMED"}
                  >
                    {loading && <Loader2 className="animate-spin" />}
                    {preview.status === "CONFIRMED"
                      ? "Imported"
                      : "Confirm import"}
                  </Button>
                </div>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </>
  );
}

function Rules({ workspace }: { workspace: Workspace }) {
  const [items, setItems] = useState<Rule[]>([]),
    [categories, setCategories] = useState<Category[]>([]),
    [loading, setLoading] = useState(true),
    [saving, setSaving] = useState(false);
  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [ruleValues, categoryValues] = await Promise.all([
        api<Rule[]>(`/workspaces/${workspace.id}/categorization-rules`),
        api<Category[]>(`/workspaces/${workspace.id}/categories`),
      ]);
      setItems(ruleValues);
      setCategories(categoryValues);
    } catch (error) {
      toast.error(message(error));
    } finally {
      setLoading(false);
    }
  }, [workspace.id]);
  useEffect(() => {
    void load();
  }, [load]);
  const create = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const form = event.currentTarget;
    const raw = Object.fromEntries(new FormData(form));
    const category = categories.find((item) => item.id === raw.categoryId);
    setSaving(true);
    try {
      await api(`/workspaces/${workspace.id}/categorization-rules`, {
        method: "POST",
        body: JSON.stringify({
          ...raw,
          type: category?.type,
          priority: Number(raw.priority),
        }),
      });
      toast.success("Rule created");
      form.reset();
      await load();
    } catch (error) {
      toast.error(message(error));
    } finally {
      setSaving(false);
    }
  };
  const apply = async () => {
    setSaving(true);
    try {
      const result = await api<{ categorizedTransactions: number }>(
        `/workspaces/${workspace.id}/categorization-rules/apply`,
        { method: "POST" },
      );
      toast.success(
        `${result.categorizedTransactions} transactions categorized`,
      );
    } catch (error) {
      toast.error(message(error));
    } finally {
      setSaving(false);
    }
  };
  const disable = async (id: string) => {
    setSaving(true);
    try {
      await api(
        `/workspaces/${workspace.id}/categorization-rules/${id}/disable`,
        { method: "POST" },
      );
      toast.success("Rule disabled");
      await load();
    } catch (error) {
      toast.error(message(error));
    } finally {
      setSaving(false);
    }
  };
  return (
    <>
      <Heading
        title="Categorization rules"
        description="Apply consistent categories to matching transaction descriptions."
        action={
          <Button
            type="button"
            variant="outline"
            onClick={apply}
            disabled={saving}
          >
            <RefreshCw className={saving ? "animate-spin" : ""} />
            Apply rules
          </Button>
        }
      />
      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="text-base">New rule</CardTitle>
        </CardHeader>
        <CardContent>
          <form className="form-grid" onSubmit={create}>
            <Field name="name" label="Rule name" required />
            <Field
              name="descriptionContains"
              label="Description contains"
              required
            />
            <Field
              name="priority"
              label="Priority"
              type="number"
              min="1"
              max="1000"
              required
            />
            <FormSelect
              name="categoryId"
              label="Category"
              options={categories.map((item) => ({
                value: item.id,
                label: `${item.type} · ${item.name}`,
              }))}
              placeholder="Choose category"
              required
            />
            <Button
              type="submit"
              className="h-10 bg-blue-500 hover:bg-blue-400"
              disabled={saving}
            >
              {saving ? <Loader2 className="animate-spin" /> : <Plus />}Create
              rule
            </Button>
          </form>
        </CardContent>
      </Card>
      <Card className="overflow-hidden">
        <CardContent className="p-0">
          {loading ? (
            <div className="p-6">
              <Skeleton className="h-40" />
            </div>
          ) : items.length === 0 ? (
            <div className="p-6">
              <Empty
                icon={Settings2}
                title="No categorization rules"
                copy="Create a rule to categorize matching uncategorized transactions."
              />
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Priority</th>
                    <th>Rule</th>
                    <th>Match</th>
                    <th>Category</th>
                    <th>Status</th>
                    <th />
                  </tr>
                </thead>
                <tbody>
                  {items.map((item) => (
                    <tr key={item.id}>
                      <td className="tabular-nums">{item.priority}</td>
                      <td className="font-medium">{item.name}</td>
                      <td>Contains “{item.descriptionContains}”</td>
                      <td>{item.categoryName}</td>
                      <td>
                        <Badge variant={item.active ? "secondary" : "outline"}>
                          {item.active ? "Active" : "Disabled"}
                        </Badge>
                      </td>
                      <td className="text-right">
                        {item.active && (
                          <Button
                            type="button"
                            size="sm"
                            variant="ghost"
                            onClick={() => disable(item.id)}
                            disabled={saving}
                          >
                            Disable
                          </Button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>
    </>
  );
}

export default App;
