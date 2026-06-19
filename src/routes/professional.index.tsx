import { createFileRoute, Link } from "@tanstack/react-router";
import { motion } from "motion/react";
import { useMemo } from "react";
import { useTranslation } from "react-i18next";
import {
  Briefcase,
  CalendarClock,
  DollarSign,
  Sparkles,
  ArrowRight,
  CheckCircle2,
  AlertCircle,
} from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { useAppStore } from "@/lib/store/app-store";
import { mockLocations } from "@/lib/mock";
import i18n from "@/lib/i18n";

export const Route = createFileRoute("/professional/")({
  component: OverviewPage,
});

function OverviewPage() {
  const { t } = useTranslation();
  const profile = useAppStore((s) => s.professionalProfile);
  const postings = useAppStore((s) => s.jobPostings);
  const applied = useAppStore((s) => s.appliedPostingIds);
  const history = useAppStore((s) => s.jobHistory);
  const banned = useAppStore((s) => s.bannedPracticeIds);

  const completion = useMemo(() => {
    let score = 0;
    if (profile.firstName && profile.lastName) score += 15;
    if (profile.phone) score += 10;
    if (profile.bio) score += 10;
    if (profile.specialties.length > 0) score += 15;
    const validCerts = profile.certificates.filter((c) => c.status === "Valid").length;
    score += Math.min(30, validCerts * 6);
    if (profile.skills.length >= 2) score += 10;
    if (profile.commutingRadius > 0) score += 10;
    return Math.min(100, score);
  }, [profile]);

  const upcoming = useMemo(
    () => postings.filter((p) => applied.includes(p.id) && p.kind === "Temporary").slice(0, 3),
    [postings, applied],
  );

  const totalEarnings = history.reduce((s, h) => s + h.earnings, 0);
  const totalHours = history.reduce((s, h) => s + h.hours, 0);
  const checkIns = history.filter((h) => h.status !== "No-Show").length;

  return (
    <div className="space-y-6 p-6">
      <header className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <p className="text-xs font-medium uppercase tracking-wider text-primary">
            {t("overview")}
          </p>
          <h1 className="mt-1 text-2xl font-semibold tracking-tight">
            {t("good_morning", { name: profile.firstName || "" }).trim()}
          </h1>
          <p className="mt-1 text-sm text-muted-foreground">{t("whats_happening_pro")}</p>
        </div>
        <Button asChild className="bg-primary text-primary-foreground hover:bg-primary/90">
          <Link to="/professional/temporary-jobs">
            <Briefcase className="h-4 w-4" /> {t("browse_jobs")}
          </Link>
        </Button>
      </header>

      <div className="grid gap-4 lg:grid-cols-3">
        <motion.div
          initial={{ opacity: 0, y: 6 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
          className="lg:col-span-1"
        >
          <Card className="flex h-full flex-col items-center gap-4 p-6">
            <p className="self-start text-xs uppercase tracking-wider text-muted-foreground">
              {t("profile_completion")}
            </p>
            <CompletionRing value={completion} />
            <div className="space-y-1.5 self-stretch text-xs">
              <CompletionRow
                ok={profile.specialties.length > 0}
                label={t("specialties_selected")}
              />
              <CompletionRow
                ok={profile.certificates.filter((c) => c.status === "Valid").length >= 3}
                label={t("three_plus_valid_certs")}
              />
              <CompletionRow ok={profile.skills.length >= 2} label={t("experience_added")} />
              <CompletionRow ok={Boolean(profile.bio)} label={t("bio_written")} />
            </div>
            <Button asChild variant="outline" size="sm" className="self-stretch">
              <Link to="/professional/profile">
                {t("complete_profile")} <ArrowRight className="h-3.5 w-3.5" />
              </Link>
            </Button>
          </Card>
        </motion.div>

        <div className="lg:col-span-2 grid gap-4 sm:grid-cols-2">
          <KpiCard
            label={t("earnings")}
            value={`$${totalEarnings.toLocaleString()}`}
            sub={`${totalHours} ${t("hours")}`}
            icon={DollarSign}
            tint="emerald"
          />
          <KpiCard
            label={t("jobs_applied")}
            value={applied.length.toString()}
            sub={`${upcoming.length} ${t("upcoming")}`}
            icon={Briefcase}
            tint="primary"
          />
          <KpiCard
            label={t("check_in_rate")}
            value={history.length === 0 ? "—" : `${Math.round((checkIns / history.length) * 100)}%`}
            sub={`${history.length} ${t("past_shifts")}`}
            icon={CheckCircle2}
            tint="indigo"
          />
          <KpiCard
            label={t("banned_offices")}
            value={banned.length.toString()}
            sub={t("hidden_from_feed")}
            icon={AlertCircle}
            tint="rose"
          />
        </div>
      </div>

      <Card className="p-5">
        <div className="mb-4 flex items-center justify-between">
          <div>
            <p className="text-sm font-semibold">
              {t("upcoming")} {t("shifts")}
            </p>
            <p className="text-xs text-muted-foreground">{t("temp_jobs_applied")}</p>
          </div>
          <Button asChild variant="ghost" size="sm">
            <Link to="/professional/temporary-jobs">{t("view_all")}</Link>
          </Button>
        </div>
        {upcoming.length === 0 ? (
          <div className="rounded-lg border border-dashed border-border/70 p-6 text-center text-sm text-muted-foreground">
            <Sparkles className="mx-auto mb-2 h-5 w-5 text-primary" />
            {t("no_upcoming_shifts")}
          </div>
        ) : (
          <div className="grid gap-2.5 sm:grid-cols-2 lg:grid-cols-3">
            {upcoming.map((p) => {
              const loc = mockLocations.find((l) => l.id === p.locationId);
              return (
                <div key={p.id} className="rounded-xl border border-border/70 bg-card p-3.5">
                  <Badge variant="outline" className="border-primary/40 bg-primary/10 text-primary">
                    {p.kind === "Temporary" ? `$${p.hourlyRate}/hr` : t("permanent")}
                  </Badge>
                  <p className="mt-2 text-sm font-semibold">{p.title}</p>
                  <p className="mt-0.5 text-xs text-muted-foreground">
                    {loc?.name ?? "—"} · {new Date(p.startDate).toLocaleDateString()}
                  </p>
                </div>
              );
            })}
          </div>
        )}
      </Card>
    </div>
  );
}

function CompletionRing({ value }: { value: number }) {
  const r = 56;
  const c = 2 * Math.PI * r;
  const offset = c - (value / 100) * c;
  return (
    <div className="relative h-36 w-36">
      <svg viewBox="0 0 140 140" className="h-full w-full -rotate-90">
        <circle
          cx="70"
          cy="70"
          r={r}
          stroke="currentColor"
          strokeWidth="10"
          fill="none"
          className="text-muted"
        />
        <motion.circle
          cx="70"
          cy="70"
          r={r}
          stroke="currentColor"
          strokeWidth="10"
          strokeLinecap="round"
          fill="none"
          className="text-primary"
          strokeDasharray={c}
          initial={{ strokeDashoffset: c }}
          animate={{ strokeDashoffset: offset }}
          transition={{ duration: 0.8, ease: "easeOut" }}
        />
      </svg>
      <div className="absolute inset-0 flex flex-col items-center justify-center">
        <span className="text-3xl font-semibold">{value}%</span>
        <span className="text-[10px] uppercase tracking-wider text-muted-foreground">
          {i18n.t("complete_ring")}
        </span>
      </div>
    </div>
  );
}

function CompletionRow({ ok, label }: { ok: boolean; label: string }) {
  return (
    <div className="flex items-center gap-2">
      <CheckCircle2
        className={`h-3.5 w-3.5 ${ok ? "text-emerald-500" : "text-muted-foreground/40"}`}
      />
      <span className={ok ? "text-foreground" : "text-muted-foreground"}>{label}</span>
    </div>
  );
}

function KpiCard({
  label,
  value,
  sub,
  icon: Icon,
  tint,
}: {
  label: string;
  value: string;
  sub: string;
  icon: typeof Briefcase;
  tint: "emerald" | "primary" | "indigo" | "rose";
}) {
  const tints: Record<string, string> = {
    emerald: "bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border-emerald-500/30",
    primary: "bg-primary/10 text-primary border-primary/30",
    indigo: "bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border-indigo-500/30",
    rose: "bg-rose-500/10 text-rose-600 dark:text-rose-400 border-rose-500/30",
  };
  return (
    <Card className="flex items-start gap-3 p-4">
      <div
        className={`flex h-10 w-10 items-center justify-center rounded-xl border ${tints[tint]}`}
      >
        <Icon className="h-5 w-5" />
      </div>
      <div className="min-w-0">
        <p className="text-xs uppercase tracking-wider text-muted-foreground">{label}</p>
        <p className="mt-0.5 text-xl font-semibold">{value}</p>
        <p className="text-[11px] text-muted-foreground">{sub}</p>
      </div>
    </Card>
  );
}
