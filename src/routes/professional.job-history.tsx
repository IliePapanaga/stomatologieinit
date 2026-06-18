import { createFileRoute } from "@tanstack/react-router";
import { useAppStore } from "@/lib/store/app-store";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { CheckCircle2, XCircle, Clock3, DollarSign } from "lucide-react";

export const Route = createFileRoute("/professional/job-history")({
  component: HistoryPage,
});

const statusStyles: Record<string, string> = {
  "Checked-In": "border-emerald-500/40 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  Completed: "border-primary/40 bg-primary/10 text-primary",
  "No-Show": "border-rose-500/40 bg-rose-500/10 text-rose-600 dark:text-rose-400",
};
const statusIcon: Record<string, typeof CheckCircle2> = {
  "Checked-In": CheckCircle2,
  Completed: CheckCircle2,
  "No-Show": XCircle,
};

function HistoryPage() {
  const history = useAppStore((s) => s.jobHistory);
  const totalEarnings = history.reduce((s, h) => s + h.earnings, 0);
  const totalHours = history.reduce((s, h) => s + h.hours, 0);

  return (
    <div className="space-y-6 p-6">
      <header>
        <p className="text-xs font-medium uppercase tracking-wider text-primary">Job History</p>
        <h1 className="mt-1 text-2xl font-semibold tracking-tight">Your past shifts</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Full timeline of completed jobs and earnings.
        </p>
      </header>

      <div className="grid gap-3 sm:grid-cols-3">
        <Stat label="Total earned" value={`$${totalEarnings.toLocaleString()}`} icon={DollarSign} />
        <Stat label="Hours worked" value={`${totalHours} hrs`} icon={Clock3} />
        <Stat label="Shifts" value={history.length.toString()} icon={CheckCircle2} />
      </div>

      <Card className="overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow className="bg-muted/40 hover:bg-muted/40">
              <TableHead>Date</TableHead>
              <TableHead>Practice</TableHead>
              <TableHead>Role</TableHead>
              <TableHead className="text-right">Hours</TableHead>
              <TableHead className="text-right">Earnings</TableHead>
              <TableHead>Status</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {history.map((h) => {
              const Icon = statusIcon[h.status];
              return (
                <TableRow key={h.id} className="border-b border-border/60">
                  <TableCell className="text-sm">
                    {new Date(h.date).toLocaleDateString("en-US", {
                      month: "short",
                      day: "numeric",
                      year: "numeric",
                    })}
                  </TableCell>
                  <TableCell className="font-medium">{h.practiceName}</TableCell>
                  <TableCell className="text-sm text-muted-foreground">{h.role}</TableCell>
                  <TableCell className="text-right tabular-nums">{h.hours}</TableCell>
                  <TableCell className="text-right tabular-nums font-medium">
                    ${h.earnings}
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline" className={`gap-1 ${statusStyles[h.status]}`}>
                      <Icon className="h-3 w-3" /> {h.status}
                    </Badge>
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </Card>
    </div>
  );
}

function Stat({ label, value, icon: Icon }: { label: string; value: string; icon: typeof DollarSign }) {
  return (
    <Card className="flex items-center gap-3 p-4">
      <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-primary/30 bg-primary/10 text-primary">
        <Icon className="h-5 w-5" />
      </div>
      <div>
        <p className="text-xs uppercase tracking-wider text-muted-foreground">{label}</p>
        <p className="text-xl font-semibold">{value}</p>
      </div>
    </Card>
  );
}
