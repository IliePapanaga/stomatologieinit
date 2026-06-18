import { useMemo, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { motion } from "motion/react";
import {
  Building2,
  MapPin,
  CalendarRange,
  Search,
  Ban,
  Check,
  Sparkles,
} from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { useAppStore, knownPractices } from "@/lib/store/app-store";
import { mockLocations } from "@/lib/mock";
import type { PermanentJobPosting } from "@/lib/types/mdd";
import { toast } from "sonner";

export const Route = createFileRoute("/professional/permanent-jobs")({
  component: PermanentJobsPage,
});

function PermanentJobsPage() {
  const postings = useAppStore((s) => s.jobPostings);
  const banned = useAppStore((s) => s.bannedPracticeIds);
  const applied = useAppStore((s) => s.appliedPostingIds);
  const apply = useAppStore((s) => s.applyToPosting);
  const banPractice = useAppStore((s) => s.banPractice);
  const [q, setQ] = useState("");

  const list = useMemo(
    () =>
      postings.filter(
        (p): p is PermanentJobPosting =>
          p.kind === "Permanent" &&
          p.status === "Open" &&
          !banned.includes(p.practiceId) &&
          (q
            ? `${p.title} ${p.specialty} ${p.subcategory}`
                .toLowerCase()
                .includes(q.toLowerCase())
            : true)
      ),
    [postings, banned, q]
  );

  return (
    <div className="space-y-6 p-6">
      <header className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <p className="text-xs font-medium uppercase tracking-wider text-primary">Assignments</p>
          <h1 className="mt-1 text-2xl font-semibold tracking-tight">Permanent jobs</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            Long-term placements with salary & benefits.
          </p>
        </div>
        <div className="relative w-full max-w-xs">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="Search role…"
            className="pl-9"
          />
        </div>
      </header>

      {list.length === 0 ? (
        <Card className="flex flex-col items-center gap-2 p-10 text-center text-sm text-muted-foreground">
          <Sparkles className="h-6 w-6 text-primary" />
          No permanent jobs match your filters.
        </Card>
      ) : (
        <div className="space-y-3">
          {list.map((p, i) => {
            const loc = mockLocations.find((l) => l.id === p.locationId);
            const practice = knownPractices.find((kp) => kp.id === p.practiceId);
            const isApplied = applied.includes(p.id);
            return (
              <motion.div
                key={p.id}
                initial={{ opacity: 0, y: 8 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: i * 0.04 }}
              >
                <Card className="flex flex-wrap items-center gap-4 p-5">
                  <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary">
                    <Building2 className="h-5 w-5" />
                  </div>
                  <div className="min-w-0 flex-1">
                    <div className="flex flex-wrap items-center gap-2">
                      <h3 className="text-sm font-semibold">{p.title}</h3>
                      <Badge variant="outline" className="border-primary/40 bg-primary/10 text-primary">
                        {p.subcategory}
                      </Badge>
                      <Badge variant="outline" className="border-indigo-500/40 bg-indigo-500/10 text-indigo-600 dark:text-indigo-400">
                        {p.fullTime ? "Full-time" : "Part-time"}
                      </Badge>
                    </div>
                    <div className="mt-1.5 flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-muted-foreground">
                      <span className="flex items-center gap-1.5">
                        <MapPin className="h-3 w-3" /> {practice?.name} · {loc?.address.city}
                      </span>
                      <span className="flex items-center gap-1.5">
                        <CalendarRange className="h-3 w-3" /> Start{" "}
                        {new Date(p.startDate).toLocaleDateString()}
                      </span>
                      <span>
                        Benefits: {p.benefits.slice(0, 3).join(" · ") || "—"}
                      </span>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-xs uppercase tracking-wider text-muted-foreground">Salary</p>
                    <p className="text-sm font-semibold">
                      ${(p.salaryRange.min / 1000).toFixed(0)}k – $
                      {(p.salaryRange.max / 1000).toFixed(0)}k
                    </p>
                    <p className="mt-0.5 text-[11px] text-primary">{p.matchPercentage}% match</p>
                  </div>
                  <div className="flex gap-2">
                    <Button
                      size="sm"
                      disabled={isApplied}
                      variant={isApplied ? "outline" : "default"}
                      onClick={() => {
                        apply(p.id);
                        toast.success("Application sent", { description: p.title });
                      }}
                    >
                      {isApplied ? (
                        <>
                          <Check className="h-3.5 w-3.5" /> Applied
                        </>
                      ) : (
                        "Apply"
                      )}
                    </Button>
                    <Button
                      size="sm"
                      variant="ghost"
                      onClick={() => {
                        banPractice(p.practiceId);
                        toast("Practice hidden", {
                          description: `${practice?.name ?? "Practice"} blocked from your feed.`,
                        });
                      }}
                    >
                      <Ban className="h-3.5 w-3.5" />
                    </Button>
                  </div>
                </Card>
              </motion.div>
            );
          })}
        </div>
      )}
    </div>
  );
}
