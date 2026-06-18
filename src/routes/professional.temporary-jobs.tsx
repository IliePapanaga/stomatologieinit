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
} from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { useAppStore, knownPractices } from "@/lib/store/app-store";
import { mockLocations } from "@/lib/mock";
import type { TemporaryJobPosting } from "@/lib/types/mdd";
import { toast } from "sonner";

export const Route = createFileRoute("/professional/temporary-jobs")({
  component: TemporaryJobsPage,
});

function TemporaryJobsPage() {
  const postings = useAppStore((s) => s.jobPostings);
  const banned = useAppStore((s) => s.bannedPracticeIds);
  const applied = useAppStore((s) => s.appliedPostingIds);
  const apply = useAppStore((s) => s.applyToPosting);
  const banPractice = useAppStore((s) => s.banPractice);
  const [q, setQ] = useState("");

  const list = useMemo(
    () =>
      postings.filter(
        (p): p is TemporaryJobPosting =>
          p.kind === "Temporary" &&
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
                  onApply={() => {
                    apply(p.id);
                    toast.success("Application sent", { description: p.title });
                  }}
                  onBan={() => {
                    banPractice(p.practiceId);
                    const name =
                      knownPractices.find((kp) => kp.id === p.practiceId)?.name ?? "Practice";
                    toast("Practice hidden", { description: `${name} blocked from your feed.` });
                  }}
                />
              </motion.div>
            ))}
          </AnimatePresence>
        </div>
      )}
    </div>
  );
}

function TempCard({
  posting,
  applied,
  onApply,
  onBan,
}: {
  posting: TemporaryJobPosting;
  applied: boolean;
  onApply: () => void;
  onBan: () => void;
}) {
  const loc = mockLocations.find((l) => l.id === posting.locationId);
  const practice = knownPractices.find((p) => p.id === posting.practiceId);
  const firstDay = posting.days[0];
  const totalHours = posting.days.reduce((s, d) => {
    const [sh, sm] = d.startTime.split(":").map(Number);
    const [eh, em] = d.endTime.split(":").map(Number);
    return s + (eh + em / 60 - sh - sm / 60 - d.breakMinutes / 60);
  }, 0);

  return (
    <Card className="group relative flex h-full flex-col overflow-hidden p-0">
      <div className="relative flex items-start justify-between gap-3 border-b border-border/60 bg-gradient-to-br from-primary/5 to-transparent p-4">
        <div className="min-w-0">
          <Badge
            variant="outline"
            className="border-primary/40 bg-primary/10 text-primary"
          >
            {posting.subcategory}
          </Badge>
          <h3 className="mt-2 truncate text-sm font-semibold">{posting.title}</h3>
          <p className="mt-0.5 text-xs text-muted-foreground">
            {practice?.name ?? "Practice"}
          </p>
        </div>
        <div className="text-right">
          <p className="text-xs uppercase tracking-wider text-muted-foreground">Match</p>
          <p className="text-base font-semibold text-primary">{posting.matchPercentage}%</p>
        </div>
      </div>

      <div className="flex flex-1 flex-col gap-2 p-4 text-xs">
        <Row icon={MapPin} text={loc ? `${loc.name} · ${loc.address.city}` : "—"} />
        <Row icon={Calendar} text={new Date(firstDay.date).toLocaleDateString("en-US", { weekday: "short", month: "short", day: "numeric" })} />
        <Row icon={Clock} text={`${firstDay.startTime} – ${firstDay.endTime} · ${posting.days.length} day${posting.days.length > 1 ? "s" : ""}`} />
        <Row icon={DollarSign} text={`$${posting.hourlyRate}/hr · ~$${Math.round(posting.hourlyRate * totalHours)} total`} />
      </div>

      <div className="flex items-center gap-2 border-t border-border/60 bg-muted/30 p-3">
        <Button
          size="sm"
          className="flex-1"
          disabled={applied}
          onClick={onApply}
          variant={applied ? "outline" : "default"}
        >
          {applied ? (
            <>
              <Check className="h-3.5 w-3.5" /> Applied
            </>
          ) : (
            <>
              <Briefcase className="h-3.5 w-3.5" /> Accept shift
            </>
          )}
        </Button>
        <Button size="sm" variant="ghost" onClick={onBan} title="Ban practice">
          <Ban className="h-3.5 w-3.5" />
        </Button>
      </div>
    </Card>
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
