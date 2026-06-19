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
  DollarSign,
  TrendingUp,
  Zap,
  Info,
} from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { useAppStore, knownPractices } from "@/lib/store/app-store";
import { mockLocations } from "@/lib/mock";
import type { PermanentJobPosting } from "@/lib/types/mdd";
import { toast } from "sonner";
import { PracticeOwnerSheet } from "@/components/professional/practice-owner-sheet";

export const Route = createFileRoute("/professional/permanent-jobs")({
  component: PermanentJobsPage,
});

// ─── constants ───────────────────────────────────────────────────────────────

type SortMode = "distance" | "pay" | "value" | "smart";

const PRO_LAT = 37.7845;
const PRO_LNG = -122.4323;

function haversine(lat1: number, lng1: number, lat2: number, lng2: number) {
  const R = 3959;
  const dLat = ((lat2 - lat1) * Math.PI) / 180;
  const dLng = ((lng2 - lng1) * Math.PI) / 180;
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos((lat1 * Math.PI) / 180) *
      Math.cos((lat2 * Math.PI) / 180) *
      Math.sin(dLng / 2) ** 2;
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}

const sortModes: { id: SortMode; label: string; icon: typeof MapPin; desc: string }[] = [
  { id: "smart",    label: "Smart",    icon: Sparkles,   desc: "Weighted: match + salary + distance" },
  { id: "distance", label: "Distance", icon: MapPin,     desc: "Closest jobs first" },
  { id: "pay",      label: "Pay",      icon: DollarSign, desc: "Highest salary first" },
  { id: "value",    label: "Value",    icon: TrendingUp, desc: "Best salary/mile ratio" },
];

// ─── page ────────────────────────────────────────────────────────────────────

function PermanentJobsPage() {
  const postings = useAppStore((s) => s.jobPostings);
  const banned = useAppStore((s) => s.bannedPracticeIds);
  const hidden = useAppStore((s) => s.hiddenPostingIds);
  const applied = useAppStore((s) => s.appliedPostingIds);
  const apply = useAppStore((s) => s.applyToPosting);
  const hidePosting = useAppStore((s) => s.hidePosting);

  const [q, setQ] = useState("");
  const [sort, setSort] = useState<SortMode>("smart");
  const [selectedPracticeId, setSelectedPracticeId] = useState<string | null>(null);
  const [selectedPostingId, setSelectedPostingId] = useState<string | undefined>();

  // Filtered list
  const filtered = useMemo(
    () =>
      postings.filter(
        (p): p is PermanentJobPosting =>
          p.kind === "Permanent" &&
          p.status === "Open" &&
          !banned.includes(p.practiceId) &&
          !hidden.includes(p.id) &&
          (q
            ? `${p.title} ${p.specialty} ${p.subcategory}`.toLowerCase().includes(q.toLowerCase())
            : true)
      ),
    [postings, banned, hidden, q]
  );

  // Sorted list — reactive, auto-applies on sort change
  const list = useMemo(() => {
    const withDist = filtered.map((p) => {
      const loc = mockLocations.find((l) => l.id === p.locationId);
      const dist = loc ? haversine(PRO_LAT, PRO_LNG, loc.lat, loc.lng) : 8;
      return { posting: p, dist };
    });

    switch (sort) {
      case "distance":
        return [...withDist].sort((a, b) => a.dist - b.dist).map((x) => x.posting);

      case "pay":
        return [...filtered].sort((a, b) => b.salaryRange.max - a.salaryRange.max);

      case "value":
        return [...withDist]
          .sort(
            (a, b) =>
              b.posting.salaryRange.max / b.dist - a.posting.salaryRange.max / a.dist
          )
          .map((x) => x.posting);

      case "smart":
      default: {
        const maxSal = Math.max(...filtered.map((p) => p.salaryRange.max), 1);
        const maxMatch = Math.max(...filtered.map((p) => p.matchPercentage), 1);
        return [...withDist]
          .map(({ posting, dist }) => ({
            posting,
            score:
              (posting.matchPercentage / maxMatch) * 0.4 +
              (posting.salaryRange.max / maxSal) * 0.35 +
              (1 / Math.max(dist, 0.5)) * 0.25,
          }))
          .sort((a, b) => b.score - a.score)
          .map((x) => x.posting);
      }
    }
  }, [filtered, sort]);

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

      {/* Sort Bar */}
      <SortBar value={sort} onChange={setSort} />

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
            const dist = loc ? haversine(PRO_LAT, PRO_LNG, loc.lat, loc.lng) : null;
            return (
              <motion.div
                key={p.id}
                layout
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
                      <Badge
                        variant="outline"
                        className="border-indigo-500/40 bg-indigo-500/10 text-indigo-600 dark:text-indigo-400"
                      >
                        {p.fullTime ? "Full-time" : "Part-time"}
                      </Badge>
                    </div>
                    <div className="mt-1.5 flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-muted-foreground">
                      <span className="flex items-center gap-1.5">
                        <MapPin className="h-3 w-3" />
                        {practice?.name} · {loc?.address.city}
                        {dist && ` · ${dist.toFixed(1)} mi`}
                      </span>
                      <span className="flex items-center gap-1.5">
                        <CalendarRange className="h-3 w-3" />
                        Start {new Date(p.startDate).toLocaleDateString()}
                      </span>
                      <span>Benefits: {p.benefits.slice(0, 3).join(" · ") || "—"}</span>
                    </div>
                    {/* Sort highlight */}
                    {sort !== "smart" && dist && (
                      <div className="mt-2">
                        <SortHighlight mode={sort} posting={p} dist={dist} />
                      </div>
                    )}
                  </div>
                  <div className="text-right">
                    <p className="text-xs uppercase tracking-wider text-muted-foreground">Salary</p>
                    <p className="text-sm font-semibold">
                      ${(p.salaryRange.min / 1000).toFixed(0)}k – ${(p.salaryRange.max / 1000).toFixed(0)}k
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
                        <><Check className="h-3.5 w-3.5" /> Applied</>
                      ) : (
                        "Apply"
                      )}
                    </Button>
                    <Button
                      size="sm"
                      variant="ghost"
                      title="Practice info"
                      onClick={() => {
                        setSelectedPracticeId(p.practiceId);
                        setSelectedPostingId(p.id);
                      }}
                    >
                      <Info className="h-3.5 w-3.5" />
                    </Button>
                    <Button
                      size="sm"
                      variant="ghost"
                      onClick={() => {
                        hidePosting(p.id);
                        toast("Job hidden", { description: `"${p.title}" removed from your feed.` });
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

      <PracticeOwnerSheet
        practiceId={selectedPracticeId}
        highlightPostingId={selectedPostingId}
        onOpenChange={(open) => {
          if (!open) {
            setSelectedPracticeId(null);
            setSelectedPostingId(undefined);
          }
        }}
      />
    </div>
  );
}

