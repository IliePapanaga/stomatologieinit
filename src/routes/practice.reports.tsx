import { useState, useMemo, useRef, useEffect } from "react";
import { createFileRoute, useSearch } from "@tanstack/react-router";
import {
  Users,
  UserCheck,
  CreditCard,
  Briefcase,
  XCircle,
  AlertCircle,
  ChevronUp,
  ChevronDown,
  ChevronsUpDown,
  Printer,
  FileText,
  RotateCcw,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useAppStore } from "@/lib/store/app-store";
import { mockPayments, mockLocations } from "@/lib/mock";
import type { JobPosting } from "@/lib/types/mdd";
import { useTranslation } from "react-i18next";

export const Route = createFileRoute("/practice/reports")({
  validateSearch: (search: Record<string, unknown>) => ({
    tab: (search.tab as string) ?? "clients",
  }),
  component: ReportsPage,
});

// ─── Types ────────────────────────────────────────────────────────────────────

type ReportCategory =
  | "clients"
  | "professionals"
  | "payments"
  | "positions"
  | "canceled"
  | "notfilled";

const CATEGORIES: { id: ReportCategory; label: string; icon: typeof Users }[] = [
  { id: "clients", label: "Clients", icon: Users },
  { id: "professionals", label: "Professionals", icon: UserCheck },
  { id: "payments", label: "Payments", icon: CreditCard },
  { id: "positions", label: "Positions", icon: Briefcase },
  { id: "canceled", label: "Canceled Postings", icon: XCircle },
  { id: "notfilled", label: "Not Filled Positions", icon: AlertCircle },
];

type SortDir = "asc" | "desc" | null;

// ─── Sort helpers ─────────────────────────────────────────────────────────────

function useSortState(defaultKey: string) {
  const [sortKey, setSortKey] = useState<string>(defaultKey);
  const [sortDir, setSortDir] = useState<SortDir>("asc");

  const toggle = (key: string) => {
    if (sortKey === key) {
      setSortDir((d) => (d === "asc" ? "desc" : "asc"));
    } else {
      setSortKey(key);
      setSortDir("asc");
    }
  };

  return { sortKey, sortDir, toggle };
}

function SortIcon({ col, sortKey, sortDir }: { col: string; sortKey: string; sortDir: SortDir }) {
  if (col !== sortKey) return <ChevronsUpDown className="h-3 w-3 ml-1 opacity-30 shrink-0" />;
  return sortDir === "asc" ? (
    <ChevronUp className="h-3 w-3 ml-1 shrink-0" />
  ) : (
    <ChevronDown className="h-3 w-3 ml-1 shrink-0" />
  );
}

function Th({
  label,
  col,
  sortKey,
  sortDir,
  onSort,
  width,
}: {
  label: string;
  col: string;
  sortKey: string;
  sortDir: SortDir;
  onSort: (k: string) => void;
  width?: string;
}) {
  return (
    <th
      style={width ? { width } : undefined}
      className="px-1.5 py-2 text-left text-[9px] font-bold uppercase tracking-wide cursor-pointer select-none border-b-2 border-gray-200 hover:bg-gray-50 align-bottom"
      onClick={() => onSort(col)}
    >
      <div className="flex items-start justify-between gap-0.5 min-w-0 w-full">
        <span className="break-words leading-tight uppercase">{label}</span>
        <SortIcon col={col} sortKey={sortKey} sortDir={sortDir} />
      </div>
    </th>
  );
}

function sortRows<T extends object>(rows: T[], key: string, dir: SortDir): T[] {
  if (!dir) return rows;
  return [...rows].sort((a, b) => {
    const av = (a as Record<string, unknown>)[key] ?? "";
    const bv = (b as Record<string, unknown>)[key] ?? "";
    const cmp = String(av).localeCompare(String(bv), undefined, { numeric: true });
    return dir === "asc" ? cmp : -cmp;
  });
}

// ─── Date filter helpers ─────────────────────────────────────────────────────

type Period =
  | "all"
  | "today"
  | "this_week"
  | "last_week"
  | "this_month"
  | "this_quarter"
  | "this_year";

function inPeriod(
  dateStr: string | undefined,
  _period: Period,
  from?: string,
  to?: string,
): boolean {
  if (!dateStr) return true;
  const d = new Date(dateStr);

  if (from || to) {
    if (from && d < new Date(from)) return false;
    if (to && d > new Date(to + "T23:59:59")) return false;
    return true;
  }
  return true;
}

