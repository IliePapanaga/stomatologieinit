import { useMemo, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { motion } from "motion/react";
import {
  ChevronLeft,
  ChevronRight,
  CalendarDays,
  Clock,
  MapPin,
  DollarSign,
  Briefcase,
  Building2,
} from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { useAppStore } from "@/lib/store/app-store";
import { mockLocations, mockPostings } from "@/lib/mock";
import type { TemporaryJobPosting } from "@/lib/types/mdd";
import { PracticeOwnerSheet } from "@/components/professional/practice-owner-sheet";
import { knownPractices } from "@/lib/store/app-store";
import { useTranslation } from "react-i18next";
import i18n from "@/lib/i18n";

export const Route = createFileRoute("/professional/schedule")({
  component: ProfessionalSchedulePage,
});

// ─── types ──────────────────────────────────────────────────────────────────

interface ProCalEvent {
  id: string;
  postingId: string;
  practiceId: string;
  locationId: string;
  title: string;
  start: Date;
  end: Date;
  hourlyRate: number;
  status: "upcoming" | "past" | "today";
}

// ─── helpers ────────────────────────────────────────────────────────────────

const sameDay = (a: Date, b: Date) =>
  a.getFullYear() === b.getFullYear() &&
  a.getMonth() === b.getMonth() &&
  a.getDate() === b.getDate();

const monthLabel = (d: Date) => d.toLocaleDateString("en-US", { month: "long", year: "numeric" });

const weekRangeLabel = (start: Date, end: Date) => {
  const sameMonth = start.getMonth() === end.getMonth();
  const s = start.toLocaleDateString("en-US", { month: "short", day: "numeric" });
  const e = end.toLocaleDateString("en-US", {
    month: sameMonth ? undefined : "short",
    day: "numeric",
    year: "numeric",
  });
  return `${s} – ${e}`;
};

const formatTime = (d: Date) =>
  d.toLocaleTimeString("en-US", { hour: "numeric", minute: "2-digit" });

function getEventStatus(start: Date, end: Date): ProCalEvent["status"] {
  const now = new Date();
  if (sameDay(start, now)) return "today";
  if (start > now) return "upcoming";
  return "past";
}

const statusStyles = {
  upcoming: {
    chip: "bg-primary/10 text-primary border-primary/30 hover:bg-primary/15",
    bar: "bg-primary",
  },
  today: {
    chip: "bg-emerald-500/10 text-emerald-700 dark:text-emerald-400 border-emerald-500/30 hover:bg-emerald-500/15",
    bar: "bg-emerald-500",
  },
  past: {
    chip: "bg-muted/60 text-muted-foreground border-border/40 hover:bg-muted",
    bar: "bg-muted-foreground/30",
  },
};

// Seeded filler events to make calendar look realistic
function seed(n: number) {
  const x = Math.sin(n * 31 + 127) * 10000;
  return x - Math.floor(x);
}

function buildProEvents(
  appliedPostings: TemporaryJobPosting[],
  monthAnchor: Date,
  historyDates: {
    date: string;
    practiceId: string;
    role: string;
    hours: number;
    earnings: number;
  }[],
): ProCalEvent[] {
  const year = monthAnchor.getFullYear();
  const month = monthAnchor.getMonth();
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const now = new Date();
  const events: ProCalEvent[] = [];

  // Real applied postings
  for (const posting of appliedPostings) {
    for (const day of posting.days) {
      const d = new Date(day.date);
      if (d.getMonth() === month && d.getFullYear() === year) {
        const start = new Date(`${day.date}T${day.startTime}`);
        const end = new Date(`${day.date}T${day.endTime}`);
        events.push({
          id: `app-${posting.id}-${day.date}`,
          postingId: posting.id,
          practiceId: posting.practiceId,
          locationId: posting.locationId,
          title: posting.title ?? posting.subcategory,
          start,
          end,
          hourlyRate: posting.hourlyRate,
          status: getEventStatus(start, end),
        });
      }
    }
  }

  // Past shifts from job history
  for (const h of historyDates) {
    const d = new Date(h.date);
    if (d.getMonth() === month && d.getFullYear() === year) {
      const existsAlready = events.some((e) => sameDay(e.start, d));
      if (!existsAlready) {
        const start = new Date(`${h.date}T08:00`);
        const end = new Date(`${h.date}T${String(8 + h.hours).padStart(2, "0")}:00`);
        events.push({
          id: `hist-${h.date}`,
          postingId: `post_h`,
          practiceId: h.practiceId,
          locationId: "loc_001",
          title: h.role,
          start,
          end,
          hourlyRate: h.hours > 0 ? Math.round(h.earnings / h.hours) : 0,
          status: "past",
        });
      }
    }
  }

  // Generated filler events to fill calendar
  const usedDays = new Set(events.map((e) => e.start.getDate()));
  const titles = ["RDH Coverage", "EFDA Shift", "Hygienist · Temp", "Dental Hygienist", "RDH · AM"];
  const practices = ["prc_001", "prc_002", "prc_003", "prc_004"];

  for (let i = 0; i < 10; i++) {
    const day = 1 + Math.floor(seed(i + 42) * (daysInMonth - 1));
    if (usedDays.has(day)) continue;
    usedDays.add(day);
    const hour = 7 + Math.floor(seed(i + 142) * 4);
    const duration = 6 + Math.floor(seed(i + 242) * 3);
    const rate = 38 + Math.floor(seed(i + 342) * 30);
    const start = new Date(year, month, day, hour, 0);
    const end = new Date(year, month, day, hour + duration, 0);
    events.push({
      id: `gen-${i}`,
      postingId: `post_00${(i % 3) + 1}`,
      practiceId: practices[i % practices.length],
      locationId: i % 2 === 0 ? "loc_001" : "loc_002",
      title: titles[i % titles.length],
      start,
      end,
      hourlyRate: rate,
      status: getEventStatus(start, end),
    });
  }

  return events.sort((a, b) => a.start.getTime() - b.start.getTime());
}

// ─── page ───────────────────────────────────────────────────────────────────

function ProfessionalSchedulePage() {
  const [view, setView] = useState<"month" | "week">("month");
  const [anchor, setAnchor] = useState(() => new Date());
  const [selectedPracticeId, setSelectedPracticeId] = useState<string | null>(null);
  const [selectedPostingId, setSelectedPostingId] = useState<string | undefined>(undefined);
  const { t } = useTranslation();

  const jobPostings = useAppStore((s) => s.jobPostings);
  const appliedIds = useAppStore((s) => s.appliedPostingIds);
  const jobHistory = useAppStore((s) => s.jobHistory);
  const profile = useAppStore((s) => s.professionalProfile);

  const appliedPostings = useMemo<TemporaryJobPosting[]>(
    () =>
      [...jobPostings, ...mockPostings].filter(
        (p): p is TemporaryJobPosting => p.kind === "Temporary" && appliedIds.includes(p.id),
      ),
    [jobPostings, appliedIds],
  );

  const events = useMemo(
    () => buildProEvents(appliedPostings, anchor, jobHistory),
    [appliedPostings, anchor, jobHistory],
  );

  const handleEventClick = (evt: ProCalEvent) => {
    setSelectedPracticeId(evt.practiceId);
    setSelectedPostingId(evt.postingId);
  };

  const navigate = (dir: -1 | 1) => {
    const next = new Date(anchor);
    if (view === "month") next.setMonth(next.getMonth() + dir);
    else next.setDate(next.getDate() + dir * 7);
    setAnchor(next);
  };

  const weekStart = useMemo(() => {
    const d = new Date(anchor);
    const dow = d.getDay();
    const diff = dow === 0 ? -6 : 1 - dow;
    d.setDate(d.getDate() + diff);
    d.setHours(0, 0, 0, 0);
    return d;
  }, [anchor]);

  const weekEnd = useMemo(() => {
    const d = new Date(weekStart);
    d.setDate(d.getDate() + 6);
    return d;
  }, [weekStart]);

  const upcomingCount = events.filter(
    (e) => e.status === "upcoming" || e.status === "today",
  ).length;
  const pastCount = events.filter((e) => e.status === "past").length;

  return (
    <div className="space-y-5 p-6">
      <header className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <p className="text-xs font-medium uppercase tracking-wider text-primary">
            {t("work_schedule")}
          </p>
          <h1 className="mt-1 text-2xl font-semibold tracking-tight">{t("my_schedule_title")}</h1>
          <p className="mt-1 text-sm text-muted-foreground">{t("schedule_desc")}</p>
        </div>
        <div className="flex flex-wrap items-center gap-3">
          <Legend />
          <Button variant="outline" onClick={() => setAnchor(new Date())}>
            {t("today")}
          </Button>
        </div>
      </header>

      <Card className="border-border/70 shadow-sm">
        <div className="flex flex-wrap items-center justify-between gap-3 border-b border-border/60 px-4 py-3">
          <div className="flex items-center gap-1">
            <Button variant="ghost" size="icon" className="h-8 w-8" onClick={() => navigate(-1)}>
              <ChevronLeft className="h-4 w-4" />
            </Button>
            <Button variant="ghost" size="icon" className="h-8 w-8" onClick={() => navigate(1)}>
              <ChevronRight className="h-4 w-4" />
            </Button>
            <h2 className="ml-2 text-sm font-semibold tabular-nums">
              {view === "month" ? monthLabel(anchor) : weekRangeLabel(weekStart, weekEnd)}
            </h2>
          </div>
          <div className="flex items-center gap-3">
            <div className="hidden gap-3 text-xs text-muted-foreground sm:flex">
              <span>
                <b className="text-foreground">{upcomingCount}</b> {t("upcoming")}
              </span>
              <span>
                <b className="text-foreground">{pastCount}</b> {t("past")}
              </span>
            </div>
            <Tabs value={view} onValueChange={(v) => setView(v as "month" | "week")}>
              <TabsList className="h-8">
                <TabsTrigger value="month" className="text-xs">
                  {t("month")}
                </TabsTrigger>
                <TabsTrigger value="week" className="text-xs">
                  {t("week")}
                </TabsTrigger>
              </TabsList>
            </Tabs>
          </div>
        </div>

        <CardContent className="p-0">
          <div className="hidden md:block">
            {view === "month" ? (
              <MonthGrid anchor={anchor} events={events} onEventClick={handleEventClick} />
            ) : (
              <WeekGrid weekStart={weekStart} events={events} onEventClick={handleEventClick} />
            )}
          </div>
          <div className="block md:hidden">
            <AgendaView events={events} onEventClick={handleEventClick} />
          </div>
        </CardContent>
      </Card>

      <UpNextSection events={events} onEventClick={handleEventClick} profile={profile} />

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

// ─── legend ─────────────────────────────────────────────────────────────────

function Legend() {
  const { t } = useTranslation();
  return (
    <div className="flex items-center gap-3 text-xs text-muted-foreground">
      <span className="flex items-center gap-1.5">
        <span className="h-2 w-2 rounded-full bg-primary" /> {t("upcoming")}
      </span>
      <span className="flex items-center gap-1.5">
        <span className="h-2 w-2 rounded-full bg-emerald-500" /> {t("today")}
      </span>
      <span className="flex items-center gap-1.5">
        <span className="h-2 w-2 rounded-full bg-muted-foreground/40" /> {t("past")}
      </span>
    </div>
  );
}

// ─── month grid ─────────────────────────────────────────────────────────────

function MonthGrid({
  anchor,
  events,
  onEventClick,
}: {
  anchor: Date;
  events: ProCalEvent[];
  onEventClick: (e: ProCalEvent) => void;
}) {
  const year = anchor.getFullYear();
  const month = anchor.getMonth();
  const today = new Date();
  const firstOfMonth = new Date(year, month, 1);
  const startDow = firstOfMonth.getDay();
  const leading = startDow === 0 ? 6 : startDow - 1;
  const start = new Date(year, month, 1 - leading);
  const cells: Date[] = Array.from({ length: 42 }, (_, i) => {
    const d = new Date(start);
    d.setDate(start.getDate() + i);
    return d;
  });

  return (
    <div>
      <div className="grid grid-cols-7 border-b border-border/60 bg-muted/30 text-[11px] font-medium uppercase tracking-wider text-muted-foreground">
        {["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"].map((d) => (
          <div key={d} className="px-3 py-2 text-center">
            {d}
          </div>
        ))}
      </div>
      <div className="grid grid-cols-7 auto-rows-[minmax(100px,auto)]">
        {cells.map((d, i) => {
          const inMonth = d.getMonth() === month;
          const isToday = sameDay(d, today);
          const dayEvents = events.filter((e) => sameDay(e.start, d));
          return (
            <div
              key={i}
              className={`relative flex flex-col gap-1 border-b border-r border-border/50 px-2 py-2 ${
                inMonth ? "bg-card" : "bg-muted/20"
              }`}
            >
              <span
                className={`flex h-6 w-6 items-center justify-center rounded-full text-xs tabular-nums ${
                  isToday
                    ? "bg-primary font-semibold text-primary-foreground"
                    : inMonth
                      ? "text-foreground"
                      : "text-muted-foreground/50"
                }`}
              >
                {d.getDate()}
              </span>
              <div className="space-y-1">
                {dayEvents.slice(0, 3).map((e, j) => {
                  const s = statusStyles[e.status];
                  return (
                    <motion.div
                      key={e.id}
                      initial={{ opacity: 0, y: 2 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: j * 0.03 }}
                      className={`flex items-center gap-1 truncate rounded-md border px-1.5 py-1 text-[10.5px] font-medium leading-tight transition cursor-pointer ${s.chip}`}
                      onClick={() => onEventClick(e)}
                    >
                      <span className="tabular-nums opacity-80">{formatTime(e.start)}</span>
                      <span className="truncate">{e.title}</span>
                    </motion.div>
                  );
                })}
                {dayEvents.length > 3 && (
                  <div className="px-1.5 text-[10px] text-muted-foreground">
                    +{dayEvents.length - 3} more
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

// ─── week grid ──────────────────────────────────────────────────────────────

function WeekGrid({
  weekStart,
  events,
  onEventClick,
}: {
  weekStart: Date;
  events: ProCalEvent[];
  onEventClick: (e: ProCalEvent) => void;
}) {
  const today = new Date();
  const days = Array.from({ length: 7 }, (_, i) => {
    const d = new Date(weekStart);
    d.setDate(weekStart.getDate() + i);
    return d;
  });
  const HOURS = Array.from({ length: 12 }, (_, i) => 7 + i);
  const SLOT_HEIGHT = 44;

  return (
    <div className="overflow-x-auto">
      <div className="min-w-[820px]">
        <div className="grid grid-cols-[60px_repeat(7,1fr)] border-b border-border/60 bg-muted/30">
          <div />
          {days.map((d) => {
            const isToday = sameDay(d, today);
            return (
              <div key={d.toISOString()} className="px-2 py-2 text-center">
                <div className="text-[10px] uppercase tracking-wider text-muted-foreground">
                  {d.toLocaleDateString("en-US", { weekday: "short" })}
                </div>
                <div
                  className={`mx-auto mt-0.5 inline-flex h-7 min-w-7 items-center justify-center rounded-full px-2 text-sm tabular-nums ${
                    isToday ? "bg-primary font-semibold text-primary-foreground" : "text-foreground"
                  }`}
                >
                  {d.getDate()}
                </div>
              </div>
            );
          })}
        </div>
        <div
          className="relative grid grid-cols-[60px_repeat(7,1fr)]"
          style={{ height: `${HOURS.length * SLOT_HEIGHT}px` }}
        >
          <div className="relative">
            {HOURS.map((h, i) => (
              <div
                key={h}
                className="absolute right-2 -translate-y-1/2 text-[10px] text-muted-foreground"
                style={{ top: `${i * SLOT_HEIGHT}px` }}
              >
                {h <= 12 ? h : h - 12}
                {h < 12 ? "a" : "p"}
              </div>
            ))}
          </div>
          {days.map((d) => {
            const dayEvents = events.filter(
              (e) => sameDay(e.start, d) && e.start.getHours() >= HOURS[0],
            );
            return (
              <div key={d.toISOString()} className="relative border-l border-border/50">
                {HOURS.map((_, i) => (
                  <div
                    key={i}
                    className="absolute inset-x-0 border-t border-border/40"
                    style={{ top: `${i * SLOT_HEIGHT}px`, height: `${SLOT_HEIGHT}px` }}
                  />
                ))}
                {dayEvents.map((e, idx) => {
                  const startH = e.start.getHours() + e.start.getMinutes() / 60;
                  const endH = e.end.getHours() + e.end.getMinutes() / 60;
                  const top = (startH - HOURS[0]) * SLOT_HEIGHT;
                  const height = Math.max(28, (endH - startH) * SLOT_HEIGHT - 2);
                  const s = statusStyles[e.status];
                  return (
                    <motion.div
                      key={e.id}
                      initial={{ opacity: 0, scale: 0.97 }}
                      animate={{ opacity: 1, scale: 1 }}
                      transition={{ delay: idx * 0.04 }}
                      className={`absolute left-1 right-1 overflow-hidden rounded-md border px-1.5 py-1 text-[10.5px] leading-tight transition cursor-pointer ${s.chip}`}
                      style={{ top: `${top}px`, height: `${height}px` }}
                      onClick={() => onEventClick(e)}
                    >
                      <div className="truncate font-medium">{e.title}</div>
                      <div className="truncate opacity-80 tabular-nums">
                        {formatTime(e.start)} – {formatTime(e.end)}
                      </div>
                    </motion.div>
                  );
                })}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

// ─── agenda (mobile) ────────────────────────────────────────────────────────

function AgendaView({
  events,
  onEventClick,
}: {
  events: ProCalEvent[];
  onEventClick: (e: ProCalEvent) => void;
}) {
  const { t } = useTranslation();
  if (events.length === 0) {
    return (
      <div className="py-12 text-center text-sm text-muted-foreground">{t("no_shifts_period")}</div>
    );
  }
  return (
    <div className="divide-y divide-border/60">
      {events.map((e) => {
        const loc = mockLocations.find((l) => l.id === e.locationId);
        const s = statusStyles[e.status];
        return (
          <div
            key={e.id}
            className="flex items-center gap-3 px-4 py-3 cursor-pointer hover:bg-muted/40 transition"
            onClick={() => onEventClick(e)}
          >
            <div className={`h-10 w-1 shrink-0 rounded-full ${s.bar}`} />
            <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-muted/60">
              <Briefcase className="h-4 w-4 text-muted-foreground" />
            </div>
            <div className="min-w-0 flex-1">
              <div className="flex items-center justify-between gap-2">
                <p className="truncate text-sm font-medium">{e.title}</p>
                <Badge variant="outline" className={`shrink-0 h-5 px-1.5 text-[10px] ${s.chip}`}>
                  {e.status === "upcoming"
                    ? t("upcoming")
                    : e.status === "today"
                      ? t("today")
                      : t("completed")}
                </Badge>
              </div>
              <div className="mt-0.5 flex flex-col gap-0.5 text-[11px] text-muted-foreground">
                <span className="flex items-center gap-1">
                  <Clock className="h-3 w-3 shrink-0" />
                  {e.start.toLocaleDateString("en-US", {
                    weekday: "short",
                    month: "short",
                    day: "numeric",
                  })}
                  {" · "}
                  {formatTime(e.start)} – {formatTime(e.end)}
                </span>
                {loc && (
                  <span className="flex items-center gap-1">
                    <MapPin className="h-3 w-3 shrink-0" />
                    <span className="truncate">{loc.name}</span>
                  </span>
                )}
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
}

// ─── up next section ────────────────────────────────────────────────────────

function UpNextSection({
  events,
  onEventClick,
  profile,
}: {
  events: ProCalEvent[];
  onEventClick: (e: ProCalEvent) => void;
  profile: { firstName: string; lastName: string; avatarInitials: string };
}) {
  const now = new Date();
  const upcoming = events
    .filter((e) => e.start.getTime() >= now.getTime() - 1000 * 60 * 60 * 12)
    .slice(0, 6);

  const { t } = useTranslation();
  if (upcoming.length === 0) return null;

  return (
    <Card className="border-border/70 shadow-sm">
      <div className="flex items-center justify-between border-b border-border/60 px-5 py-3">
        <h3 className="flex items-center gap-2 text-sm font-semibold">
          <CalendarDays className="h-4 w-4 text-primary" /> {t("coming_up")}
        </h3>
        <span className="text-xs text-muted-foreground">
          {t("next_shifts", { count: upcoming.length })}
        </span>
      </div>
      <CardContent className="divide-y divide-border/60 p-0">
        {upcoming.map((e, i) => {
          const loc = mockLocations.find((l) => l.id === e.locationId);
          const practice = knownPractices.find((p) => p.id === e.practiceId);
          const s = statusStyles[e.status];
          const totalHours = (e.end.getTime() - e.start.getTime()) / (1000 * 60 * 60);
          const earnings = Math.round(e.hourlyRate * totalHours);
          return (
            <motion.div
              key={e.id}
              initial={{ opacity: 0, x: -4 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: i * 0.04 }}
              className="flex items-center gap-3 px-5 py-3 cursor-pointer hover:bg-muted/40 transition"
              onClick={() => onEventClick(e)}
            >
              <div className={`h-10 w-1 rounded-full ${s.bar}`} />
              <Avatar className="h-9 w-9">
                <AvatarFallback className="bg-gradient-brand text-xs text-primary-foreground">
                  {profile.avatarInitials}
                </AvatarFallback>
              </Avatar>
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-2">
                  <Badge variant="outline" className={`h-5 gap-1 px-1.5 text-[10px] ${s.chip}`}>
                    <Briefcase className="h-3 w-3" />
                    {e.status === "today"
                      ? t("today")
                      : e.status === "upcoming"
                        ? t("upcoming")
                        : t("completed")}
                  </Badge>
                  <p className="truncate text-sm font-medium">{e.title}</p>
                </div>
                <div className="mt-0.5 flex items-center gap-3 text-[11px] text-muted-foreground">
                  <span className="flex items-center gap-1">
                    <Clock className="h-3 w-3" />
                    {e.start.toLocaleDateString("en-US", {
                      weekday: "short",
                      month: "short",
                      day: "numeric",
                    })}
                    {" · "}
                    {formatTime(e.start)} – {formatTime(e.end)}
                  </span>
                  {loc && (
                    <span className="flex items-center gap-1">
                      <MapPin className="h-3 w-3" />
                      {loc.name}
                    </span>
                  )}
                </div>
              </div>
              <div className="text-right shrink-0">
                <div className="flex items-center gap-0.5 text-sm font-semibold text-emerald-600 dark:text-emerald-400">
                  <DollarSign className="h-3.5 w-3.5" />
                  {earnings}
                </div>
                <div className="text-[10px] text-muted-foreground">~{totalHours.toFixed(0)}h</div>
              </div>
            </motion.div>
          );
        })}
      </CardContent>
    </Card>
  );
}
