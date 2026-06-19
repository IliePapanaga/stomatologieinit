import { motion } from "motion/react";
import { Briefcase, CheckCircle2, CalendarClock, Siren } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { AnimatedNumber } from "@/components/shared/animated-number";
import { Link } from "@tanstack/react-router";

export interface KpiItem {
  key: string;
  label: string;
  value: number;
  icon: any;
  tint: "primary" | "success" | "warning" | "destructive";
  delta: string;
  href?: string;
}

interface Props {
  kpis: KpiItem[];
}

const tintClasses: Record<string, string> = {
  primary: "bg-primary/10 text-primary",
  success: "bg-success/15 text-success",
  warning: "bg-warning/15 text-warning",
  destructive: "bg-destructive/10 text-destructive",
};

export function KpiStrip({ kpis }: Props) {
  return (
    <div className="grid grid-cols-2 gap-3 sm:gap-4 lg:grid-cols-4">
      {kpis.map((item, i) => (
        <motion.div
          key={item.key}
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: i * 0.06, ease: "easeOut" }}
        >
          {item.href ? (
            <Link to={item.href} className="block transition-all hover:-translate-y-0.5">
              <Card className="shadow-soft border-border/60 overflow-hidden hover:border-primary/40 hover:shadow-glow dark:hover:border-primary/30 transition-all h-full cursor-pointer">
                <CardContent className="p-4 sm:p-5">
                  <div className="flex items-start justify-between gap-3">
                    <div className="min-w-0">
                      <p className="text-xs font-medium text-muted-foreground uppercase tracking-wide">
                        {item.label}
                      </p>
                      <p className="mt-1.5 text-3xl font-semibold tracking-tight tabular-nums">
                        <AnimatedNumber value={item.value} />
                      </p>
                      <p className="mt-1 text-[11px] text-muted-foreground">{item.delta}</p>
                    </div>
                    <div
                      className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-xl ${tintClasses[item.tint]}`}
                    >
                      <item.icon className="h-4 w-4" />
                    </div>
                  </div>
                </CardContent>
              </Card>
            </Link>
          ) : (
            <Card className="shadow-soft border-border/60 overflow-hidden h-full">
              <CardContent className="p-4 sm:p-5">
                <div className="flex items-start justify-between gap-3">
                  <div className="min-w-0">
                    <p className="text-xs font-medium text-muted-foreground uppercase tracking-wide">
                      {item.label}
                    </p>
                    <p className="mt-1.5 text-3xl font-semibold tracking-tight tabular-nums">
                      <AnimatedNumber value={item.value} />
                    </p>
                    <p className="mt-1 text-[11px] text-muted-foreground">{item.delta}</p>
                  </div>
                  <div
                    className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-xl ${tintClasses[item.tint]}`}
                  >
                    <item.icon className="h-4 w-4" />
                  </div>
                </div>
              </CardContent>
            </Card>
          )}
        </motion.div>
      ))}
    </div>
  );
}