// ─── PDF Print (opens clean print window) ────────────────────────────────────

function printReport(ref: React.RefObject<HTMLDivElement | null>, title: string) {
  if (!ref.current) return;
  const html = ref.current.innerHTML;
  const win = window.open("", "_blank", "width=900,height=700");
  if (!win) return;
  win.document.write(`<!DOCTYPE html><html><head>
    <title>${title} – MDD Report</title>
    <style>
      @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap');
      *{box-sizing:border-box;margin:0;padding:0}
      body{font-family:'Plus Jakarta Sans',sans-serif;font-size:10px;color:#1a1a1a;background:#fff;padding:24px 28px}
      h1{font-size:16px;font-weight:700;margin-bottom:2px}
      .subtitle{font-size:9px;color:#888;margin-bottom:18px}
      table{width:100%;border-collapse:collapse;font-size:9px}
      thead th{background:#f5f5f5;font-weight:700;font-size:8px;text-transform:uppercase;letter-spacing:.05em;padding:6px 8px;border-bottom:2px solid #ddd;text-align:left;word-break:break-word}
      tbody tr{border-bottom:1px solid #eee}
      tbody tr:nth-child(even){background:#fafafa}
      tbody td{padding:5px 8px;vertical-align:top;word-break:break-word}
      .badge{display:inline-block;border-radius:9999px;padding:1px 6px;font-size:8px;font-weight:600}
      @media print{
        @page { size: letter; margin: 0.5in; }
        body{padding:12px 16px}
        thead{display:table-header-group}
      }
    </style>
  </head><body>
    <h1>${title}</h1>
    <p class="subtitle">Generated by MDD · ${new Date().toLocaleString()}</p>
    ${html}
  </body></html>`);
  win.document.close();
  win.focus();
  setTimeout(() => { win.print(); }, 400);
}

// ─── Main Page ────────────────────────────────────────────────────────────────

function ReportsPage() {
  const { tab: tabParam } = useSearch({ from: "/practice/reports" });
  const { t } = useTranslation();
  const [active, setActive] = useState<ReportCategory>(
    (tabParam as ReportCategory) ?? "clients",
  );

  useEffect(() => {
    if (tabParam && tabParam !== active) {
      setActive(tabParam as ReportCategory);
    }
  }, [tabParam]);

  const cat = CATEGORIES.find((c) => c.id === active)!;

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-background p-3 sm:p-4 md:p-6">
      {/* Page header */}
      <div className="flex flex-wrap items-start justify-between gap-2 mb-4">
        <div>
          <div className="flex items-center gap-2">
            <cat.icon className="h-5 w-5 text-primary shrink-0" />
            <h1 className="text-lg md:text-xl font-semibold tracking-tight">{t("report_" + cat.id)}</h1>
          </div>
          <p className="mt-0.5 text-xs text-muted-foreground">
            {t("live_report_desc", { defaultValue: "Live report — sourced directly from app data" })}
          </p>
        </div>
      </div>

      {active === "clients"       && <ClientsReport />}
      {active === "professionals" && <ProfessionalsReport />}
      {active === "payments"      && <PaymentsReport />}
      {active === "positions"     && <PositionsReport />}
      {active === "canceled"      && <CanceledPostingsReport />}
      {active === "notfilled"     && <NotFilledReport />}
    </div>
  );
}

// ─── CLIENTS ─────────────────────────────────────────────────────────────────

function ClientsReport() {
  const users = useAppStore((s) => s.users);
  const clients = users.filter((u) => u.role === "PracticeOwner");
  const [period, setPeriod] = useState<Period>("all");
  const { sortKey, sortDir, toggle } = useSortState("lastName");
  const printRef = useRef<HTMLDivElement>(null);

  const rows = useMemo(() =>
    sortRows(
      clients.map((u) => ({
        lastName: u.lastName,
        firstName: u.firstName,
        email: u.email,
        phone: "—",
        address: u.practiceAddress ?? "—",
        tenant: u.tenant,
        lastActivity: "—",
      })),
      sortKey,
      sortDir,
    ),
    [clients, sortKey, sortDir],
  );

  const cols = [
    { key: "lastName",     label: "Last Name",      width: "13%" },
    { key: "firstName",    label: "First Name",     width: "13%" },
    { key: "email",        label: "Email",          width: "22%" },
    { key: "phone",        label: "Phone",          width: "12%" },
    { key: "address",      label: "Address",        width: "25%" },
    { key: "tenant",       label: "Practice",       width: "15%" },
  ];

  return (
    <ReportShell
      title="Clients"
      printRef={printRef}
      topBar={
        <PeriodSelect value={period} onChange={setPeriod} />
      }
    >
      <div ref={printRef}>
        <ReportTable cols={cols} rows={rows} sortKey={sortKey} sortDir={sortDir} onSort={toggle} />
      </div>
    </ReportShell>
  );
}

