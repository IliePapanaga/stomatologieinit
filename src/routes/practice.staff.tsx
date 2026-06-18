import { useMemo, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { motion } from "motion/react";
import {
  Search,
  Users,
  Star,
  Phone,
  Mail,
  ShieldCheck,
  ShieldAlert,
  Shield,
  MoreHorizontal,
} from "lucide-react";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { mockProfessionals, mockActivity } from "@/lib/mock";
import type {
  Professional,
  ProfessionalSpecialty,
  ProfessionalSubcategory,
} from "@/lib/types/mdd";

export const Route = createFileRoute("/practice/staff")({
  component: StaffPage,
});

const defaultSubBySpecialty: Record<ProfessionalSpecialty, ProfessionalSubcategory> = {
  Hygienist: "RDH",
  Dentist: "GeneralDentist",
  Assistant: "DentalAssistant",
  FrontOffice: "Receptionist",
  Orthodontist: "GeneralDentist",
};

const formatSub = (s: ProfessionalSubcategory) =>
  s.replace(/([A-Z])/g, " $1").trim();

interface StaffRow {
  pro: Professional;
  subcategory: ProfessionalSubcategory;
  shifts: number;
  checkIns: number;
  noShows: number;
  lateAlerts: number;
  reliability: number; // 0–100
  lastWorked: string;
  status: "Active" | "Bench" | "New";
}

function buildRows(): StaffRow[] {
  return mockProfessionals.map((pro, idx) => {
    // Derive check-ins / no-shows / late alerts from mock activity + deterministic noise
    const checkInsFromMock = mockActivity.filter(
      (a) => a.kind === "CheckIn" && a.professionalId === pro.id
    ).length;
    const noShowsFromMock = mockActivity.filter(
      (a) => a.kind === "NoShow" && a.professionalId === pro.id
    ).length;
    const lateFromMock = mockActivity.filter(
      (a) => a.kind === "AttendanceAlert" && a.professionalId === pro.id
    ).length;

    const base = (idx * 7) % 30;
    const checkIns = checkInsFromMock + 18 + base;
    const noShows = noShowsFromMock + (idx % 9 === 0 ? 2 : idx % 5 === 0 ? 1 : 0);
    const lateAlerts = lateFromMock + (idx % 6 === 0 ? 2 : idx % 3 === 0 ? 1 : 0);
    const shifts = checkIns + noShows;

    const successRate = checkIns / Math.max(1, shifts);
    const penalty = Math.min(0.2, lateAlerts * 0.015);
    const reliability = Math.round(Math.max(0, Math.min(1, successRate - penalty)) * 100);

    const lastWorked = new Date(Date.now() - (idx + 1) * 86400000 * 2)
      .toLocaleDateString("en-US", { month: "short", day: "numeric" });

    const status: StaffRow["status"] =
      idx < 14 ? "Active" : idx < 22 ? "Bench" : "New";

    return {
      pro,
      subcategory: defaultSubBySpecialty[pro.specialty],
      shifts,
      checkIns,
      noShows,
      lateAlerts,
      reliability,
      lastWorked,
      status,
    };
  });
}

function reliabilityTone(score: number) {
  if (score >= 92)
    return {
      label: "Excellent",
      icon: ShieldCheck,
      cls: "border-emerald-500/40 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
      bar: "bg-emerald-500",
    };
  if (score >= 78)
    return {
      label: "Trusted",
      icon: Shield,
      cls: "border-primary/40 bg-primary/10 text-primary",
      bar: "bg-primary",
    };
  if (score >= 60)
    return {
      label: "Watch",
      icon: Shield,
      cls: "border-amber-500/40 bg-amber-500/10 text-amber-600 dark:text-amber-400",
      bar: "bg-amber-500",
    };
  return {
    label: "At risk",
    icon: ShieldAlert,
    cls: "border-rose-500/40 bg-rose-500/10 text-rose-600 dark:text-rose-400",
    bar: "bg-rose-500",
  };
}

const statusStyles: Record<StaffRow["status"], string> = {
  Active: "border-emerald-500/40 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  Bench: "border-border bg-muted text-muted-foreground",
  New: "border-primary/40 bg-primary/10 text-primary",
};

function StaffPage() {
  const allRows = useMemo(buildRows, []);
  const [q, setQ] = useState("");
  const [specialty, setSpecialty] = useState<"All" | ProfessionalSpecialty>("All");
  const [status, setStatus] = useState<"All" | StaffRow["status"]>("All");

  const rows = useMemo(() => {
    const term = q.trim().toLowerCase();
    return allRows.filter((r) => {
      if (specialty !== "All" && r.pro.specialty !== specialty) return false;
      if (status !== "All" && r.status !== status) return false;
      if (!term) return true;
      return (
        `${r.pro.firstName} ${r.pro.lastName}`.toLowerCase().includes(term) ||
        r.pro.specialty.toLowerCase().includes(term) ||
        formatSub(r.subcategory).toLowerCase().includes(term)
      );
    });
  }, [allRows, q, specialty, status]);

  const summary = useMemo(() => {
    const total = allRows.length;
    const active = allRows.filter((r) => r.status === "Active").length;
    const avg = Math.round(allRows.reduce((s, r) => s + r.reliability, 0) / Math.max(1, total));
    const top = allRows.filter((r) => r.reliability >= 92).length;
    return { total, active, avg, top };
  }, [allRows]);

  return (
    <div className="space-y-6 p-6">
      <header className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">Staff</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            Professionals matched to your practice. Reliability is scored from check-ins, no-shows, and late alerts.
          </p>
        </div>
        <Button variant="outline" className="gap-2">
          <Users className="h-4 w-4" /> Invite professional
        </Button>
      </header>

      {/* Summary strip */}
      <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
        <SummaryCard label="Total staff" value={summary.total} />
        <SummaryCard label="Currently active" value={summary.active} tone="emerald" />
        <SummaryCard label="Avg reliability" value={`${summary.avg}%`} tone="primary" />
        <SummaryCard label="Top tier (≥ 92)" value={summary.top} tone="primary" />
      </div>

      {/* Filters */}
      <div className="flex flex-wrap items-center gap-3">
        <div className="relative w-full max-w-xs">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="Search name, specialty…"
            className="pl-9"
          />
        </div>
        <Select value={specialty} onValueChange={(v) => setSpecialty(v as typeof specialty)}>
          <SelectTrigger className="w-[170px]"><SelectValue /></SelectTrigger>
          <SelectContent>
            <SelectItem value="All">All specialties</SelectItem>
            {(["Hygienist", "Dentist", "Assistant", "FrontOffice", "Orthodontist"] as ProfessionalSpecialty[]).map((s) => (
              <SelectItem key={s} value={s}>{s}</SelectItem>
            ))}
          </SelectContent>
        </Select>
        <Select value={status} onValueChange={(v) => setStatus(v as typeof status)}>
          <SelectTrigger className="w-[140px]"><SelectValue /></SelectTrigger>
          <SelectContent>
            <SelectItem value="All">All status</SelectItem>
            <SelectItem value="Active">Active</SelectItem>
            <SelectItem value="Bench">Bench</SelectItem>
            <SelectItem value="New">New</SelectItem>
          </SelectContent>
        </Select>
        <span className="ml-auto text-xs text-muted-foreground">
          {rows.length} of {allRows.length} professionals
        </span>
      </div>

      <Card className="overflow-hidden border-border/70 shadow-sm">
        <Table>
          <TableHeader>
            <TableRow className="bg-muted/40 hover:bg-muted/40">
              <TableHead className="w-[34%]">Professional</TableHead>
              <TableHead>Subcategory</TableHead>
              <TableHead>Reliability</TableHead>
              <TableHead className="text-right">Shifts</TableHead>
              <TableHead>Last worked</TableHead>
              <TableHead className="w-[1%]"></TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {rows.map((r, i) => (
              <StaffRowItem key={r.pro.id} row={r} index={i} />
            ))}
            {rows.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} className="py-12 text-center text-sm text-muted-foreground">
                  No professionals match your filters.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </Card>
    </div>
  );
}

function SummaryCard({
  label,
  value,
  tone,
}: {
  label: string;
  value: string | number;
  tone?: "emerald" | "primary";
}) {
  const accent =
    tone === "emerald" ? "text-emerald-600 dark:text-emerald-400" :
    tone === "primary" ? "text-primary" : "text-foreground";
  return (
    <Card className="border-border/60 p-4 shadow-sm">
      <p className="text-[11px] font-medium uppercase tracking-wider text-muted-foreground">{label}</p>
      <p className={`mt-1.5 text-2xl font-semibold tabular-nums ${accent}`}>{value}</p>
    </Card>
  );
}

function StaffRowItem({ row, index }: { row: StaffRow; index: number }) {
  const tone = reliabilityTone(row.reliability);
  const Icon = tone.icon;
  const initials = `${row.pro.firstName[0]}${row.pro.lastName[0]}`;

  return (
    <motion.tr
      initial={{ opacity: 0, y: 4 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: Math.min(index * 0.015, 0.3), duration: 0.25 }}
      className="border-b border-border/60 transition hover:bg-muted/40"
    >
      <TableCell className="py-3">
        <div className="flex items-center gap-3">
          <div className="relative">
            <Avatar className="h-9 w-9">
              <AvatarFallback className="bg-gradient-brand text-xs font-semibold text-primary-foreground">
                {initials}
              </AvatarFallback>
            </Avatar>
            {row.pro.online && (
              <span className="absolute -right-0.5 -bottom-0.5 h-2.5 w-2.5 rounded-full border-2 border-background bg-emerald-500" />
            )}
          </div>
          <div className="min-w-0">
            <p className="truncate text-sm font-medium">
              {row.pro.firstName} {row.pro.lastName}
            </p>
            <div className="flex items-center gap-2 text-[11px] text-muted-foreground">
              <span className="flex items-center gap-0.5">
                <Star className="h-3 w-3 fill-amber-400 text-amber-400" />
                {row.pro.rating.toFixed(1)}
              </span>
              <span>·</span>
              <Badge
                variant="outline"
                className={`h-4 px-1.5 text-[9px] font-medium ${statusStyles[row.status]}`}
              >
                {row.status}
              </Badge>
            </div>
          </div>
        </div>
      </TableCell>

      <TableCell>
        <div>
          <p className="text-sm font-medium">{formatSub(row.subcategory)}</p>
          <p className="text-[11px] text-muted-foreground">{row.pro.specialty}</p>
        </div>
      </TableCell>

      <TableCell>
        <TooltipProvider delayDuration={150}>
          <Tooltip>
            <TooltipTrigger asChild>
              <div className="inline-flex items-center gap-2.5">
                <Badge variant="outline" className={`h-6 gap-1 px-2 text-[11px] ${tone.cls}`}>
                  <Icon className="h-3 w-3" />
                  {row.reliability}
                </Badge>
                <div className="h-1.5 w-24 overflow-hidden rounded-full bg-muted">
                  <motion.div
                    initial={{ width: 0 }}
                    animate={{ width: `${row.reliability}%` }}
                    transition={{ duration: 0.5, delay: 0.1 + Math.min(index * 0.01, 0.2) }}
                    className={`h-full rounded-full ${tone.bar}`}
                  />
                </div>
                <span className="text-[11px] text-muted-foreground">{tone.label}</span>
              </div>
            </TooltipTrigger>
            <TooltipContent side="top" className="text-xs">
              <div className="space-y-0.5">
                <p>{row.checkIns} check-ins · {row.noShows} no-shows · {row.lateAlerts} late</p>
              </div>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      </TableCell>

      <TableCell className="text-right tabular-nums text-sm">{row.shifts}</TableCell>
      <TableCell className="text-sm text-muted-foreground">{row.lastWorked}</TableCell>

      <TableCell className="text-right">
        <div className="flex items-center justify-end gap-1">
          <Button variant="ghost" size="icon" className="h-7 w-7 text-muted-foreground">
            <Mail className="h-3.5 w-3.5" />
          </Button>
          <Button variant="ghost" size="icon" className="h-7 w-7 text-muted-foreground">
            <Phone className="h-3.5 w-3.5" />
          </Button>
          <Button variant="ghost" size="icon" className="h-7 w-7 text-muted-foreground">
            <MoreHorizontal className="h-3.5 w-3.5" />
          </Button>
        </div>
      </TableCell>
    </motion.tr>
  );
}
