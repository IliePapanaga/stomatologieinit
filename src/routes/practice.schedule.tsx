import { useMemo, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { motion } from "motion/react";
import {
  ChevronLeft,
  ChevronRight,
  CalendarDays,
  Clock,
  UserCheck,
  Briefcase,
  MapPin,
} from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { mockProfessionals, mockPostings, mockLocations } from "@/lib/mock";

export const Route = createFileRoute("/practice/schedule")({
  component: SchedulePage,
});

type EventKind = "Shift" | "Interview";
interface CalEvent {
  id: string;
  kind: EventKind;
  title: string;
  professionalId: string;
  postingId: string;
  start: Date; // includes time
  end: Date;
  locationId: string;
}

const eventStyles: Record<EventKind, { dot: string; chip: string; bar: string; label: string }> = {
  Shift: {
    dot: "bg-primary",
    chip: "bg-primary/10 text-primary border-primary/30 hover:bg-primary/15",
    bar: "bg-primary",
    label: "Temporary shift",
  },
  Interview: {
    dot: "bg-amber-500",
    chip: "bg-amber-500/10 text-amber-700 dark:text-amber-400 border-amber-500/30 hover:bg-amber-500/15",
    bar: "bg-amber-500",
    label: "Interview",
  },
};

function buildEvents(monthAnchor: Date): CalEvent[] {
  // Deterministic spread across the month for a clean calendar
  const year = monthAnchor.getFullYear();
  const month = monthAnchor.getMonth();
  const seed = (n: number) => {
    const x = Math.sin((year + 1) * 13 + (month + 1) * 37 + n * 7) * 10000;
    return x - Math.floor(x);
  };
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const events: CalEvent[] = [];

  // 10 shifts
  for (let i = 0; i < 10; i++) {
    const day = 1 + Math.floor(seed(i) * (daysInMonth - 1));
    const hour = 7 + Math.floor(seed(i + 100) * 4); // 7–10am
    const duration = 6 + Math.floor(seed(i + 200) * 4); // 6–9h
    const pro = mockProfessionals[i % mockProfessionals.length];
    const posting = mockPostings[i % mockPostings.length];
    const start = new Date(year, month, day, hour, 0);
    const end = new Date(year, month, day, hour + duration, 0);
    events.push({
      id: `shift-${i}`,
      kind: "Shift",
      title: `${pro.specialty} · ${pro.firstName} ${pro.lastName[0]}.`,
      professionalId: pro.id,
      postingId: posting.id,
      start,
      end,
      locationId: posting.locationId,
    });
  }

  // 6 interviews
  for (let i = 0; i < 6; i++) {
    const day = 1 + Math.floor(seed(i + 50) * (daysInMonth - 1));
    const hour = 9 + Math.floor(seed(i + 300) * 7); // 9am–4pm
    const pro = mockProfessionals[(i * 4 + 1) % mockProfessionals.length];
    const posting = mockPostings[(i + 1) % mockPostings.length];
    const start = new Date(year, month, day, hour, 30);
    const end = new Date(year, month, day, hour + 1, 0);
    events.push({
      id: `intv-${i}`,
      kind: "Interview",
      title: `Interview · ${pro.firstName} ${pro.lastName[0]}.`,
      professionalId: pro.id,
      postingId: posting.id,
      start,
      end,
      locationId: posting.locationId,
    });
  }

  return events.sort((a, b) => a.start.getTime() - b.start.getTime());
}

const monthLabel = (d: Date) =>
  d.toLocaleDateString("en-US", { month: "long", year: "numeric" });
const weekRangeLabel = (start: Date, end: Date) => {
  const sameMonth = start.getMonth() === end.getMonth();
  const startTxt = start.toLocaleDateString("en-US", { month: "short", day: "numeric" });
  const endTxt = end.toLocaleDateString("en-US", {
    month: sameMonth ? undefined : "short",
    day: "numeric",
    year: "numeric",
  });
  return `${startTxt} – ${endTxt}`;
};
const sameDay = (a: Date, b: Date) =>
  a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate();

const formatTime = (d: Date) =>
  d.toLocaleTimeString("en-US", { hour: "numeric", minute: "2-digit" });

function SchedulePage() {
  const [view, setView] = useState<"month" | "week">("month");
  const [anchor, setAnchor] = useState(() => new Date());
  const events = useMemo(() => buildEvents(anchor), [anchor]);

  const navigate = (dir: -1 | 1) => {
    const next = new Date(anchor);
    if (view === "month") next.setMonth(next.getMonth() + dir);
    else next.setDate(next.getDate() + dir * 7);
    setAnchor(next);
  };

  const shiftsCount = events.filter((e) => e.kind === "Shift").length;
  const intvCount = events.filter((e) => e.kind === "Interview").length;

  // For week view
  const weekStart = useMemo(() => {
    const d = new Date(anchor);
    const dow = d.getDay();
    const diff = dow === 0 ? -6 : 1 - dow; // Monday start
    d.setDate(d.getDate() + diff);
    d.setHours(0, 0, 0, 0);
    return d;
  }, [anchor]);
  const weekEnd = useMemo(() => {
    const d = new Date(weekStart);
    d.setDate(d.getDate() + 6);
    return d;
  }, [weekStart]);

  return (
    <div className="space-y-5 p-6">
      <header className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">Schedule</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            Upcoming shifts and interviews across all locations.
          </p>
        </div>
        <div className="flex flex-wrap items-center gap-4">
          <Legend />
          <Button variant="outline" onClick={() => setAnchor(new Date())}>Today</Button>
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
              <span><b className="text-foreground">{shiftsCount}</b> shifts</span>
              <span><b className="text-foreground">{intvCount}</b> interviews</span>
            </div>
            <Tabs value={view} onValueChange={(v) => setView(v as "month" | "week")}>
              <TabsList className="h-8">
                <TabsTrigger value="month" className="text-xs">Month</TabsTrigger>
                <TabsTrigger value="week" className="text-xs">Week</TabsTrigger>
              </TabsList>
            </Tabs>
          </div>
        </div>

        <CardContent className="p-0">
          {view === "month" ? (
            <MonthGrid anchor={anchor} events={events} />
          ) : (
            <WeekGrid weekStart={weekStart} events={events} />
          )}
        </CardContent>
      </Card>

      <UpNext events={events} />
    </div>
  );
}

function Legend() {
  return (
    <div className="flex items-center gap-3 text-xs text-muted-foreground">
      <span className="flex items-center gap-1.5">
        <span className="h-2 w-2 rounded-full bg-primary" /> Shifts
      </span>
      <span className="flex items-center gap-1.5">
        <span className="h-2 w-2 rounded-full bg-amber-500" /> Interviews
      </span>
    </div>
  );
}

function MonthGrid({ anchor, events }: { anchor: Date; events: CalEvent[] }) {
  const year = anchor.getFullYear();
  const month = anchor.getMonth();
  const today = new Date();

  const firstOfMonth = new Date(year, month, 1);
  const startDow = firstOfMonth.getDay();
  const leading = startDow === 0 ? 6 : startDow - 1; // Monday start
  const start = new Date(year, month, 1 - leading);

  const cells: Date[] = [];
  for (let i = 0; i < 42; i++) {
    const d = new Date(start);
    d.setDate(start.getDate() + i);
    cells.push(d);
  }

  return (
    <div>
      <div className="grid grid-cols-7 border-b border-border/60 bg-muted/30 text-[11px] font-medium uppercase tracking-wider text-muted-foreground">
        {["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"].map((d) => (
          <div key={d} className="px-3 py-2 text-center">{d}</div>
        ))}
      </div>
      <div className="grid grid-cols-7 auto-rows-[minmax(110px,auto)]">
        {cells.map((d, i) => {
          const inMonth = d.getMonth() === month;
          const isToday = sameDay(d, today);
          const dayEvents = events.filter((e) => sameDay(e.start, d));
          return (
            <div
              key={i}
              className={`relative flex flex-col gap-1 border-b border-r border-border/50 px-2 py-2 last:border-r-0 ${
                inMonth ? "bg-card" : "bg-muted/20"
              }`}
            >
              <div className="flex items-center justify-between">
                <span
                  className={`flex h-6 w-6 items-center justify-center rounded-full text-xs tabular-nums ${
                    isToday
                      ? "bg-primary font-semibold text-primary-foreground"
                      : inMonth
                      ? "text-foreground"
                      : "text-muted-foreground/60"
                  }`}
                >
                  {d.getDate()}
                </span>
              </div>
              <div className="space-y-1">
                {dayEvents.slice(0, 3).map((e, j) => (
                  <motion.div
                    key={e.id}
                    initial={{ opacity: 0, y: 2 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: j * 0.03 }}
                    className={`flex items-center gap-1.5 truncate rounded-md border px-1.5 py-1 text-[10.5px] font-medium leading-tight transition ${eventStyles[e.kind].chip}`}
                  >
                    <span className="tabular-nums opacity-80">{formatTime(e.start)}</span>
                    <span className="truncate">{e.title}</span>
                  </motion.div>
                ))}
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

function WeekGrid({ weekStart, events }: { weekStart: Date; events: CalEvent[] }) {
  const today = new Date();
  const days: Date[] = Array.from({ length: 7 }, (_, i) => {
    const d = new Date(weekStart);
    d.setDate(weekStart.getDate() + i);
    return d;
  });
  const HOURS = Array.from({ length: 12 }, (_, i) => 7 + i); // 7am – 6pm
  const SLOT_HEIGHT = 44; // px per hour

  return (
    <div className="overflow-x-auto">
      <div className="min-w-[820px]">
        {/* Header row */}
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

        {/* Time grid */}
        <div
          className="relative grid grid-cols-[60px_repeat(7,1fr)]"
          style={{ height: `${HOURS.length * SLOT_HEIGHT}px` }}
        >
          {/* Hour labels + horizontal lines */}
          <div className="relative">
            {HOURS.map((h, i) => (
              <div
                key={h}
                className="absolute right-2 -translate-y-1/2 text-[10px] text-muted-foreground"
                style={{ top: `${i * SLOT_HEIGHT}px` }}
              >
                {h <= 12 ? h : h - 12}{h < 12 ? "a" : "p"}
              </div>
            ))}
          </div>

          {/* Day columns */}
          {days.map((d) => {
            const dayEvents = events.filter(
              (e) => sameDay(e.start, d) && e.start.getHours() >= HOURS[0]
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
                  return (
                    <motion.div
                      key={e.id}
                      initial={{ opacity: 0, scale: 0.97 }}
                      animate={{ opacity: 1, scale: 1 }}
                      transition={{ delay: idx * 0.04 }}
                      className={`absolute left-1 right-1 overflow-hidden rounded-md border px-1.5 py-1 text-[10.5px] leading-tight transition ${eventStyles[e.kind].chip}`}
                      style={{ top: `${top}px`, height: `${height}px` }}
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

function UpNext({ events }: { events: CalEvent[] }) {
  const now = new Date();
  const upcoming = events
    .filter((e) => e.start.getTime() >= now.getTime() - 1000 * 60 * 60 * 12)
    .slice(0, 6);

  if (upcoming.length === 0) return null;

  return (
    <Card className="border-border/70 shadow-sm">
      <div className="flex items-center justify-between border-b border-border/60 px-5 py-3">
        <h3 className="flex items-center gap-2 text-sm font-semibold">
          <CalendarDays className="h-4 w-4 text-primary" /> Up next
        </h3>
        <span className="text-xs text-muted-foreground">Next 6 events</span>
      </div>
      <CardContent className="divide-y divide-border/60 p-0">
        {upcoming.map((e, i) => {
          const pro = mockProfessionals.find((p) => p.id === e.professionalId);
          const loc = mockLocations.find((l) => l.id === e.locationId);
          const tone = eventStyles[e.kind];
          return (
            <motion.div
              key={e.id}
              initial={{ opacity: 0, x: -4 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: i * 0.04 }}
              className="flex items-center gap-3 px-5 py-3"
            >
              <div className={`h-10 w-1 rounded-full ${tone.bar}`} />
              <Avatar className="h-9 w-9">
                <AvatarFallback className="bg-gradient-brand text-xs text-primary-foreground">
                  {pro ? `${pro.firstName[0]}${pro.lastName[0]}` : "—"}
                </AvatarFallback>
              </Avatar>
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-2">
                  <Badge variant="outline" className={`h-5 gap-1 px-1.5 text-[10px] ${tone.chip}`}>
                    {e.kind === "Shift" ? <Briefcase className="h-3 w-3" /> : <UserCheck className="h-3 w-3" />}
                    {tone.label}
                  </Badge>
                  <p className="truncate text-sm font-medium">{e.title}</p>
                </div>
                <div className="mt-0.5 flex items-center gap-3 text-[11px] text-muted-foreground">
                  <span className="flex items-center gap-1">
                    <Clock className="h-3 w-3" />
                    {e.start.toLocaleDateString("en-US", { weekday: "short", month: "short", day: "numeric" })}
                    {" · "}
                    {formatTime(e.start)} – {formatTime(e.end)}
                  </span>
                  {loc && (
                    <span className="flex items-center gap-1">
                      <MapPin className="h-3 w-3" /> {loc.name}
                    </span>
                  )}
                </div>
              </div>
            </motion.div>
          );
        })}
      </CardContent>
    </Card>
  );
}
