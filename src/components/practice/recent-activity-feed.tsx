import { motion } from "motion/react";
import { formatDistanceToNow } from "date-fns";
import { CalendarCheck2, LogIn, AlertTriangle, XCircle, Siren } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useRecentActivity } from "@/lib/hooks/practice";
import { mockProfessionals } from "@/lib/mock";
import type { ActivityEvent } from "@/lib/types/mdd";
import { useTranslation } from "react-i18next";

function getEventMeta(ev: ActivityEvent, t: (k: string) => string) {
  switch (ev.kind) {
    case "CheckIn":
      return { icon: LogIn, tint: "text-success bg-success/10", title: t("checked_in"), at: ev.at };
    case "NoShow":
      return {
        icon: XCircle,
        tint: "text-destructive bg-destructive/10",
        title: t("no_show_recorded"),
        at: ev.at,
      };
    case "AttendanceAlert":
      // Since ev.message is dynamic from mock ("Running 10 min late"), we translate it exactly
      return {
        icon: AlertTriangle,
        tint: "text-warning bg-warning/15",
        title: t(ev.message),
        at: ev.at,
      };
    case "JobInterview":
      return {
        icon: CalendarCheck2,
        tint: "text-primary bg-primary/10",
        title: t("interview_scheduled"),
        at: ev.scheduledDate,
      };
    case "SosRequest":
      return {
        icon: Siren,
        tint: "text-destructive bg-destructive/10",
        title: t("sos_sent"),
        at: ev.createdAt,
      };
  }
}

function getProName(id: string) {
  const p = mockProfessionals.find((p) => p.id === id);
  return p ? `${p.firstName} ${p.lastName.charAt(0)}.` : "Unknown";
}

export function RecentActivityFeed() {
  const { data: events = [] } = useRecentActivity();
  const { t } = useTranslation();
  return (
    <Card className="shadow-soft border-border/60">
      <CardHeader className="pb-2">
        <CardTitle className="text-base">{t("recent_activity")}</CardTitle>
      </CardHeader>
      <CardContent>
        <ul className="space-y-1">
          {events.map((ev, i) => {
            const meta = getEventMeta(ev, t);
            const proId =
              "professionalId" in ev
                ? ev.professionalId
                : "candidateId" in ev
                  ? ev.candidateId
                  : "";
            return (
              <motion.li
                key={ev.id}
                initial={{ opacity: 0, x: -8 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.3, delay: i * 0.06, ease: "easeOut" }}
                className="flex items-center gap-3 rounded-lg px-2 py-2 transition-colors hover:bg-muted/50"
              >
                <div
                  className={`flex h-8 w-8 shrink-0 items-center justify-center rounded-full ${meta.tint}`}
                >
                  <meta.icon className="h-4 w-4" />
                </div>
                <div className="min-w-0 flex-1">
                  <p className="truncate text-sm font-medium">{meta.title}</p>
                  <p className="truncate text-xs text-muted-foreground">
                    {proId && <>{getProName(proId)} · </>}
                    {formatDistanceToNow(new Date(meta.at), { addSuffix: true })}
                  </p>
                </div>
              </motion.li>
            );
          })}
        </ul>
      </CardContent>
    </Card>
  );
}
