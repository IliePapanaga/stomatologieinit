import { createFileRoute } from "@tanstack/react-router";
import { motion } from "motion/react";
import {
  CreditCard,
  Landmark,
  Lock,
  Plus,
  ShieldCheck,
  FileText,
  Download,
  Receipt,
  CheckCircle2,
  Trash2,
} from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { usePaymentMethods } from "@/lib/hooks/practice";
import { mockPayments } from "@/lib/mock";
import { toast } from "sonner";

export const Route = createFileRoute("/practice/billing")({
  component: BillingPage,
});

interface JasperReport {
  id: string;
  name: string;
  period: string;
  size: string;
  generatedAt: string;
}

const reports: JasperReport[] = [
  { id: "rpt_001", name: "Monthly payments_report.pdf", period: "June 2026", size: "184 KB", generatedAt: "2026-06-15" },
  { id: "rpt_002", name: "Monthly payments_report.pdf", period: "May 2026", size: "212 KB", generatedAt: "2026-05-31" },
  { id: "rpt_003", name: "Quarterly placements_summary.pdf", period: "Q2 2026", size: "446 KB", generatedAt: "2026-06-01" },
  { id: "rpt_004", name: "Annual tax statement_1099.pdf", period: "FY 2025", size: "98 KB", generatedAt: "2026-01-12" },
];

function BillingPage() {
  return (
    <div className="space-y-6 p-6">
      <header className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">Billing</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            Secure payment processing powered by PrimeRate. All card data is tokenized and PCI-compliant.
          </p>
        </div>
        <Badge variant="outline" className="gap-1.5 border-emerald-500/40 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400">
          <ShieldCheck className="h-3.5 w-3.5" />
          PCI DSS Level 1
        </Badge>
      </header>

      <div className="grid gap-6 lg:grid-cols-3">
        <div className="lg:col-span-2 space-y-6">
          <PrimeRateDropIn />
          <RecentPayments />
        </div>
        <div className="space-y-6">
          <Vault />
          <ReportsCard />
        </div>
      </div>
    </div>
  );
}

