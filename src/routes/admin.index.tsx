import { createFileRoute } from "@tanstack/react-router";
import { motion } from "motion/react";
import {
  Building2,
  Users,
  FileCheck2,
  AlertTriangle,
  TrendingUp,
  Activity,
  ShieldAlert,
  Server,
  Database,
  Cloud,
} from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useTranslation } from "react-i18next";
import i18n from "@/lib/i18n";

export const Route = createFileRoute("/admin/")({
  component: AdminOverview,
});

interface Metric {
  label: string;
  value: string;
  delta?: string;
  trend?: "up" | "down" | "flat";
  icon: typeof Building2;
  tone?: "primary" | "amber" | "emerald" | "rose";
}

const metrics: Metric[] = [
  {
    get label() {
      return i18n.t("active_owners");
    },
    value: "1,284",
    delta: "+38 this week",
    trend: "up",
    icon: Building2,
    tone: "primary",
  },
  {
    get label() {
      return i18n.t("active_professionals");
    },
    value: "9,762",
    delta: "+412 this month",
    trend: "up",
    icon: Users,
    tone: "emerald",
  },
  {
    get label() {
      return i18n.t("pending_certificates");
    },
    value: "147",
    delta: "32 over 7 days",
    trend: "down",
    icon: FileCheck2,
    tone: "amber",
  },
  {
    get label() {
      return i18n.t("open_incidents");
    },
    value: "3",
    delta: "All under SLA",
    trend: "flat",
    icon: AlertTriangle,
    tone: "rose",
  },
];

const toneClasses: Record<NonNullable<Metric["tone"]>, string> = {
  primary: "bg-primary/10 text-primary",
  amber: "bg-amber-500/10 text-amber-600 dark:text-amber-400",
  emerald: "bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  rose: "bg-rose-500/10 text-rose-600 dark:text-rose-400",
};

const levelStyles: Record<string, string> = {
  info: "bg-primary/10 text-primary border-primary/30",
  warn: "bg-amber-500/15 text-amber-600 dark:text-amber-400 border-amber-500/30",
  critical: "bg-rose-500/15 text-rose-600 dark:text-rose-400 border-rose-500/30",
};

const statusDot: Record<string, string> = {
  operational: "bg-emerald-500",
  degraded: "bg-amber-500",
  down: "bg-rose-500",
};