// ─── sort bar ────────────────────────────────────────────────────────────────

function SortBar({ value, onChange }: { value: SortMode; onChange: (m: SortMode) => void }) {
  return (
    <div className="flex flex-wrap items-center gap-2">
      <span className="text-xs font-medium text-muted-foreground uppercase tracking-wider">Sort by</span>
      <div className="flex flex-wrap gap-1.5">
        {sortModes.map((m) => {
          const active = value === m.id;
          return (
            <button
              key={m.id}
              title={m.desc}
              onClick={() => onChange(m.id)}
              className={`flex items-center gap-1.5 rounded-full border px-3 py-1 text-xs font-medium transition-all ${
                active
                  ? "border-primary/50 bg-primary/10 text-primary shadow-sm"
                  : "border-border/60 bg-card text-muted-foreground hover:text-foreground hover:border-border"
              }`}
            >
              <m.icon className="h-3.5 w-3.5" />
              {m.label}
              {active && <Zap className="h-3 w-3 text-primary/70" />}
            </button>
          );
        })}
      </div>
    </div>
  );
}

function SortHighlight({
  mode,
  posting,
  dist,
}: {
  mode: SortMode;
  posting: PermanentJobPosting;
  dist: number;
}) {
  const map: Partial<Record<SortMode, { label: string; val: string; color: string }>> = {
    distance: {
      label: "Distance",
      val: `${dist.toFixed(1)} mi`,
      color: "text-indigo-600 dark:text-indigo-400 bg-indigo-500/10 border-indigo-500/30",
    },
    pay: {
      label: "Max salary",
      val: `$${(posting.salaryRange.max / 1000).toFixed(0)}k/yr`,
      color: "text-emerald-600 dark:text-emerald-400 bg-emerald-500/10 border-emerald-500/30",
    },
    value: {
      label: "Value",
      val: `$${(posting.salaryRange.max / dist).toFixed(0)}k/mi`,
      color: "text-amber-600 dark:text-amber-400 bg-amber-500/10 border-amber-500/30",
    },
  };
  const info = map[mode];
  if (!info) return null;
  return (
    <div
      className={`inline-flex items-center gap-1.5 rounded-full border px-2 py-0.5 text-[11px] font-medium ${info.color}`}
    >
      <TrendingUp className="h-3 w-3" />
      {info.label}: <span className="font-bold">{info.val}</span>
    </div>
  );
}
