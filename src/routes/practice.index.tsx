import { createFileRoute } from "@tanstack/react-router";
import { PracticeDashboard } from "@/components/practice/practice-dashboard";
import { dashboardQuery } from "@/lib/hooks/practice";

export const Route = createFileRoute("/practice/")({
  head: () => ({
    meta: [
      { title: "Owner Dashboard · MDD Dental Staffing" },
      {
        name: "description",
        content:
          "Real-time view of postings, nearby pros, attendance, and billing for your dental practice.",
      },
    ],
  }),
  loader: ({ context }) => {
    context.queryClient.ensureQueryData(dashboardQuery());
  },
  component: PracticeDashboard,
});