function AdminOverview() {
  const { t } = useTranslation();

  const auditEvents = [
    {
      id: "ev1",
      actor: `${t("super_admin")} · sjohnson`,
      action: t("impersonated"),
      target: "Dr. Maya Chen (Brightside Dental)",
      at: t("2m_ago"),
      level: "info",
    },
    {
      id: "ev2",
      actor: t("system"),
      action: t("auto_rejected"),
      target: t("12_expired_cpr"),
      at: t("21m_ago"),
      level: "warn",
    },
    {
      id: "ev3",
      actor: `${t("system_admin")} · achen`,
      action: t("approved"),
      target: t("xray_license_pro412"),
      at: t("1h_ago"),
      level: "info",
    },
    {
      id: "ev4",
      actor: t("system"),
      action: t("rate_limit_triggered"),
      target: t("primerate_webhook_503"),
      at: t("3h_ago"),
      level: "warn",
    },
    {
      id: "ev5",
      actor: `${t("system_admin")} · achen`,
      action: t("suspended"),
      target: t("owner_prc_204_chargeback"),
      at: t("6h_ago"),
      level: "critical",
    },
  ];

  const services = [
    { name: "API Gateway", status: "operational", latency: "84ms", icon: Server },
    { name: "PostgreSQL primary", status: "operational", latency: "12ms", icon: Database },
    { name: "PrimeRate webhook", status: "degraded", latency: "612ms", icon: Cloud },
    { name: "JasperReports worker", status: "operational", latency: "—", icon: Activity },
  ];

  return (
    <div className="space-y-6 p-6">
      <header>
        <p className="text-xs font-medium uppercase tracking-wider text-primary">
          {t("system_console")}
        </p>
        <h1 className="mt-1 text-2xl font-semibold tracking-tight">{t("system_overview")}</h1>
        <p className="mt-1 text-sm text-muted-foreground">{t("system_desc")}</p>
      </header>

      <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
        {metrics.map((m, i) => (
          <motion.div
            key={m.label}
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.05, duration: 0.3 }}
          >
            <Card className="border-border/70 shadow-sm">
              <CardContent className="p-5">
                <div className="flex items-start justify-between">
                  <div>
                    <p className="text-[11px] font-medium uppercase tracking-wider text-muted-foreground">
                      {m.label}
                    </p>
                    <p className="mt-1.5 text-3xl font-semibold tabular-nums">{m.value}</p>
                  </div>
                  <div
                    className={`flex h-10 w-10 items-center justify-center rounded-xl ${toneClasses[m.tone ?? "primary"]}`}
                  >
                    <m.icon className="h-5 w-5" />
                  </div>
                </div>
                {m.delta && (
                  <p className="mt-3 flex items-center gap-1 text-xs text-muted-foreground">
                    <TrendingUp className={`h-3 w-3 ${m.trend === "down" ? "rotate-180" : ""}`} />
                    {m.delta}
                  </p>
                )}
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>

      <div className="grid gap-6 lg:grid-cols-[1.6fr_1fr]">
        <Card className="border-border/70 shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-3">
            <div>
              <CardTitle className="flex items-center gap-2 text-base">
                <ShieldAlert className="h-4 w-4 text-primary" />
                {t("audit_log")}
              </CardTitle>
              <p className="mt-1 text-xs text-muted-foreground">{t("latest_actions")}</p>
            </div>
            <Badge variant="outline">
              {auditEvents.length} {t("events")}
            </Badge>
          </CardHeader>
          <CardContent className="divide-y divide-border/60 p-0">
            {auditEvents.map((e, i) => (
              <motion.div
                key={e.id}
                initial={{ opacity: 0, x: -4 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: i * 0.04 }}
                className="flex flex-col sm:flex-row sm:items-center gap-1.5 sm:gap-3 px-4 py-3 sm:px-5"
              >
                <div className="flex items-center justify-between sm:justify-start">
                  <Badge
                    variant="outline"
                    className={`h-5 px-1.5 text-[10px] shrink-0 ${levelStyles[e.level]}`}
                  >
                    {e.action}
                  </Badge>
                  <span className="text-[11px] tabular-nums text-muted-foreground sm:hidden">
                    {e.at}
                  </span>
                </div>
                <div className="min-w-0 flex-1">
                  <p className="text-sm leading-snug sm:truncate">
                    <span className="font-medium">{e.actor}</span>
                    <span className="text-muted-foreground block sm:inline mt-0.5 sm:mt-0">
                      <span className="hidden sm:inline mx-1.5 opacity-50">→</span>
                      {e.target}
                    </span>
                  </p>
                </div>
                <span className="hidden sm:block text-[11px] tabular-nums text-muted-foreground shrink-0">
                  {e.at}
                </span>
              </motion.div>
            ))}
          </CardContent>
        </Card>

        <Card className="border-border/70 shadow-sm">
          <CardHeader className="pb-3">
            <CardTitle className="flex items-center gap-2 text-base">
              <Activity className="h-4 w-4 text-primary" />
              {t("service_health")}
            </CardTitle>
            <p className="mt-1 text-xs text-muted-foreground">{t("last_5_min")}</p>
          </CardHeader>
          <CardContent className="space-y-2">
            {services.map((s) => (
              <div
                key={s.name}
                className="flex items-center justify-between rounded-lg border border-border/60 bg-card px-3 py-2"
              >
                <div className="flex items-center gap-2.5">
                  <s.icon className="h-4 w-4 text-muted-foreground" />
                  <span className="text-sm font-medium">{s.name}</span>
                </div>
                <div className="flex items-center gap-2">
                  <span className="text-[11px] tabular-nums text-muted-foreground">
                    {s.latency}
                  </span>
                  <span className="relative flex h-2 w-2">
                    <span
                      className={`absolute inline-flex h-full w-full animate-ping rounded-full opacity-60 ${statusDot[s.status]}`}
                    />
                    <span
                      className={`relative inline-flex h-2 w-2 rounded-full ${statusDot[s.status]}`}
                    />
                  </span>
                </div>
              </div>
            ))}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
