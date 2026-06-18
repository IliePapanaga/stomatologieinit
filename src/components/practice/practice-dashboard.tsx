import { motion } from "motion/react";
import { KpiStrip } from "@/components/practice/kpi-strip";
import { LiveRadar } from "@/components/practice/live-radar";
import { SosButton } from "@/components/practice/sos-button";
import { JobCreatorSheet } from "@/components/practice/job-creator-sheet";
import { RecentActivityFeed } from "@/components/practice/recent-activity-feed";
import { UpcomingShifts } from "@/components/practice/upcoming-shifts";

import { usePracticeDashboard } from "@/lib/hooks/practice";
import { Skeleton } from "@/components/ui/skeleton";

export function PracticeDashboard() {
  const { data, isLoading } = usePracticeDashboard();

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
          <p className="text-xs font-medium uppercase tracking-wider text-primary">
            {data.location.name}
          </p>
          <h1 className="mt-1 text-2xl font-semibold tracking-tight sm:text-3xl">
            Good morning, {data.practice.ownerFirstName.replace("Dr. ", "")}
          </h1>
          <p className="mt-1 text-sm text-muted-foreground">
            Here's what's happening across your practice today.
          </p>
        </div>
        <JobCreatorSheet />
      </div>

      <KpiStrip kpis={data.kpis} />

      {/* Centerpiece row: radar + SOS */}
      <div className="grid gap-4 lg:grid-cols-[1.55fr_1fr]">
        <LiveRadar />
        <div className="flex flex-col gap-4">
          <SosButton />
          <UpcomingShifts />
        </div>
      </div>

      {/* Activity */}
      <RecentActivityFeed />

    </motion.div>
  );
}
