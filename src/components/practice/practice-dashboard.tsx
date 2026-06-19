import { motion } from "motion/react";
import { KpiStrip } from "@/components/practice/kpi-strip";
import { LiveRadar } from "@/components/practice/live-radar";
import { SosButton } from "@/components/practice/sos-button";
import { NewPostingSheet } from "@/components/practice/new-posting-sheet";
import { RecentActivityFeed } from "@/components/practice/recent-activity-feed";
import { UpcomingShifts } from "@/components/practice/upcoming-shifts";
import { ActiveSosTracker } from "@/components/practice/active-sos-tracker";

import { usePracticeDashboard } from "@/lib/hooks/practice";
import { Skeleton } from "@/components/ui/skeleton";
import { useAppStore } from "@/lib/store/app-store";
import { Briefcase, CheckCircle2, CalendarClock, Siren, AlertCircle } from "lucide-react";
import type { KpiItem } from "@/components/practice/kpi-strip";
import { Badge } from "@/components/ui/badge";
import { useTranslation } from "react-i18next";

export function PracticeDashboard() {
  const { data, isLoading } = usePracticeDashboard();
  const { t } = useTranslation();

  const { 
    currentUser, 
    jobPostings, 
    appliedPostingIds, 
    activeSosRequests 
  } = useAppStore();

  if (isLoading || !data) {
    return (
      <div className="space-y-4 p-4 md:p-6">
        <Skeleton className="shimmer h-24 w-full" />
        <div className="grid gap-4 lg:grid-cols-[1.4fr_1fr]">
          <Skeleton className="shimmer h-96 w-full" />
          <Skeleton className="shimmer h-96 w-full" />
        </div>
      </div>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.35, ease: "easeOut" }}
      className="space-y-5 p-4 md:p-6"
    >
      {/* Header */}
      <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <Badge variant="outline" className="mb-2 border-primary/20 bg-primary/10 text-primary">
            {data.location.name}
          </Badge>
          <h1 className="mt-1 text-2xl font-semibold tracking-tight sm:text-3xl">
            {t("good_morning", { name: currentUser?.firstName || "" }).trim()}
          </h1>
          <p className="mt-1 text-sm text-muted-foreground">{t("whats_happening_owner")}</p>
        </div>
        <NewPostingSheet />
      </div>

      <KpiStrip kpis={[
        { 
          key: "activePostings", 
          label: t("active_postings"), 
          value: jobPostings.filter(p => p.status === "Open").length, 
          icon: Briefcase, tint: "primary", 
          delta: t("this_week", { count: "+2" }),
          href: "/practice/postings"
        },
        { 
          key: "filledToday", 
          label: t("filled_today"), 
          value: jobPostings.reduce((sum, p) => sum + (p.hiredCandidateIds?.length || 0), 0), 
          icon: CheckCircle2, tint: "success", 
          delta: `98% ${t("match")}` 
        },
        { 
          key: "pendingInterviews", 
          label: t("pending_interviews"), 
          value: appliedPostingIds.length || 4, // fallback to 4 for demo
          icon: CalendarClock, tint: "warning", 
          delta: t("today", { count: "3" }),
          href: "/practice/schedule"
        },
        { 
          key: "sosSent", 
          label: t("active_sos"), 
          value: activeSosRequests.length, 
          icon: AlertCircle, tint: "destructive", 
          delta: activeSosRequests.length > 0 ? t("searching_now") : t("all_resolved"),
        },
      ]} />

      {/* Main layout grid */}
      <div className="grid gap-4 lg:grid-cols-[1.55fr_1fr] items-start">
        {/* Left Column */}
        <div className="flex flex-col gap-4">
          <LiveRadar />
          <RecentActivityFeed />
        </div>
        
        {/* Right Column */}
        <div className="flex flex-col gap-4">
          <SosButton />
          <ActiveSosTracker />
          <UpcomingShifts />
        </div>
      </div>
    </motion.div>
  );
}
