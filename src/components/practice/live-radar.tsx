import { motion } from "motion/react";
import { Users, MapPin } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useNearbyProfessionals } from "@/lib/hooks/practice";
import type { ProfessionalSpecialty } from "@/lib/types/mdd";
import { useTranslation } from "react-i18next";

const SPECIALTY_COLORS: Record<ProfessionalSpecialty, string> = {
  Hygienist: "var(--color-primary)",
  Dentist: "var(--color-chart-2)",
  Assistant: "var(--color-chart-3)",
  FrontOffice: "var(--color-chart-5)",
  Orthodontist: "var(--color-chart-4)",
};

const RADIUS = 14;

export function LiveRadar() {
  const { data: pros = [] } = useNearbyProfessionals(RADIUS);
  const { t } = useTranslation();
  const onlineCount = pros.filter((p) => p.online).length;
  const specialties = Object.keys(SPECIALTY_COLORS) as ProfessionalSpecialty[];

  // Bucket by distance for a clean histogram
  const buckets = [
    { label: "≤ 3 mi", max: 3 },
    { label: "3 – 6 mi", max: 6 },
    { label: "6 – 10 mi", max: 10 },
    { label: "10 – 14 mi", max: 14 },
  ];
  const bucketCounts = buckets.map((b, i) => {
    const min = i === 0 ? 0 : buckets[i - 1].max;
    return pros.filter((p) => p.distanceMiles > min && p.distanceMiles <= b.max).length;
  });
  const maxBucket = Math.max(1, ...bucketCounts);

  return (
    <Card className="border-border/60 shadow-sm">
      <CardHeader className="flex flex-row items-start justify-between gap-3 space-y-0 pb-3">
        <div>
          <CardTitle className="flex items-center gap-2 text-base">
            <Users className="h-4 w-4 text-primary" />
            {t("nearby_talent")}
          </CardTitle>
          <p className="mt-1 text-sm text-muted-foreground">
            <span className="font-semibold text-foreground">{t("professionals_within", { count: pros.length, dist: RADIUS })}</span>{" "}
            · {t("online", { count: onlineCount })}
          </p>
        </div>
        <Badge
          variant="outline"
          className="gap-1.5 border-emerald-500/40 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400"
        >
          <span className="h-1.5 w-1.5 rounded-full bg-emerald-500" />
          {t("live")}
        </Badge>
      </CardHeader>

      <CardContent className="grid gap-6 md:grid-cols-2">
        {/* Distance distribution */}
        <div>
          <p className="text-[11px] font-medium uppercase tracking-wider text-muted-foreground">
            {t("by_distance")}
          </p>
          <ul className="mt-3 space-y-3">
            {buckets.map((b, i) => {
              const count = bucketCounts[i];
              const pct = (count / maxBucket) * 100;
              return (
                <li key={b.label} className="space-y-1.5">
                  <div className="flex items-center justify-between text-xs">
                    <span className="text-muted-foreground">{b.label.replace('mi', t('mi'))}</span>
                    <span className="font-semibold tabular-nums text-foreground">{count}</span>
                  </div>
                  <div className="h-1.5 overflow-hidden rounded-full bg-muted">
                    <motion.div
                      initial={{ width: 0 }}
                      animate={{ width: `${pct}%` }}
                      transition={{ duration: 0.6, delay: 0.05 * i, ease: "easeOut" }}
                      className="h-full rounded-full bg-primary"
                    />
                  </div>
                </li>
              );
            })}
          </ul>
        </div>

        {/* Specialty breakdown */}
        <div>
          <p className="text-[11px] font-medium uppercase tracking-wider text-muted-foreground">
            {t("by_specialty")}
          </p>
          <ul className="mt-3 space-y-2">
            {specialties.map((s) => {
              const count = pros.filter((p) => p.specialty === s).length;
              const online = pros.filter((p) => p.specialty === s && p.online).length;
              return (
                <li
                  key={s}
                  className="flex items-center justify-between rounded-lg border border-border/50 bg-card px-3 py-2 text-xs"
                >
                  <span className="flex items-center gap-2 text-foreground">
                    <span
                      className="h-2 w-2 rounded-full"
                      style={{ background: SPECIALTY_COLORS[s] }}
                    />
                    {t(`${s.toLowerCase()}_label`)}
                  </span>
                  <span className="flex items-center gap-2">
                    <span className="text-muted-foreground">{t("online", { count: online })}</span>
                    <span className="font-semibold tabular-nums text-foreground">{count}</span>
                  </span>
                </li>
              );
            })}
          </ul>

          <div className="mt-4 flex items-center gap-1.5 text-[11px] text-muted-foreground">
            <MapPin className="h-3 w-3" /> Mission Bay Studio · {t("updated_just_now")}
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
