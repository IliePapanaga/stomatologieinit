import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { motion } from "motion/react";
import {
  FileBarChart2,
  Download,
  CalendarRange,
  Filter,
  Database,
  TrendingUp,
  Users,
  CreditCard,
  ShieldCheck,
} from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { toast } from "sonner";

export const Route = createFileRoute("/admin/reports")({
  component: ReportsPage,
});

interface SystemReport {
  id: string;
  name: string;
  category: "Financials" | "Compliance" | "Operations" | "Growth";
  period: string;
  size: string;
  generatedAt: string;
  icon: typeof FileBarChart2;
}

const reports: SystemReport[] = [
  { id: "rpt_g_001", name: "Global revenue_summary.pdf", category: "Financials", period: "June 2026", size: "1.2 MB", generatedAt: "2026-06-15", icon: CreditCard },
  { id: "rpt_g_002", name: "Practice churn_analysis.pdf", category: "Growth", period: "Q2 2026", size: "884 KB", generatedAt: "2026-06-14", icon: TrendingUp },
  { id: "rpt_g_003", name: "Professional onboarding_funnel.pdf", category: "Growth", period: "May 2026", size: "562 KB", generatedAt: "2026-06-01", icon: Users },
  { id: "rpt_g_004", name: "Certificate compliance_audit.pdf", category: "Compliance", period: "Q2 2026", size: "2.1 MB", generatedAt: "2026-06-10", icon: ShieldCheck },
  { id: "rpt_g_005", name: "PrimeRate gateway_reconciliation.pdf", category: "Financials", period: "May 2026", size: "1.7 MB", generatedAt: "2026-05-31", icon: Database },
  { id: "rpt_g_006", name: "SOS response_metrics.pdf", category: "Operations", period: "Q2 2026", size: "418 KB", generatedAt: "2026-06-12", icon: TrendingUp },
  { id: "rpt_g_007", name: "Attendance & no-show_analysis.pdf", category: "Operations", period: "June 2026", size: "936 KB", generatedAt: "2026-06-13", icon: Users },
  { id: "rpt_g_008", name: "Tax statements_batch_1099.zip", category: "Financials", period: "FY 2025", size: "12.4 MB", generatedAt: "2026-01-12", icon: CreditCard },
];

const categoryStyles: Record<SystemReport["category"], string> = {
  Financials: "border-emerald-500/40 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  Compliance: "border-primary/40 bg-primary/10 text-primary",
  Operations: "border-amber-500/40 bg-amber-500/10 text-amber-600 dark:text-amber-400",
  Growth: "border-rose-500/40 bg-rose-500/10 text-rose-600 dark:text-rose-400",
};

const categories = ["All", "Financials", "Compliance", "Operations", "Growth"] as const;
type Category = (typeof categories)[number];

function ReportsPage() {
  const [tab, setTab] = useState<Category>("All");
  const filtered = tab === "All" ? reports : reports.filter((r) => r.category === tab);

  const download = (r: SystemReport) => {
    const ext = r.name.split(".").pop() ?? "pdf";
    const blob = new Blob(
      [`JasperReports export · ${r.name}\nCategory: ${r.category}\nPeriod: ${r.period}\nGenerated: ${r.generatedAt}\n`],
      { type: ext === "zip" ? "application/zip" : "application/pdf" }
    );
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = r.name.replace(/\.(pdf|zip)$/, `_${r.period.replace(/\s+/g, "_")}.${ext}`);
    a.click();
    URL.revokeObjectURL(url);
    toast.success("Report downloaded", { description: r.name });
  };

  return (
    <div className="space-y-6 p-6">
      <header className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <p className="text-xs font-medium uppercase tracking-wider text-primary">System Reports</p>
          <h1 className="mt-1 text-2xl font-semibold tracking-tight">JasperReports</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            Platform-wide compliance, financial, and operational reports. Generated nightly by the Jasper worker.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" className="gap-2">
            <CalendarRange className="h-4 w-4" /> Date range
          </Button>
          <Button variant="outline" className="gap-2">
            <Filter className="h-4 w-4" /> Filters
          </Button>
          <Button className="bg-primary text-primary-foreground hover:bg-primary/90">
            <FileBarChart2 className="h-4 w-4" /> Generate report
          </Button>
        </div>
      </header>

      <Tabs value={tab} onValueChange={(v) => setTab(v as Category)}>
        <TabsList>
          {categories.map((c) => (
            <TabsTrigger key={c} value={c}>{c}</TabsTrigger>
          ))}
        </TabsList>
      </Tabs>

      <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
        {filtered.map((r, i) => (
          <motion.div
            key={r.id}
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: Math.min(i * 0.04, 0.3), duration: 0.3 }}
          >
            <Card className="group flex h-full flex-col border-border/70 shadow-sm transition hover:border-primary/40 hover:shadow-md">
              <CardHeader className="flex flex-row items-start justify-between space-y-0 pb-3">
                <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10 text-primary">
                  <r.icon className="h-5 w-5" />
                </div>
                <Badge variant="outline" className={`text-[10px] ${categoryStyles[r.category]}`}>
                  {r.category}
                </Badge>
              </CardHeader>
              <CardContent className="flex flex-1 flex-col">
                <CardTitle className="text-sm font-semibold leading-snug">{r.name}</CardTitle>
                <p className="mt-1 text-xs text-muted-foreground">
                  {r.period} · {r.size}
                </p>
                <p className="mt-1 text-[11px] text-muted-foreground">
                  Generated {new Date(r.generatedAt).toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" })}
                </p>
                <Button
                  size="sm"
                  variant="outline"
                  onClick={() => download(r)}
                  className="mt-4 w-full gap-2 group-hover:border-primary/50 group-hover:text-primary"
                >
                  <Download className="h-3.5 w-3.5" /> Download
                </Button>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>
    </div>
  );
}