function PrimeRateDropIn() {
  return (
    <Card className="relative overflow-hidden border-border/70 shadow-sm">
      <div className="absolute inset-x-0 top-0 h-1 bg-gradient-brand" />
      <CardHeader className="flex flex-row items-start justify-between space-y-0">
        <div>
          <CardTitle className="flex items-center gap-2 text-base">
            <Lock className="h-4 w-4 text-primary" />
            Add payment method
          </CardTitle>
          <p className="mt-1 text-xs text-muted-foreground">
            Hosted drop-in iframe · card data never touches our servers
          </p>
        </div>
        <div className="flex items-center gap-1.5 text-[11px] text-muted-foreground">
          <ShieldCheck className="h-3.5 w-3.5 text-emerald-500" />
          <span>Secured by PrimeRate</span>
        </div>
      </CardHeader>
      <CardContent>
        <div
          data-primerate-mount
          className="relative rounded-xl border border-dashed border-border/80 bg-gradient-to-b from-muted/30 to-muted/10 p-8"
        >
          {/* Decorative mock of the hosted iframe */}
          <div className="mx-auto max-w-md space-y-4">
            <MockField label="Cardholder name" placeholder="Maya Chen" />
            <MockField label="Card number" placeholder="1234 5678 9012 3456" right="VISA" />
            <div className="grid grid-cols-2 gap-3">
              <MockField label="Expiry" placeholder="MM / YY" />
              <MockField label="CVC" placeholder="•••" />
            </div>
            <Button
              className="w-full bg-gradient-brand text-primary-foreground"
              onClick={() =>
                toast.success("Tokenization request sent", {
                  description: "PrimeRate returned a vault token. Card added.",
                })
              }
            >
              <Plus className="h-4 w-4" />
              Tokenize & save to vault
            </Button>
          </div>
          <div className="mt-6 flex items-center justify-center gap-2 text-[10px] uppercase tracking-[0.18em] text-muted-foreground/70">
            <span className="h-px w-8 bg-border" />
            iframe mount point · #primerate-dropin
            <span className="h-px w-8 bg-border" />
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

function MockField({ label, placeholder, right }: { label: string; placeholder: string; right?: string }) {
  return (
    <div className="space-y-1.5">
      <label className="text-[11px] font-medium uppercase tracking-wide text-muted-foreground">
        {label}
      </label>
      <div className="flex h-10 items-center justify-between rounded-lg border border-border/70 bg-background px-3 text-sm text-muted-foreground/70">
        <span>{placeholder}</span>
        {right && <span className="text-[10px] font-bold tracking-wider text-muted-foreground">{right}</span>}
      </div>
    </div>
  );
}

function Vault() {
  const { data: methods, isLoading } = usePaymentMethods();

  return (
    <Card className="border-border/70 shadow-sm">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-3">
        <div>
          <CardTitle className="flex items-center gap-2 text-base">
            <Lock className="h-4 w-4 text-primary" /> Vault
          </CardTitle>
          <p className="mt-1 text-xs text-muted-foreground">Tokenized payment methods</p>
        </div>
        <Badge variant="secondary" className="text-[10px]">{methods?.length ?? 0} saved</Badge>
      </CardHeader>
      <CardContent className="space-y-2">
        {isLoading
          ? Array.from({ length: 3 }).map((_, i) => <Skeleton key={i} className="h-16 rounded-xl" />)
          : methods?.map((pm, i) => (
              <motion.div
                key={pm.id}
                initial={{ opacity: 0, y: 6 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: i * 0.06 }}
                className="group relative flex items-center gap-3 rounded-xl border border-border/60 bg-card p-3 transition hover:border-primary/40"
              >
                <div
                  className={`flex h-10 w-14 items-center justify-center rounded-md text-primary-foreground ${
                    pm.type === "ACH" ? "bg-secondary" : "bg-gradient-brand"
                  }`}
                >
                  {pm.type === "ACH" ? <Landmark className="h-4 w-4" /> : <CreditCard className="h-4 w-4" />}
                </div>
                <div className="min-w-0 flex-1">
                  <p className="truncate text-sm font-medium">
                    {pm.brand} •••• {pm.last4}
                  </p>
                  <p className="truncate text-[11px] text-muted-foreground">
                    {pm.holderName}
                    {pm.expMonth
                      ? ` · exp ${pm.expMonth.toString().padStart(2, "0")}/${pm.expYear?.toString().slice(-2)}`
                      : ""}
                  </p>
                </div>
                {pm.isDefault ? (
                  <Badge
                    variant="outline"
                    className="border-emerald-500/40 bg-emerald-500/10 text-[10px] text-emerald-600 dark:text-emerald-400"
                  >
                    <CheckCircle2 className="mr-1 h-3 w-3" /> Default
                  </Badge>
                ) : (
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-7 w-7 opacity-0 transition group-hover:opacity-100"
                    onClick={() => toast("Removed from vault")}
                  >
                    <Trash2 className="h-3.5 w-3.5" />
                  </Button>
                )}
              </motion.div>
            ))}
        <p className="pt-2 text-center text-[10px] uppercase tracking-wider text-muted-foreground/60">
          Tokens stored at PrimeRate · never on our servers
        </p>
      </CardContent>
    </Card>
  );
}

function ReportsCard() {
  const downloadMock = (r: JasperReport) => {
    const blob = new Blob(
      [`JasperReports export\n${r.name}\nPeriod: ${r.period}\nGenerated: ${r.generatedAt}\n`],
      { type: "application/pdf" }
    );
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = r.name.replace(".pdf", `_${r.period.replace(/\s+/g, "_")}.pdf`);
    a.click();
    URL.revokeObjectURL(url);
    toast.success("Report downloaded", { description: r.name });
  };

  return (
    <Card className="border-border/70 shadow-sm">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-3">
        <div>
          <CardTitle className="flex items-center gap-2 text-base">
            <FileText className="h-4 w-4 text-primary" /> Reports
          </CardTitle>
          <p className="mt-1 text-xs text-muted-foreground">JasperReports · auto-generated</p>
        </div>
      </CardHeader>
      <CardContent className="space-y-2">
        {reports.map((r, i) => (
          <motion.button
            key={r.id}
            initial={{ opacity: 0, x: -6 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: i * 0.05 }}
            onClick={() => downloadMock(r)}
            className="group flex w-full items-center gap-3 rounded-lg border border-border/60 bg-card p-3 text-left transition hover:border-primary/40 hover:bg-muted/40"
          >
            <div className="flex h-9 w-9 items-center justify-center rounded-md bg-primary/10 text-primary">
              <FileText className="h-4 w-4" />
            </div>
            <div className="min-w-0 flex-1">
              <p className="truncate text-sm font-medium">{r.name}</p>
              <p className="truncate text-[11px] text-muted-foreground">
                {r.period} · {r.size}
              </p>
            </div>
            <Download className="h-4 w-4 text-muted-foreground transition group-hover:text-primary" />
          </motion.button>
        ))}
      </CardContent>
    </Card>
  );
}

function RecentPayments() {
  return (
    <Card className="border-border/70 shadow-sm">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-3">
        <div>
          <CardTitle className="flex items-center gap-2 text-base">
            <Receipt className="h-4 w-4 text-primary" /> Recent transactions
          </CardTitle>
          <p className="mt-1 text-xs text-muted-foreground">Last 30 days</p>
        </div>
        <Button variant="ghost" size="sm" className="text-xs">View all</Button>
      </CardHeader>
      <CardContent>
        <div className="divide-y divide-border/60">
          {mockPayments.map((p, i) => (
            <motion.div
              key={p.id}
              initial={{ opacity: 0, y: 4 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.05 }}
              className="flex items-center justify-between py-3"
            >
              <div className="flex items-center gap-3">
                <div
                  className={`flex h-9 w-9 items-center justify-center rounded-md ${
                    p.method === "ACH" ? "bg-secondary text-secondary-foreground" : "bg-primary/10 text-primary"
                  }`}
                >
                  {p.method === "ACH" ? <Landmark className="h-4 w-4" /> : <CreditCard className="h-4 w-4" />}
                </div>
                <div>
                  <p className="text-sm font-medium">{p.description}</p>
                  <p className="text-[11px] text-muted-foreground">
                    {new Date(p.date).toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" })}
                    {" · "}
                    {p.gatewayId}
                  </p>
                </div>
              </div>
              <div className="text-right">
                <p className="text-sm font-semibold tabular-nums">${p.amount.toLocaleString()}</p>
                <Badge
                  variant="outline"
                  className="mt-0.5 border-emerald-500/40 bg-emerald-500/10 text-[10px] text-emerald-600 dark:text-emerald-400"
                >
                  {p.status}
                </Badge>
              </div>
            </motion.div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}
