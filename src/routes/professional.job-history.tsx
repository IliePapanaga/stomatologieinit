import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { useAppStore } from "@/lib/store/app-store";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { motion } from "motion/react";
import {
  CheckCircle2,
  XCircle,
  Clock3,
  DollarSign,
  MapPin,
  Info,
  Building2,
  CalendarDays,
} from "lucide-react";
import { PracticeOwnerSheet } from "@/components/professional/practice-owner-sheet";
import { knownPractices } from "@/lib/store/app-store";

export const Route = createFileRoute("/professional/job-history")({
  component: HistoryPage,
});

const statusStyles: Record<string, string> = {
  "Checked-In": "border-emerald-500/40 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  Completed: "border-primary/40 bg-primary/10 text-primary",
  "No-Show": "border-rose-500/40 bg-rose-500/10 text-rose-600 dark:text-rose-400",
};
const statusIcon: Record<string, typeof CheckCircle2> = {
  "Checked-In": CheckCircle2,
  Completed: CheckCircle2,
  "No-Show": XCircle,
};

function HistoryPage() {
  const history = useAppStore((s) => s.jobHistory);
  const totalEarnings = history.reduce((s, h) => s + h.earnings, 0);
  const totalHours = history.reduce((s, h) => s + h.hours, 0);

  const [selectedPracticeId, setSelectedPracticeId] = useState<string | null>(null);

  return (
    <div className="space-y-6 p-6">
      <header>
        <p className="text-xs font-medium uppercase tracking-wider text-primary">Job History</p>
        <h1 className="mt-1 text-2xl font-semibold tracking-tight">Your past shifts</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Full timeline of completed jobs and earnings. Click any row for owner details.
        </p>
      </header>

      {/* Stats */}
      <div className="grid gap-3 sm:grid-cols-3">
        <Stat label="Total earned" value={`$${totalEarnings.toLocaleString()}`} icon={DollarSign} />
        <Stat label="Hours worked" value={`${totalHours} hrs`} icon={Clock3} />
        <Stat label="Shifts" value={history.length.toString()} icon={CheckCircle2} />
      </div>

      {/* Cards (mobile-first) */}
      <div className="md:hidden space-y-3">
        {history.map((h, i) => {
          const Icon = statusIcon[h.status];
          const practice = knownPractices.find((p) => p.id === h.practiceId);
          return (
            <motion.div
              key={h.id}
              initial={{ opacity: 0, y: 8 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: Math.min(i * 0.04, 0.2) }}
            >
              <Card className="p-4 space-y-3">
                <div className="flex items-start justify-between gap-2">
                  <div className="min-w-0">
                    <p className="font-semibold text-sm">{h.role}</p>
                    <p className="text-xs text-muted-foreground mt-0.5">{h.practiceName}</p>
                  </div>
                  <Badge variant="outline" className={`shrink-0 gap-1 ${statusStyles[h.status]}`}>
                    <Icon className="h-3 w-3" /> {h.status}
                  </Badge>
                </div>
                <div className="flex flex-wrap gap-x-4 gap-y-1 text-xs text-muted-foreground">
                  <span className="flex items-center gap-1">
                    <CalendarDays className="h-3.5 w-3.5" />
                    {new Date(h.date).toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" })}
                  </span>
                  <span className="flex items-center gap-1">
                    <Clock3 className="h-3.5 w-3.5" />
                    {h.hours} hrs
                  </span>
                  <span className="flex items-center gap-1 text-emerald-600 dark:text-emerald-400 font-medium">
                    <DollarSign className="h-3.5 w-3.5" />${h.earnings}
                  </span>
                </div>
                {h.practiceId && (
                  <Button
                    size="sm"
                    variant="outline"
                    className="w-full gap-1.5 text-xs"
                    onClick={() => setSelectedPracticeId(h.practiceId)}
                  >
                    <Info className="h-3.5 w-3.5" />
                    View owner info{practice ? ` · ${practice.name}` : ""}
                  </Button>
                )}
              </Card>
            </motion.div>
          );
        })}
      </div>

      {/* Table (desktop) */}
      <Card className="hidden md:block overflow-hidden border-border/70 shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border/60 bg-muted/40">
                <th className="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-muted-foreground">Date</th>
                <th className="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-muted-foreground">Owner</th>
                <th className="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-muted-foreground">Role</th>
                <th className="px-5 py-3 text-right text-xs font-medium uppercase tracking-wider text-muted-foreground">Hours</th>
                <th className="px-5 py-3 text-right text-xs font-medium uppercase tracking-wider text-muted-foreground">Earnings</th>
                <th className="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-muted-foreground">Status</th>
                <th className="px-5 py-3 text-right text-xs font-medium uppercase tracking-wider text-muted-foreground">Info</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border/60">
              {history.map((h, i) => {
                const Icon = statusIcon[h.status];
                return (
                  <motion.tr
                    key={h.id}
                    initial={{ opacity: 0, y: 4 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: Math.min(i * 0.03, 0.2) }}
                    className="hover:bg-muted/30 transition-colors"
                  >
                    <td className="whitespace-nowrap px-5 py-3 text-muted-foreground">
                      {new Date(h.date).toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" })}
                    </td>
                    <td className="px-5 py-3">
                      <div className="flex items-center gap-2">
                        <div className="flex h-7 w-7 shrink-0 items-center justify-center rounded-lg bg-primary/10 text-primary">
                          <Building2 className="h-3.5 w-3.5" />
                        </div>
                        <span className="font-medium">{h.practiceName}</span>
                      </div>
                    </td>
                    <td className="px-5 py-3 text-muted-foreground">{h.role}</td>
                    <td className="px-5 py-3 text-right tabular-nums">{h.hours}</td>
                    <td className="px-5 py-3 text-right tabular-nums font-medium text-emerald-600 dark:text-emerald-400">
                      ${h.earnings}
                    </td>
                    <td className="px-5 py-3">
                      <Badge variant="outline" className={`gap-1 ${statusStyles[h.status]}`}>
                        <Icon className="h-3 w-3" /> {h.status}
                      </Badge>
                    </td>
                    <td className="px-5 py-3 text-right">
                      {h.practiceId && (
                        <Button
                          size="sm"
                          variant="ghost"
                          className="h-7 w-7 p-0"
                          onClick={() => setSelectedPracticeId(h.practiceId)}
                          title="View owner info"
                        >
                          <Info className="h-4 w-4" />
                        </Button>
                      )}
                    </td>
                  </motion.tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </Card>

      <PracticeOwnerSheet
        practiceId={selectedPracticeId}
        onOpenChange={(open) => { if (!open) setSelectedPracticeId(null); }}
      />
    </div>
  );
}

function Stat({ label, value, icon: Icon }: { label: string; value: string; icon: typeof DollarSign }) {
  return (
    <Card className="flex items-center gap-3 p-4">
      <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-primary/30 bg-primary/10 text-primary">
        <Icon className="h-5 w-5" />
      </div>
      <div>
        <p className="text-xs uppercase tracking-wider text-muted-foreground">{label}</p>
        <p className="text-xl font-semibold">{value}</p>
      </div>
    </Card>
  );
}
