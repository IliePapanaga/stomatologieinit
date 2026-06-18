import { useMemo, useState } from "react";
import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { useAppStore, dashboardForRole, type AppRole, type AppUser } from "@/lib/store/app-store";
import { motion } from "motion/react";
import {
  Search,
  UserCog,
  ShieldCheck,
  Building2,
  Stethoscope,
  KeyRound,
  MoreHorizontal,
  Ban,
  Mail,
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
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { toast } from "sonner";
import type { UserRole } from "@/lib/types/mdd";

export const Route = createFileRoute("/admin/users")({
  component: UsersPage,
});

interface AdminUser {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  tenant: string;
  status: "Active" | "Suspended" | "Pending";
  lastSeen: string;
}

const mockUsers: AdminUser[] = [
  { id: "u_001", firstName: "Maya", lastName: "Chen", email: "maya@brightsidedental.com", role: "PracticeOwner", tenant: "Brightside Dental Group", status: "Active", lastSeen: "2m ago" },
  { id: "u_002", firstName: "Amelia", lastName: "Brooks", email: "amelia@example.com", role: "Professional", tenant: "—", status: "Active", lastSeen: "14m ago" },
  { id: "u_003", firstName: "Noah", lastName: "Patel", email: "noah@example.com", role: "Professional", tenant: "—", status: "Pending", lastSeen: "3h ago" },
  { id: "u_004", firstName: "Alex", lastName: "Chen", email: "achen@mdd.health", role: "SystemAdmin", tenant: "MDD HQ", status: "Active", lastSeen: "1m ago" },
  { id: "u_005", firstName: "Sofia", lastName: "Nguyen", email: "sofia@northpoint.dental", role: "PracticeOwner", tenant: "Northpoint Dental", status: "Active", lastSeen: "1d ago" },
  { id: "u_006", firstName: "Liam", lastName: "Okafor", email: "liam@example.com", role: "Professional", tenant: "—", status: "Suspended", lastSeen: "9d ago" },
  { id: "u_007", firstName: "Zara", lastName: "Reyes", email: "zara@example.com", role: "Professional", tenant: "—", status: "Active", lastSeen: "27m ago" },
  { id: "u_008", firstName: "Ethan", lastName: "Kim", email: "ethan@summit.dental", role: "PracticeOwner", tenant: "Summit Dental Care", status: "Active", lastSeen: "4h ago" },
  { id: "u_009", firstName: "Priya", lastName: "Nair", email: "priya@brightsidedental.com", role: "PracticeOwner", tenant: "Brightside Dental Group", status: "Active", lastSeen: "12m ago" },
  { id: "u_010", firstName: "Sam", lastName: "Johnson", email: "sjohnson@mdd.health", role: "SuperAdmin", tenant: "MDD HQ", status: "Active", lastSeen: "just now" },
  { id: "u_011", firstName: "Hana", lastName: "Suzuki", email: "hana@example.com", role: "Professional", tenant: "—", status: "Active", lastSeen: "2h ago" },
  { id: "u_012", firstName: "Marcus", lastName: "Diaz", email: "marcus@coastal.dental", role: "PracticeOwner", tenant: "Coastal Smiles", status: "Pending", lastSeen: "—" },
];

const roleStyles: Record<UserRole, string> = {
  SuperAdmin: "border-rose-500/40 bg-rose-500/10 text-rose-600 dark:text-rose-400",
  SystemAdmin: "border-primary/40 bg-primary/10 text-primary",
  PracticeOwner: "border-emerald-500/40 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  Professional: "border-border bg-muted text-foreground",
};

const roleIcons: Record<UserRole, typeof ShieldCheck> = {
  SuperAdmin: ShieldCheck,
  SystemAdmin: UserCog,
  PracticeOwner: Building2,
  Professional: Stethoscope,
};

const statusStyles: Record<AdminUser["status"], string> = {
  Active: "border-emerald-500/40 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  Suspended: "border-rose-500/40 bg-rose-500/10 text-rose-600 dark:text-rose-400",
  Pending: "border-amber-500/40 bg-amber-500/10 text-amber-600 dark:text-amber-400",
};

function UsersPage() {
  const [q, setQ] = useState("");
  const [role, setRole] = useState<"All" | UserRole>("All");
  const [status, setStatus] = useState<"All" | AdminUser["status"]>("All");
  const [impersonateTarget, setImpersonateTarget] = useState<AdminUser | null>(null);

  const filtered = useMemo(() => {
    const term = q.trim().toLowerCase();
    return mockUsers.filter((u) => {
      if (role !== "All" && u.role !== role) return false;
      if (status !== "All" && u.status !== status) return false;
      if (!term) return true;
      return (
        `${u.firstName} ${u.lastName}`.toLowerCase().includes(term) ||
        u.email.toLowerCase().includes(term) ||
        u.tenant.toLowerCase().includes(term)
      );
    });
  }, [q, role, status]);

  const impersonateAction = useAppStore((s) => s.impersonate);
  const navigate = useNavigate();

  const confirmImpersonate = () => {
    if (!impersonateTarget) return;
    const target = impersonateTarget;
    const targetRole = target.role as AppRole;
    if (targetRole !== "PracticeOwner" && targetRole !== "Professional" && targetRole !== "SuperAdmin") {
      toast.error("Cannot impersonate this role yet");
      setImpersonateTarget(null);
      return;
    }
    const appUser: AppUser = {
      id: target.id,
      firstName: target.firstName,
      lastName: target.lastName,
      email: target.email,
      role: targetRole,
      tenant: target.tenant,
      avatarInitials: `${target.firstName[0]}${target.lastName[0]}`,
    };
    impersonateAction(appUser);
    toast.success("Impersonation session started", {
      description: `Signed in as ${target.firstName} ${target.lastName}. All actions are audited.`,
    });
    setImpersonateTarget(null);
    navigate({ to: dashboardForRole(targetRole), replace: true });
  };

  return (
    <div className="space-y-6 p-6">
      <header className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <p className="text-xs font-medium uppercase tracking-wider text-primary">User Management</p>
          <h1 className="mt-1 text-2xl font-semibold tracking-tight">Users</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            Manage roles, suspend accounts, and start audited impersonation sessions via <code className="rounded bg-muted px-1 py-0.5 text-[11px]">ImpersonateController</code>.
          </p>
        </div>
        <Button className="bg-primary text-primary-foreground hover:bg-primary/90">
          <Mail className="h-4 w-4" /> Invite user
        </Button>
      </header>

      <div className="flex flex-wrap items-center gap-3">
        <div className="relative w-full max-w-xs">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="Search name, email, tenant…"
            className="pl-9"
          />
        </div>
        <Select value={role} onValueChange={(v) => setRole(v as typeof role)}>
          <SelectTrigger className="w-[170px]"><SelectValue /></SelectTrigger>
          <SelectContent>
            <SelectItem value="All">All roles</SelectItem>
            <SelectItem value="SuperAdmin">SuperAdmin</SelectItem>
            <SelectItem value="SystemAdmin">SystemAdmin</SelectItem>
            <SelectItem value="PracticeOwner">PracticeOwner</SelectItem>
            <SelectItem value="Professional">Professional</SelectItem>
          </SelectContent>
        </Select>
        <Select value={status} onValueChange={(v) => setStatus(v as typeof status)}>
          <SelectTrigger className="w-[150px]"><SelectValue /></SelectTrigger>
          <SelectContent>
            <SelectItem value="All">All status</SelectItem>
            <SelectItem value="Active">Active</SelectItem>
            <SelectItem value="Pending">Pending</SelectItem>
            <SelectItem value="Suspended">Suspended</SelectItem>
          </SelectContent>
        </Select>
        <span className="ml-auto text-xs text-muted-foreground">
          {filtered.length} of {mockUsers.length} users
        </span>
      </div>

      <Card className="overflow-hidden border-border/70 shadow-sm">
        <Table>
          <TableHeader>
            <TableRow className="bg-muted/40 hover:bg-muted/40">
              <TableHead>User</TableHead>
              <TableHead>Role</TableHead>
              <TableHead>Tenant</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Last seen</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filtered.map((u, i) => {
              const RoleIcon = roleIcons[u.role];
              const canImpersonate = u.role !== "SuperAdmin" && u.status !== "Suspended";
              return (
                <motion.tr
                  key={u.id}
                  initial={{ opacity: 0, y: 4 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: Math.min(i * 0.02, 0.25), duration: 0.25 }}
                  className="border-b border-border/60 transition hover:bg-muted/40"
                >
                  <TableCell className="py-3">
                    <div className="flex items-center gap-3">
                      <Avatar className="h-9 w-9">
                        <AvatarFallback className="bg-primary text-xs font-semibold text-primary-foreground">
                          {u.firstName[0]}{u.lastName[0]}
                        </AvatarFallback>
                      </Avatar>
                      <div className="min-w-0">
                        <p className="truncate text-sm font-medium">
                          {u.firstName} {u.lastName}
                        </p>
                        <p className="truncate text-[11px] text-muted-foreground">{u.email}</p>
                      </div>
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline" className={`gap-1 ${roleStyles[u.role]}`}>
                      <RoleIcon className="h-3 w-3" /> {u.role}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-sm text-muted-foreground">{u.tenant}</TableCell>
                  <TableCell>
                    <Badge variant="outline" className={statusStyles[u.status]}>
                      {u.status}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-sm text-muted-foreground">{u.lastSeen}</TableCell>
                  <TableCell className="text-right">
                    <div className="flex items-center justify-end gap-2">
                      <Button
                        size="sm"
                        variant="outline"
                        disabled={!canImpersonate}
                        onClick={() => setImpersonateTarget(u)}
                        className="h-8 gap-1.5 border-primary/40 text-primary hover:bg-primary/10 hover:text-primary"
                      >
                        <KeyRound className="h-3.5 w-3.5" /> Impersonate
                      </Button>
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon" className="h-8 w-8">
                            <MoreHorizontal className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end" className="w-48">
                          <DropdownMenuItem><Mail className="mr-2 h-3.5 w-3.5" /> Send email</DropdownMenuItem>
                          <DropdownMenuItem><UserCog className="mr-2 h-3.5 w-3.5" /> Edit role</DropdownMenuItem>
                          <DropdownMenuSeparator />
                          <DropdownMenuItem className="text-destructive focus:text-destructive">
                            <Ban className="mr-2 h-3.5 w-3.5" /> Suspend account
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </div>
                  </TableCell>
                </motion.tr>
              );
            })}
            {filtered.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} className="py-12 text-center text-sm text-muted-foreground">
                  No users match your filters.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </Card>

      <AlertDialog open={!!impersonateTarget} onOpenChange={(o) => !o && setImpersonateTarget(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle className="flex items-center gap-2">
              <KeyRound className="h-4 w-4 text-primary" /> Start impersonation session
            </AlertDialogTitle>
            <AlertDialogDescription asChild>
              <div className="space-y-3">
                <p>
                  You're about to act as{" "}
                  <span className="font-medium text-foreground">
                    {impersonateTarget?.firstName} {impersonateTarget?.lastName}
                  </span>{" "}
                  ({impersonateTarget?.role}). Every action will be recorded in the audit log under your SuperAdmin identity.
                </p>
                <div className="rounded-lg border border-amber-500/30 bg-amber-500/10 p-3 text-xs text-amber-700 dark:text-amber-300">
                  Calls <code className="font-mono">ImpersonateController.start(userId)</code> · session auto-expires in 30 min.
                </div>
              </div>
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={confirmImpersonate} className="bg-primary text-primary-foreground hover:bg-primary/90">
              Start session
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
