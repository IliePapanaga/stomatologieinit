import { useMemo, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { motion } from "motion/react";
import {
  Briefcase,
  Clock4,
  MapPin,
  Users,
  CalendarDays,
  Search,
  Sparkles,
} from "lucide-react";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";
import { NewPostingSheet } from "@/components/practice/new-posting-sheet";
import { CandidatesSheet } from "@/components/practice/candidates-sheet";
import { usePostings, useRemovePosting } from "@/lib/hooks/postings";
import { mockLocations } from "@/lib/mock";
import { Trash2 } from "lucide-react";
import type {
  JobPosting,
  PermanentJobPosting,
  PostingStatus,
  ProfessionalSubcategory,
  TemporaryJobPosting,
} from "@/lib/types/mdd";

export const Route = createFileRoute("/practice/postings")({
  component: PostingsPage,
});

const formatSub = (s: ProfessionalSubcategory) =>
  s.replace(/([A-Z])/g, " $1").trim();

const fmtDate = (iso: string) =>
  new Date(iso).toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" });

const statusStyles: Record<PostingStatus, string> = {
  Open: "bg-emerald-500/15 text-emerald-600 dark:text-emerald-400 border-emerald-500/30",
  Filled: "bg-primary/15 text-primary border-primary/30",
  Draft: "bg-muted text-muted-foreground border-border",
  Cancelled: "bg-rose-500/15 text-rose-600 dark:text-rose-400 border-rose-500/30",
  Expired: "bg-amber-500/15 text-amber-600 dark:text-amber-400 border-amber-500/30",
};

function PostingsPage() {
  const { data, isLoading } = usePostings();
  const [tab, setTab] = useState<"Permanent" | "Temporary">("Permanent");
  const [q, setQ] = useState("");
  const [spaceFilter, setSpaceFilter] = useState<"All" | "Current" | "Full" | "Past">("All");

  const filtered = useMemo(() => {
    if (!data) return [];
    const term = q.trim().toLowerCase();
    const now = new Date().getTime();
    
    return data
      .filter((p) => p.kind === tab)
      .filter((p) => {
        if (spaceFilter === "All") return true;
        const endDate = p.endDate ? new Date(p.endDate).getTime() : new Date(p.startDate).getTime() + 86400000;
        const isPast = endDate < now;
        if (spaceFilter === "Past") return isPast;
        if (isPast) return false; // if it's not past filter, exclude past events from Current/Full
        const spotsLeft = p.workingSpaces - (p.hiredCandidateIds?.length || 0);
        if (spaceFilter === "Full") return spotsLeft <= 0;
        if (spaceFilter === "Current") return spotsLeft > 0;
        return true;
      })
      .filter((p) =>
        !term
          ? true
          : (p.title ?? "").toLowerCase().includes(term) ||
            p.specialty.toLowerCase().includes(term) ||
            p.subcategory.toLowerCase().includes(term)
      );
  }, [data, tab, q, spaceFilter]);

  const counts = useMemo(() => {
    const perm = data?.filter((p) => p.kind === "Permanent").length ?? 0;
    const temp = data?.filter((p) => p.kind === "Temporary").length ?? 0;
    return { perm, temp };
  }, [data]);

  return (
    <div className="space-y-6 p-6">
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">Roles</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            Manage permanent roles and temporary shifts. Live match scores update as candidates apply.
          </p>
        </div>
        <NewPostingSheet />
      </div>

      <Tabs value={tab} onValueChange={(v) => setTab(v as "Permanent" | "Temporary")}>
        <div className="flex flex-wrap items-center justify-between gap-3">
          <TabsList>
            <TabsTrigger value="Permanent" className="gap-2">
              <Briefcase className="h-3.5 w-3.5" />
              Permanent
              <Badge variant="secondary" className="ml-1 h-5 px-1.5 text-[10px]">
                {counts.perm}
              </Badge>
            </TabsTrigger>
            <TabsTrigger value="Temporary" className="gap-2">
              <Clock4 className="h-3.5 w-3.5" />
              Temporary
              <Badge variant="secondary" className="ml-1 h-5 px-1.5 text-[10px]">
                {counts.temp}
              </Badge>
            </TabsTrigger>
          </TabsList>
          <div className="flex items-center gap-3 w-full sm:w-auto">
            <Select value={spaceFilter} onValueChange={(v) => setSpaceFilter(v as any)}>
              <SelectTrigger className="w-[120px]"><SelectValue /></SelectTrigger>
              <SelectContent>
                <SelectItem value="All">All statuses</SelectItem>
                <SelectItem value="Current">Current</SelectItem>
                <SelectItem value="Full">Full</SelectItem>
                <SelectItem value="Past">Past</SelectItem>
              </SelectContent>
            </Select>
            <div className="relative w-full sm:w-[200px]">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                value={q}
                onChange={(e) => setQ(e.target.value)}
                placeholder="Search title, specialty…"
                className="pl-9"
              />
            </div>
          </div>
        </div>

        <TabsContent value="Permanent" className="mt-5">
          <PostingsList
            postings={filtered as PermanentJobPosting[]}
            loading={isLoading}
            empty="No permanent postings yet"
          />
        </TabsContent>
        <TabsContent value="Temporary" className="mt-5">
          <PostingsList
            postings={filtered as TemporaryJobPosting[]}
            loading={isLoading}
            empty="No temporary shifts posted"
          />
        </TabsContent>
      </Tabs>
    </div>
  );
}

function PostingsList({
  postings,
  loading,
  empty,
}: {
  postings: JobPosting[];
  loading: boolean;
  empty: string;
}) {
  if (loading) {
    return (
      <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
        {Array.from({ length: 4 }).map((_, i) => (
          <Skeleton key={i} className="h-48 rounded-2xl" />
        ))}
      </div>
    );
  }
  if (postings.length === 0) {
    return (
      <div className="rounded-2xl border border-dashed border-border/70 p-12 text-center">
        <Sparkles className="mx-auto h-6 w-6 text-muted-foreground" />
        <p className="mt-3 text-sm font-medium">{empty}</p>
        <p className="mt-1 text-xs text-muted-foreground">
          Try a different search or create a new posting.
        </p>
      </div>
    );
  }

  return (
    <motion.div
      className="grid gap-3 md:grid-cols-2 xl:grid-cols-3"
      initial="hidden"
      animate="visible"
      variants={{
        visible: { transition: { staggerChildren: 0.06, delayChildren: 0.05 } },
      }}
    >
      {postings.map((p) => (
        <PostingCard key={p.id} posting={p} />
      ))}
    </motion.div>
  );
}

function PostingCard({ posting }: { posting: JobPosting }) {
  const [sheetMode, setSheetMode] = useState<"candidates" | "hired" | null>(null);
  const [editOpen, setEditOpen] = useState(false);
  const remove = useRemovePosting();

  const location = mockLocations.find((l) => l.id === posting.locationId);
  const isTemp = posting.kind === "Temporary";
  const matchTone =
    posting.matchPercentage >= 85
      ? "text-emerald-600 dark:text-emerald-400"
      : posting.matchPercentage >= 70
      ? "text-primary"
      : "text-amber-600 dark:text-amber-400";

  return (
    <motion.article
      variants={{
        hidden: { opacity: 0, y: 14 },
        visible: { opacity: 1, y: 0, transition: { duration: 0.35, ease: [0.22, 1, 0.36, 1] } },
      }}
      whileHover={{ y: -3 }}
      className="group relative flex flex-col rounded-2xl border border-border/70 bg-card p-5 shadow-sm transition-shadow hover:shadow-md"
    >
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <div className="flex items-center gap-2">
            <Badge
              variant="outline"
              className={`h-5 px-2 text-[10px] font-medium ${statusStyles[posting.status]}`}
            >
              {posting.status}
            </Badge>
            <span className="text-[11px] uppercase tracking-wide text-muted-foreground">
              {posting.specialty}
            </span>
          </div>
          <h3 className="mt-2 truncate text-base font-semibold text-foreground">
            {posting.title ?? `${formatSub(posting.subcategory)} role`}
          </h3>
          <p className="mt-0.5 truncate text-xs text-muted-foreground">
            {formatSub(posting.subcategory)}
          </p>
        </div>
        <div className="text-right">
          <div className={`text-xl font-bold tabular-nums ${matchTone}`}>
            {posting.matchPercentage}%
          </div>
          <div className="text-[10px] uppercase tracking-wide text-muted-foreground">
            match
          </div>
        </div>
      </div>

      <div className="mt-4 grid grid-cols-2 gap-3 text-xs">
        <Stat
          icon={<MapPin className="h-3.5 w-3.5" />}
          label={location?.name ?? "—"}
          sub={`${posting.commutingRadius} mi radius`}
        />
        <Stat
          icon={<CalendarDays className="h-3.5 w-3.5" />}
          label={fmtDate(posting.startDate)}
          sub={posting.endDate ? `→ ${fmtDate(posting.endDate)}` : isTemp ? "single shift" : "open-ended"}
        />
      </div>

      <div className="mt-4 flex items-center justify-between border-t border-border/60 pt-4">
        <div>
          <div className="text-sm font-semibold text-foreground">
            {isTemp
              ? `$${(posting as TemporaryJobPosting).hourlyRate}/hr`
              : `$${((posting as PermanentJobPosting).salaryRange.min / 1000).toFixed(0)}k – $${((posting as PermanentJobPosting).salaryRange.max / 1000).toFixed(0)}k`}
          </div>
          <div className="text-[10px] uppercase tracking-wide text-muted-foreground">
            {isTemp ? (posting as TemporaryJobPosting).temporaryKind + " schedule" : (posting as PermanentJobPosting).fullTime ? "Full-time" : "Part-time"}
          </div>
        </div>
        <div className="flex items-center gap-1.5 rounded-full bg-muted px-2.5 py-1 text-xs">
          <Users className="h-3.5 w-3.5 text-muted-foreground" />
          <span className="font-medium">{posting.applicantsCount}</span>
          <span className="text-muted-foreground">applicants</span>
        </div>
      </div>

      <div className="mt-4 flex flex-wrap items-center gap-2">
        <Button size="sm" variant="outline" className="flex-1 bg-primary/5 border-primary/20 hover:bg-primary/10 text-primary" onClick={() => setSheetMode("candidates")}>
          View candidates
        </Button>
        <Button size="sm" variant="outline" className="flex-1 border-primary/20 hover:bg-primary/5 text-primary" onClick={() => setSheetMode("hired")}>
          Hired ({posting.hiredCandidateIds?.length || 0})
        </Button>
        <Button size="sm" variant="ghost" className="text-muted-foreground" onClick={() => setEditOpen(true)}>
          Edit
        </Button>
        <Button 
          size="sm" 
          variant="ghost" 
          className="text-destructive hover:bg-destructive/10 hover:text-destructive px-2"
          onClick={() => {
            if (window.confirm("Are you sure you want to delete this role?")) {
              remove.mutate(posting.id);
            }
          }}
        >
          <Trash2 className="h-4 w-4" />
        </Button>
      </div>

      <CandidatesSheet
        posting={sheetMode ? posting : null}
        mode={sheetMode || "candidates"}
        onOpenChange={(v) => { if (!v) setSheetMode(null); }}
      />
      <NewPostingSheet
        open={editOpen}
        onOpenChange={setEditOpen}
        initialData={posting}
        hideTrigger
      />
    </motion.article>
  );
}

function Stat({
  icon,
  label,
  sub,
}: {
  icon: React.ReactNode;
  label: string;
  sub?: string;
}) {
  return (
    <div className="flex items-start gap-2">
      <div className="mt-0.5 flex h-6 w-6 items-center justify-center rounded-md bg-muted text-muted-foreground">
        {icon}
      </div>
      <div className="min-w-0">
        <div className="truncate font-medium text-foreground">{label}</div>
        {sub && <div className="truncate text-[11px] text-muted-foreground">{sub}</div>}
      </div>
    </div>
  );
}
