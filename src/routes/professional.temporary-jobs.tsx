import { useMemo, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { motion, AnimatePresence } from "motion/react";
import {
  Briefcase,
  Calendar,
  Clock,
  MapPin,
  DollarSign,
  Ban,
  Sparkles,
  Search,
  Check,
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
import type { TemporaryJobPosting } from "@/lib/types/mdd";
import { toast } from "sonner";
import { PracticeOwnerSheet } from "@/components/professional/practice-owner-sheet";

export const Route = createFileRoute("/professional/temporary-jobs")({
  component: TemporaryJobsPage,
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
  { id: "smart",    label: "Smart",    icon: Sparkles,   desc: "Weighted: match + pay + distance" },
  { id: "distance", label: "Distance", icon: MapPin,     desc: "Closest jobs first" },
  { id: "pay",      label: "Pay",      icon: DollarSign, desc: "Highest hourly rate first" },
  { id: "value",    label: "Value",    icon: TrendingUp, desc: "Best $/mile ratio" },
];

// ─── page ────────────────────────────────────────────────────────────────────

function TemporaryJobsPage() {
  const postings = useAppStore((s) => s.jobPostings);
  const banned = useAppStore((s) => s.bannedPracticeIds);
  const applied = useAppStore((s) => s.appliedPostingIds);
  const apply = useAppStore((s) => s.applyToPosting);
  const banPractice = useAppStore((s) => s.banPractice);

  const [q, setQ] = useState("");
  const [sort, setSort] = useState<SortMode>("smart");
  const [selectedPracticeId, setSelectedPracticeId] = useState<string | null>(null);
  const [selectedPostingId, setSelectedPostingId] = useState<string | undefined>();

  // Filtered list
  const filtered = useMemo(
    () =>
      postings.filter(
        (p): p is TemporaryJobPosting =>
          p.kind === "Temporary" &&
          p.status === "Open" &&
          !banned.includes(p.practiceId) &&
          (q
            ? `${p.title} ${p.specialty} ${p.subcategory}`.toLowerCase().includes(q.toLowerCase())
            : true)
      ),
    [postings, banned, q]
  );

  // Sorted list — reactive, no button press needed
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
        return [...filtered].sort((a, b) => b.hourlyRate - a.hourlyRate);

      case "value":
        return [...withDist]
          .sort((a, b) => b.posting.hourlyRate / b.dist - a.posting.hourlyRate / a.dist)
          .map((x) => x.posting);

      case "smart":
      default: {
        const maxRate = Math.max(...filtered.map((p) => p.hourlyRate), 1);
        const maxMatch = Math.max(...filtered.map((p) => p.matchPercentage), 1);
        return [...withDist]
          .map(({ posting, dist }) => ({
            posting,
            score:
              (posting.matchPercentage / maxMatch) * 0.4 +
              (posting.hourlyRate / maxRate) * 0.35 +
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
          <h1 className="mt-1 text-2xl font-semibold tracking-tight">Temporary jobs</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            Short-term shifts near you. Apply with one click.
          </p>
        </div>
        <div className="relative w-full max-w-xs">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="Search role, title…"
            className="pl-9"
          />
        </div>
      </header>

      {/* Sort Bar */}
      <SortBar value={sort} onChange={setSort} />

      {list.length === 0 ? (
        <Card className="flex flex-col items-center gap-2 p-10 text-center text-sm text-muted-foreground">
          <Sparkles className="h-6 w-6 text-primary" />
          No temporary jobs match right now. Check back soon.
        </Card>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
          <AnimatePresence mode="popLayout">
            {list.map((p, i) => (
              <motion.div
                key={p.id}
                layout
                initial={{ opacity: 0, y: 12 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, scale: 0.95 }}
                transition={{ duration: 0.28, delay: Math.min(i * 0.03, 0.2) }}
              >
                <TempCard
                  posting={p}
                  applied={applied.includes(p.id)}
                  sortMode={sort}
                  onApply={() => {
                    apply(p.id);
                    toast.success("Application sent", { description: p.title });
                  }}
                  onBan={() => {
                    banPractice(p.practiceId);
                    const name = knownPractices.find((kp) => kp.id === p.practiceId)?.name ?? "Practice";
                    toast("Practice hidden", { description: `${name} blocked from your feed.` });
                  }}
                  onInfo={() => {
                    setSelectedPracticeId(p.practiceId);
                    setSelectedPostingId(p.id);
                  }}
                />
              </motion.div>
            ))}
          </AnimatePresence>
        </div>
      )}

      <PracticeOwnerSheet
        practiceId={selectedPracticeId}
        highlightPostingId={selectedPostingId}
        onOpenChange={(open) => { if (!open) { setSelectedPracticeId(null); setSelectedPostingId(undefined); } }}
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

// ─── card ────────────────────────────────────────────────────────────────────

function TempCard({
  posting,
  applied,
  sortMode,
  onApply,
  onBan,
  onInfo,
}: {
  posting: TemporaryJobPosting;
  applied: boolean;
  sortMode: SortMode;
  onApply: () => void;
  onBan: () => void;
  onInfo: () => void;
}) {
  const loc = mockLocations.find((l) => l.id === posting.locationId);
  const practice = knownPractices.find((p) => p.id === posting.practiceId);
  const firstDay = posting.days[0];
  const totalHours = posting.days.reduce((s, d) => {
    const [sh, sm] = d.startTime.split(":").map(Number);
    const [eh, em] = d.endTime.split(":").map(Number);
    return s + (eh + em / 60 - sh - sm / 60 - d.breakMinutes / 60);
  }, 0);
  const dist = loc ? haversine(PRO_LAT, PRO_LNG, loc.lat, loc.lng) : null;
  const value = dist && dist > 0 ? (posting.hourlyRate / dist).toFixed(1) : null;

  return (
    <Card className="group relative flex h-full flex-col overflow-hidden p-0">
      <div className="relative flex items-start justify-between gap-3 border-b border-border/60 bg-gradient-to-br from-primary/5 to-transparent p-4">
        <div className="min-w-0">
          <Badge variant="outline" className="border-primary/40 bg-primary/10 text-primary">
            {posting.subcategory}
          </Badge>
          <h3 className="mt-2 truncate text-sm font-semibold">{posting.title}</h3>
          <p className="mt-0.5 text-xs text-muted-foreground">{practice?.name ?? "Practice"}</p>
        </div>
        <div className="text-right">
          <p className="text-xs uppercase tracking-wider text-muted-foreground">Match</p>
          <p className="text-base font-semibold text-primary">{posting.matchPercentage}%</p>
        </div>
      </div>

      <div className="flex flex-1 flex-col gap-2 p-4 text-xs">
        <Row icon={MapPin} text={loc ? `${loc.name} · ${loc.address.city}` : "—"} />
        <Row
          icon={Calendar}
          text={new Date(firstDay.date).toLocaleDateString("en-US", {
            weekday: "short",
            month: "short",
            day: "numeric",
          })}
        />
        <Row
          icon={Clock}
          text={`${firstDay.startTime} – ${firstDay.endTime} · ${posting.days.length} day${posting.days.length > 1 ? "s" : ""}`}
        />
        <Row
          icon={DollarSign}
          text={`$${posting.hourlyRate}/hr · ~$${Math.round(posting.hourlyRate * totalHours)} total`}
        />
        {dist && (
          <Row
            icon={MapPin}
            text={`${dist.toFixed(1)} mi away${value ? ` · $${value}/mile` : ""}`}
          />
        )}
      </div>

      {/* Sort highlight badge */}
      {sortMode !== "smart" && (
        <div className="px-4 pb-2">
          <SortHighlight mode={sortMode} posting={posting} dist={dist} value={value} />
        </div>
      )}

      <div className="flex items-center gap-2 border-t border-border/60 bg-muted/30 p-3">
        <Button
          size="sm"
          className="flex-1"
          disabled={applied}
          onClick={onApply}
          variant={applied ? "outline" : "default"}
        >
          {applied ? (
            <><Check className="h-3.5 w-3.5" /> Applied</>
          ) : (
            <><Briefcase className="h-3.5 w-3.5" /> Accept shift</>
          )}
        </Button>
        <Button size="sm" variant="ghost" onClick={onInfo} title="Practice info">
          <Info className="h-3.5 w-3.5" />
        </Button>
        <Button size="sm" variant="ghost" onClick={onBan} title="Ban practice">
          <Ban className="h-3.5 w-3.5" />
        </Button>
      </div>
    </Card>
  );
}

function SortHighlight({
  mode,
  posting,
  dist,
  value,
}: {
  mode: SortMode;
  posting: TemporaryJobPosting;
  dist: number | null;
  value: string | null;
}) {
  const map: Partial<Record<SortMode, { label: string; val: string; color: string }>> = {
    distance: {
      label: "Distance",
      val: dist ? `${dist.toFixed(1)} mi` : "—",
      color: "text-indigo-600 dark:text-indigo-400 bg-indigo-500/10 border-indigo-500/30",
    },
    pay: {
      label: "Hourly rate",
      val: `$${posting.hourlyRate}/hr`,
      color: "text-emerald-600 dark:text-emerald-400 bg-emerald-500/10 border-emerald-500/30",
    },
    value: {
      label: "Value",
      val: value ? `$${value}/mi` : "—",
      color: "text-amber-600 dark:text-amber-400 bg-amber-500/10 border-amber-500/30",
    },
  };
  const info = map[mode];
  if (!info) return null;
  return (
    <div className={`inline-flex items-center gap-1.5 rounded-full border px-2 py-0.5 text-[11px] font-medium ${info.color}`}>
      <TrendingUp className="h-3 w-3" />
      {info.label}: <span className="font-bold">{info.val}</span>
    </div>
  );
}

function Row({ icon: Icon, text }: { icon: typeof MapPin; text: string }) {
  return (
    <div className="flex items-center gap-2 text-muted-foreground">
      <Icon className="h-3.5 w-3.5 shrink-0" />
      <span className="truncate text-foreground">{text}</span>
    </div>
  );
}
