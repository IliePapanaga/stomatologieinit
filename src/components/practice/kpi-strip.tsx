import { motion } from "motion/react";
import { Briefcase, CheckCircle2, CalendarClock, Siren } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { AnimatedNumber } from "@/components/shared/animated-number";
import type { PracticeDashboardData } from "@/lib/types/mdd";

interface Props {
  kpis: PracticeDashboardData["kpis"];
}

const items = [
  { key: "activePostings" as const, label: "Active postings", icon: Briefcase, tint: "primary", delta: "+2 this week" },
  { key: "filledToday" as const, label: "Filled today", icon: CheckCircle2, tint: "success", delta: "98% match" },
  { key: "pendingInterviews" as const, label: "Pending interviews", icon: CalendarClock, tint: "warning", delta: "3 today" },
  { key: "sosSent" as const, label: "SOS in last 7d", icon: Siren, tint: "destructive", delta: "1 resolved" },
];

const tintClasses: Record<string, string> = {
  primary: "bg-primary/10 text-primary",
  success: "bg-success/15 text-success",
  warning: "bg-warning/15 text-warning",
  destructive: "bg-destructive/10 text-destructive",
};

export function KpiStrip({ kpis }: Props) {
  return (
    <div className="grid grid-cols-2 gap-3 sm:gap-4 lg:grid-cols-4">
      {items.map((item, i) => (
        <motion.div
          key={item.key}
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: i * 0.06, ease: "easeOut" }}
        >
          <Card className="shadow-soft border-border/60 overflow-hidden">
            <CardContent className="p-4 sm:p-5">
              <div className="flex items-start justify-between gap-3">
                <div className="min-w-0">
                  <p className="text-xs font-medium text-muted-foreground uppercase tracking-wide">
                    {item.label}
                  </p>
                  <p className="mt-1.5 text-3xl font-semibold tracking-tight tabular-nums">
                    <AnimatedNumber value={kpis[item.key]} />
                  </p>
                  <p className="mt-1 text-[11px] text-muted-foreground">{item.delta}</p>
                </div>
                <div className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-xl ${tintClasses[item.tint]}`}>
                  <item.icon className="h-4 w-4" />
                </div>
              </div>
            </CardContent>
          </Card>
        </motion.div>
      ))}
    </div>
  );
}