// ─── PROFESSIONALS ────────────────────────────────────────────────────────────

function ProfessionalsReport() {
  const professionals = useAppStore((s) => s.professionals);
  const [period, setPeriod] = useState<Period>("all");
  const [specialty, setSpecialty] = useState("all");
  const { sortKey, sortDir, toggle } = useSortState("lastName");
  const printRef = useRef<HTMLDivElement>(null);
  const { t } = useTranslation();

  const specialties = useMemo(() => {
    const s = new Set(professionals.map((p) => p.specialty));
    return ["all", ...Array.from(s)];
  }, [professionals]);

  const rows = useMemo(() => {
    const filtered = professionals.filter(
      (p) => specialty === "all" || p.specialty === specialty,
    );
    return sortRows(
      filtered.map((p) => ({
        lastName: p.lastName,
        firstName: p.firstName,
        email: p.email,
        phone: p.phone,
        specialty: p.specialty,
        status: p.status,
        docs: p.documentStatus,
        rating: p.rating.toFixed(1),
      })),
      sortKey,
      sortDir,
    );
  }, [professionals, specialty, sortKey, sortDir]);

  const cols = [
    { key: "lastName",  label: "Last Name",  width: "12%" },
    { key: "firstName", label: "First Name", width: "12%" },
    { key: "email",     label: "Email",      width: "22%" },
    { key: "phone",     label: "Phone",      width: "14%" },
    { key: "specialty", label: "Specialty",  width: "12%" },
    { key: "status",    label: "Status",     width: "9%"  },
    { key: "docs",      label: "Docs",       width: "10%" },
    { key: "rating",    label: "Rating",     width: "9%"  },
  ];

  return (
    <ReportShell
      title="Professionals"
      printRef={printRef}
      topBar={
        <>
          <PeriodSelect value={period} onChange={setPeriod} />
          <Select value={specialty} onValueChange={setSpecialty}>
            <SelectTrigger className="h-8 w-32 text-xs">
              <SelectValue placeholder={t("specialty")} />
            </SelectTrigger>
            <SelectContent>
              {specialties.map((s) => (
                <SelectItem key={s} value={s} className="text-xs capitalize">
                  {s === "all" ? t("all_specialties") : t(s.toLowerCase() + "_label", { defaultValue: s })}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </>
      }
    >
      <div ref={printRef}>
        <ReportTable cols={cols} rows={rows} sortKey={sortKey} sortDir={sortDir} onSort={toggle} />
      </div>
    </ReportShell>
  );
}

// ─── PAYMENTS ────────────────────────────────────────────────────────────────

function PaymentsReport() {
  const [from, setFrom] = useState("");
  const [to, setTo]     = useState("");
  const { sortKey, sortDir, toggle } = useSortState("period");
  const printRef = useRef<HTMLDivElement>(null);
  const { t } = useTranslation();

  const data = useMemo(() => {
    const filtered = mockPayments.filter((p) =>
      inPeriod(p.date, "all", from || undefined, to || undefined),
    );
    const rows = sortRows(
      filtered.map((p) => ({
        period:      p.date.slice(0, 10),
        description: p.description,
        paid:        p.status === "Succeeded" ? `$${p.amount.toLocaleString()}` : "$0",
        delinquent:  p.status === "Failed"    ? `$${p.amount.toLocaleString()}` : "$0",
        method:      p.method,
        status:      p.status,
      })),
      sortKey,
      sortDir,
    );
    const totalPaid       = filtered.filter((p) => p.status === "Succeeded").reduce((s, p) => s + p.amount, 0);
    const totalDelinquent = filtered.filter((p) => p.status === "Failed").reduce((s, p) => s + p.amount, 0);
    return { rows, totalPaid, totalDelinquent };
  }, [from, to, sortKey, sortDir]);

  const cols = [
    { key: "period",      label: "Period",      width: "16%" },
    { key: "description", label: "Description", width: "34%" },
    { key: "paid",        label: "Paid",        width: "13%" },
    { key: "delinquent",  label: "Delinquent",  width: "13%" },
    { key: "method",      label: "Method",      width: "12%" },
    { key: "status",      label: "Status",      width: "12%" },
  ];

  return (
    <ReportShell
      title="Payments"
      printRef={printRef}
      topBar={
        <>
          <DateInput label={t("filter_from")} value={from} onChange={setFrom} />
          <DateInput label={t("filter_to")}   value={to}   onChange={setTo} />
          <ResetBtn onClick={() => { setFrom(""); setTo(""); }} />
        </>
      }
    >
      <div ref={printRef}>
        {/* Summary row */}
        <div className="flex flex-wrap gap-2 mb-4">
          <SummaryBadge label="Total Paid"  value={`$${data.totalPaid.toLocaleString()}`}  color="emerald" />
          <SummaryBadge label="Delinquent"  value={`$${data.totalDelinquent.toLocaleString()}`} color="rose" />
        </div>
        <ReportTable cols={cols} rows={data.rows} sortKey={sortKey} sortDir={sortDir} onSort={toggle} />
      </div>
    </ReportShell>
  );
}

// ─── POSITIONS ────────────────────────────────────────────────────────────────

function PositionsReport() {
  const jobPostings = useAppStore((s) => s.jobPostings);
  const [from, setFrom] = useState("");
  const [to, setTo]     = useState("");
  const [statusFilter,   setStatusFilter]   = useState("all");
  const [positionFilter, setPositionFilter] = useState("all");
  const { sortKey, sortDir, toggle } = useSortState("startDate");
  const printRef = useRef<HTMLDivElement>(null);
  const { t } = useTranslation();

  const locationMap = useMemo(() => buildLocationMap(), []);
  const specialties  = useMemo(() => {
    const s = new Set(jobPostings.map((p) => p.specialty));
    return ["all", ...Array.from(s)];
  }, [jobPostings]);

  const rows = useMemo(() => {
    const filtered = jobPostings.filter((p) => {
      if (statusFilter   !== "all" && p.status    !== statusFilter)   return false;
      if (positionFilter !== "all" && p.specialty !== positionFilter) return false;
      if (!inPeriod(p.startDate, "all", from || undefined, to || undefined)) return false;
      return true;
    });
    return sortRows(
      filtered.map((p) => {
        const loc = locationMap[p.locationId];
        return {
          position:  p.title ?? p.specialty,
          type:      p.kind,
          office:    loc?.name ?? "—",
          practice:  "Brightside Dental",
          city:      loc?.address.city  ?? "—",
          state:     loc?.address.state ?? "—",
          status:    p.status,
          startDate: p.startDate.slice(0, 10),
        };
      }),
      sortKey,
      sortDir,
    );
  }, [jobPostings, from, to, statusFilter, positionFilter, sortKey, sortDir, locationMap]);

  const cols = [
    { key: "position",  label: "Position",  width: "24%" },
    { key: "type",      label: "Type",      width: "11%" },
    { key: "office",    label: "Office",    width: "17%" },
    { key: "practice",  label: "Practice",  width: "17%" },
    { key: "city",      label: "City",      width: "13%" },
    { key: "state",     label: "State",     width: "7%"  },
    { key: "status",    label: "Status",    width: "11%" },
  ];

  return (
    <ReportShell
      title="Positions"
      printRef={printRef}
      topBar={
        <>
          <DateInput label={t("filter_from")} value={from} onChange={setFrom} />
          <DateInput label={t("filter_to")}   value={to}   onChange={setTo} />
          <Select value={positionFilter} onValueChange={setPositionFilter}>
            <SelectTrigger className="h-8 w-32 text-xs">
              <SelectValue placeholder={t("position")} />
            </SelectTrigger>
            <SelectContent>
              {specialties.map((s) => (
                <SelectItem key={s} value={s} className="text-xs">
                  {s === "all" ? t("all_positions") : t(s.toLowerCase() + "_label", { defaultValue: s })}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <Select value={statusFilter} onValueChange={setStatusFilter}>
            <SelectTrigger className="h-8 w-32 text-xs">
              <SelectValue placeholder={t("status")} />
            </SelectTrigger>
            <SelectContent>
              {["all","Open","Filled","Cancelled","Expired","Draft"].map((s) => (
                <SelectItem key={s} value={s} className="text-xs">
                  {s === "all" ? t("all_statuses") : t("status_" + s.toLowerCase(), { defaultValue: s })}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <ResetBtn onClick={() => { setFrom(""); setTo(""); setStatusFilter("all"); setPositionFilter("all"); }} />
        </>
      }
    >
      <div ref={printRef}>
        <ReportTable cols={cols} rows={rows} sortKey={sortKey} sortDir={sortDir} onSort={toggle} />
      </div>
    </ReportShell>
  );
}

// ─── CANCELED POSTINGS ────────────────────────────────────────────────────────

function CanceledPostingsReport() {
  const jobPostings   = useAppStore((s) => s.jobPostings);
  const professionals = useAppStore((s) => s.professionals);
  const [from, setFrom] = useState("");
  const [to, setTo]     = useState("");
  const { sortKey, sortDir, toggle } = useSortState("jobStartDate");
  const printRef = useRef<HTMLDivElement>(null);
  const { t } = useTranslation();

  const locationMap = useMemo(() => buildLocationMap(), []);
  const proMap = useMemo(() => {
    const m: Record<string, (typeof professionals)[0]> = {};
    professionals.forEach((p) => { m[p.id] = p; });
    return m;
  }, [professionals]);

  const rows = useMemo(() => {
    const cancelled = jobPostings.filter(
      (p): p is JobPosting & { status: "Cancelled" } => p.status === "Cancelled",
    );
    const filtered = cancelled.filter((p) =>
      inPeriod(p.startDate, "all", from || undefined, to || undefined),
    );
    return sortRows(
      filtered.map((p) => {
        const loc    = locationMap[p.locationId];
        const hiredId = p.hiredCandidateIds?.[0];
        const pro    = hiredId ? proMap[hiredId] : undefined;
        return {
          clientLast:   "Chen",
          clientFirst:  "Maya",
          office:       loc?.name ?? "—",
          location:     loc ? `${loc.address.city}, ${loc.address.state}` : "—",
          dateCancelled:p.endDate?.slice(0, 10) ?? p.startDate.slice(0, 10),
          proLast:      pro?.lastName  ?? "—",
          proFirst:     pro?.firstName ?? "—",
          position:     p.title ?? p.specialty,
          jobStartDate: p.startDate.slice(0, 10),
          cancelSub:    p.endDate?.slice(0, 10) ?? "—",
        };
      }),
      sortKey,
      sortDir,
    );
  }, [jobPostings, from, to, sortKey, sortDir, locationMap, proMap]);

  const cols = [
    { key: "clientLast",    label: "Client Last",   width: "10%" },
    { key: "clientFirst",   label: "Client First",  width: "10%" },
    { key: "office",        label: "Office",        width: "14%" },
    { key: "location",      label: "Location",      width: "13%" },
    { key: "dateCancelled", label: "Cancelled",     width: "10%" },
    { key: "proLast",       label: "Pro Last",      width: "9%"  },
    { key: "proFirst",      label: "Pro First",     width: "9%"  },
    { key: "position",      label: "Position",      width: "14%" },
    { key: "jobStartDate",  label: "Job Start",     width: "11%" },
  ];

  return (
    <ReportShell
      title="Canceled Postings"
      printRef={printRef}
      topBar={
        <>
          <DateInput label={t("filter_from")} value={from} onChange={setFrom} />
          <DateInput label={t("filter_to")}   value={to}   onChange={setTo} />
          <ResetBtn onClick={() => { setFrom(""); setTo(""); }} />
        </>
      }
    >
      <div ref={printRef}>
        {rows.length === 0 ? (
          <EmptyState icon={XCircle} message="No cancelled postings found" sub="Cancelled postings will appear here." />
        ) : (
          <ReportTable cols={cols} rows={rows} sortKey={sortKey} sortDir={sortDir} onSort={toggle} />
        )}
      </div>
    </ReportShell>
  );
}

// ─── NOT FILLED POSITIONS ─────────────────────────────────────────────────────

function NotFilledReport() {
  const jobPostings = useAppStore((s) => s.jobPostings);
  const [from, setFrom] = useState("");
  const [to, setTo]     = useState("");
  const [positionFilter, setPositionFilter] = useState("all");
  const { sortKey, sortDir, toggle } = useSortState("startDate");
  const printRef = useRef<HTMLDivElement>(null);
  const { t } = useTranslation();

  const locationMap = useMemo(() => buildLocationMap(), []);
  const specialties  = useMemo(() => {
    const s = new Set(jobPostings.map((p) => p.specialty));
    return ["all", ...Array.from(s)];
  }, [jobPostings]);

  const notFilled = useMemo(
    () => jobPostings.filter(
      (p) => p.status === "Open" && (!p.hiredCandidateIds || p.hiredCandidateIds.length === 0),
    ),
    [jobPostings],
  );

  const rows = useMemo(() => {
    const filtered = notFilled.filter((p) => {
      if (positionFilter !== "all" && p.specialty !== positionFilter) return false;
      if (!inPeriod(p.startDate, "all", from || undefined, to || undefined)) return false;
      return true;
    });
    return sortRows(
      filtered.map((p) => {
        const loc         = locationMap[p.locationId];
        const daysUnfilled = Math.max(0, Math.floor((Date.now() - new Date(p.startDate).getTime()) / 86400000));
        return {
          position:     p.title ?? p.specialty,
          type:         p.kind,
          office:       loc?.name ?? "—",
          practice:     "Brightside Dental",
          city:         loc?.address.city  ?? "—",
          state:        loc?.address.state ?? "—",
          startDate:    p.startDate.slice(0, 10),
          daysUnfilled: String(daysUnfilled),
          applicants:   String(p.applicantsCount),
        };
      }),
      sortKey,
      sortDir,
    );
  }, [notFilled, from, to, positionFilter, sortKey, sortDir, locationMap]);

  const cols = [
    { key: "position",     label: "Position",     width: "20%" },
    { key: "type",         label: "Type",         width: "10%" },
    { key: "office",       label: "Office",       width: "15%" },
    { key: "practice",     label: "Practice",     width: "15%" },
    { key: "city",         label: "City",         width: "12%" },
    { key: "state",        label: "State",        width: "6%"  },
    { key: "startDate",    label: "Start Date",   width: "11%" },
    { key: "daysUnfilled", label: "Days Unfilled",width: "11%" },
  ];

  return (
    <ReportShell
      title="Not Filled Positions"
      printRef={printRef}
      topBar={
        <>
          <DateInput label={t("filter_from")} value={from} onChange={setFrom} />
          <DateInput label={t("filter_to")}   value={to}   onChange={setTo} />
          <Select value={positionFilter} onValueChange={setPositionFilter}>
            <SelectTrigger className="h-8 w-32 text-xs">
              <SelectValue placeholder={t("position")} />
            </SelectTrigger>
            <SelectContent>
              {specialties.map((s) => (
                <SelectItem key={s} value={s} className="text-xs">
                  {s === "all" ? t("all_positions") : t(s.toLowerCase() + "_label", { defaultValue: s })}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <ResetBtn onClick={() => { setFrom(""); setTo(""); setPositionFilter("all"); }} />
        </>
      }
    >
      <div ref={printRef}>
        <ReportTable cols={cols} rows={rows} sortKey={sortKey} sortDir={sortDir} onSort={toggle} />
      </div>
    </ReportShell>
  );
}

// ─── Shared UI Components ─────────────────────────────────────────────────────

/** US Letter = 816px at 96dpi. We use 800px max so it fits with a little breathing room. */
function ReportShell({
  title,
  topBar,
  children,
  printRef,
}: {
  title: string;
  topBar: React.ReactNode;
  children: React.ReactNode;
  printRef: React.RefObject<HTMLDivElement | null>;
}) {
  const { t } = useTranslation();
  const [scale, setScale] = useState(1);
  const [docHeight, setDocHeight] = useState<number | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const docRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!containerRef.current || !docRef.current) return;

    const updateScale = () => {
      if (!containerRef.current || !docRef.current) return;
      const containerWidth = containerRef.current.getBoundingClientRect().width;
      const padding = window.innerWidth < 640 ? 32 : 48; // p-4 (32px total) vs sm:p-6 (48px total)
      const availableWidth = containerWidth - padding;
      // US Letter width representation = 800px
      const newScale = Math.min(1, availableWidth / 800);
      setScale(newScale);
      setDocHeight(docRef.current.scrollHeight);
    };

    updateScale();
    window.addEventListener("resize", updateScale);

    const observer = new ResizeObserver(updateScale);
    observer.observe(docRef.current);
    observer.observe(containerRef.current);

    return () => {
      window.removeEventListener("resize", updateScale);
      observer.disconnect();
    };
  }, []);

  return (
    <div className="space-y-3">
      {/* ── Filter / action bar ── */}
      <div className="flex flex-wrap items-center gap-2 rounded-xl border border-border/60 bg-card/70 px-3 py-2 shadow-sm">
        {topBar}
        <div className="ml-auto">
          <Button
            size="sm"
            className="h-8 gap-1.5 text-xs bg-primary text-primary-foreground hover:bg-primary/90"
            onClick={() => printReport(printRef as React.RefObject<HTMLDivElement>, title)}
          >
            <Printer className="h-3.5 w-3.5" />
            <span className="hidden sm:inline">{t("export_pdf")}</span>
            <span className="sm:hidden">PDF</span>
          </Button>
        </div>
      </div>

      {/* ── PDF-style viewer shell ── */}
      <div className="rounded-xl border border-border/60 bg-[#3a3a3a] shadow-xl overflow-hidden">
        {/* Toolbar bar */}
        <div className="flex items-center gap-2 border-b border-white/10 bg-[#2a2a2a] px-4 py-2">
          <FileText className="h-3.5 w-3.5 text-white/50 shrink-0" />
          <span className="text-[11px] text-white/40 font-mono truncate">
            mdd_{title.toLowerCase().replace(/\s+/g, "_")}_report.pdf
          </span>
        </div>

        {/* White US Letter document — scaled dynamically to fit smaller screens */}
        <div
          ref={containerRef}
          className="flex justify-center p-4 sm:p-6 bg-[#3a3a3a] overflow-hidden"
          style={docHeight && scale < 1 ? { height: `${docHeight * scale + (window.innerWidth < 640 ? 32 : 48)}px` } : undefined}
        >
          <div
            ref={docRef}
            className="bg-white shadow-2xl origin-top shrink-0"
            style={{
              width: "800px",
              minHeight: "500px",
              borderRadius: "2px",
              transform: `scale(${scale})`,
            }}
          >
            {/* Document inner padding */}
            <div className="px-8 pt-7 pb-8">
              {/* Report title block */}
              <div className="mb-5 pb-3 border-b border-gray-200">
                <h2 className="text-base font-bold text-gray-900 leading-tight">{title}</h2>
                <p className="text-[9px] text-gray-400 mt-0.5">
                  Generated by MDD · Mayday Dental Staffing ·{" "}
                  {new Date().toLocaleString("en-US", {
                    month: "short",
                    day: "numeric",
                    year: "numeric",
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </p>
              </div>

              {/* Report body (table, badges etc.) */}
              {children}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

/** Table that fits within Letter — fixed layout, wrapped cells, no overflow scroll */
function ReportTable({
  cols,
  rows,
  sortKey,
  sortDir,
  onSort,
}: {
  cols: { key: string; label: string; width?: string }[];
  rows: Record<string, string>[];
  sortKey: string;
  sortDir: SortDir;
  onSort: (k: string) => void;
}) {
  if (rows.length === 0) {
    return (
      <div className="py-10 text-center text-xs text-gray-400">
        No data matches the current filters.
      </div>
    );
  }

  return (
    <div>
      {/* table-fixed so column widths are respected; overflow hidden so no horizontal bar */}
      <table className="w-full border-collapse text-[10px] table-fixed">
        <colgroup>
          {cols.map((c) => (
            <col key={c.key} style={{ width: c.width }} />
          ))}
        </colgroup>
        <thead>
          <tr>
            {cols.map((c) => (
              <Th
                key={c.key}
                label={c.label}
                col={c.key}
                sortKey={sortKey}
                sortDir={sortDir}
                onSort={onSort}
                width={c.width}
              />
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, i) => (
            <tr
              key={i}
              className={`border-b border-gray-100 ${
                i % 2 === 0 ? "bg-white" : "bg-gray-50/60"
              } hover:bg-blue-50/40 transition-colors`}
            >
              {cols.map((c) => (
                <td
                  key={c.key}
                  className="px-2 py-1.5 text-gray-700 align-top break-words"
                >
                  {c.key === "status" || c.key === "docs" ? (
                    <StatusBadge value={row[c.key]} />
                  ) : (
                    row[c.key]
                  )}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
      <p className="pt-2 text-right text-[9px] text-gray-400">
        {rows.length} record{rows.length !== 1 ? "s" : ""}
      </p>
    </div>
  );
}

function StatusBadge({ value }: { value: string }) {
  const color: Record<string, string> = {
    Open:       "bg-emerald-100 text-emerald-700",
    Filled:     "bg-blue-100 text-blue-700",
    Cancelled:  "bg-rose-100 text-rose-700",
    Expired:    "bg-amber-100 text-amber-700",
    Draft:      "bg-gray-100 text-gray-600",
    Active:     "bg-emerald-100 text-emerald-700",
    Inactive:   "bg-gray-100 text-gray-600",
    Succeeded:  "bg-emerald-100 text-emerald-700",
    Failed:     "bg-rose-100 text-rose-700",
    Pending:    "bg-amber-100 text-amber-700",
    Refunded:   "bg-purple-100 text-purple-700",
    Complete:   "bg-emerald-100 text-emerald-700",
    Incomplete: "bg-amber-100 text-amber-700",
  };
  return (
    <span
      className={`badge inline-flex rounded-full px-1.5 py-0.5 text-[9px] font-semibold leading-none ${
        color[value] ?? "bg-gray-100 text-gray-600"
      }`}
    >
      {value}
    </span>
  );
}

function SummaryBadge({ label, value, color }: { label: string; value: string; color: "emerald" | "rose" }) {
  const cls = color === "emerald"
    ? "bg-emerald-50 border-emerald-200 text-emerald-700 dark:bg-emerald-950/30 dark:border-emerald-800 dark:text-emerald-300"
    : "bg-rose-50 border-rose-200 text-rose-700 dark:bg-rose-950/30 dark:border-rose-800 dark:text-rose-300";
  return (
    <div className={`flex items-center gap-2 rounded-lg border px-3 py-1.5 ${cls}`}>
      <span className="text-[10px] font-medium">{label}</span>
      <span className="text-xs font-bold">{value}</span>
    </div>
  );
}

function EmptyState({
  icon: Icon,
  message,
  sub,
}: {
  icon: typeof XCircle;
  message: string;
  sub: string;
}) {
  return (
    <div className="flex flex-col items-center justify-center py-14 text-gray-400">
      <Icon className="h-10 w-10 mb-3 opacity-25" />
      <p className="text-sm font-medium">{message}</p>
      <p className="text-xs mt-1">{sub}</p>
    </div>
  );
}

// ─── Small filter widgets ─────────────────────────────────────────────────────

function PeriodSelect({ value, onChange }: { value: Period; onChange: (p: Period) => void }) {
  const { t } = useTranslation();
  return (
    <Select value={value} onValueChange={(v) => onChange(v as Period)}>
      <SelectTrigger className="h-8 w-36 text-xs">
        <SelectValue placeholder={t("period_all")} />
      </SelectTrigger>
      <SelectContent>
        {(["all","today","this_week","last_week","this_month","this_quarter","this_year"] as Period[]).map((p) => (
          <SelectItem key={p} value={p} className="text-xs">
            {t("period_" + p)}
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  );
}

function DateInput({ label, value, onChange }: { label: string; value: string; onChange: (v: string) => void }) {
  return (
    <label className="flex items-center gap-1 text-xs text-muted-foreground">
      {label}
      <Input
        type="date"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="h-8 w-32 sm:w-36 text-xs"
      />
    </label>
  );
}

function ResetBtn({ onClick }: { onClick: () => void }) {
  const { t } = useTranslation();
  return (
    <Button variant="ghost" size="sm" className="h-8 text-xs gap-1" onClick={onClick}>
      <RotateCcw className="h-3 w-3" />
      <span className="hidden sm:inline">{t("reset_filters")}</span>
    </Button>
  );
}

// ─── Utilities ────────────────────────────────────────────────────────────────

function buildLocationMap() {
  const m: Record<string, (typeof mockLocations)[0]> = {};
  mockLocations.forEach((l) => { m[l.id] = l; });
  return m;
}
